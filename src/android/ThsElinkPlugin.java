package com.ths.plugin.elinkPlugin;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.hxyl.Elink;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import cn.com.sc.mobile.affairs.MainActivity;//要换成对应的应用包名
import elink.mobile.im.bean.MesBean;

/**
 * elink即时消息插件
 * This class echoes a string called from JavaScript.
 * 1、	Elink.registerSystemMsgHandler(Activity.class, sourceType);
 注册系统消息处理activity
 对应sourceType类型的系统消息通知，点击打开注册的activity
 2、	获取数据
 对应的activity通过intent获取通知信息
 Intent intent = getIntent();
 String msgId = intent.getStringExtra("messageId");
 byte type = intent.getByteExtra("messageType", (byte) -1);
 byte sourceType = intent.getByteExtra("messgeSource", (byte) -1);
 long sendTime = intent.getLongExtra("sendTime", -1);
 String title = intent.getStringExtra("title");
 String content = intent.getStringExtra("content");

 3、	Elink.setSysMsgReaded(msgId);
 打开activity后调用此方法通知消息已读，以更新消息未读数
 1、注册广播接收器，监听会话列表变化通知
 registerReceiver(receiver, new IntentFilter(Elink.MSG_LIST_ACTION));

 BroadcastReceiver receiver = new BroadcastReceiver() {
@Override
public void onReceive(Context context, Intent intent) {
getData();
}
};

 2、获取最新会话列表
 private void getData(){
 //获取最新消息列表，异步
 Elink.getMessageList(new Elink.IMsgCallback() {
@Override
public void onSuccess(ArrayList<MesBean> mesList) {
//会话列表
refresh(mesList);
}
@Override
public void onFail(String msg) {
Log.e("MesListActivity", msg);
}
});
 }

 3、会话类型及打开对应会话页面
 MesBean bean = dataList.get(position);
 switch (bean.getType()) {
 case 0:
 //文件助手
 Elink.showFileHelper(this);
 break;
 case 1:
 //点对点
 Elink.startChart(this,bean.getId(), bean.getName());
 break;
 case 2:
 //群组
 Elink.startGroupChat(this, bean.getId());
 break;
 case 3:
 //系统消息
 Elink.showSystemMsg(this);
 break;
 case 4:
 //新闻消息
 Elink.showNewsMsg(this, bean.getId());
 break;
 default:
 break;
 }

 具体查看demo中 MesListActivity 类
 */
public class ThsElinkPlugin extends CordovaPlugin implements Elink.IEventHandler {
     private Activity activity;//上下文对象
    final int NET_NOTFOUND = 0xFF01;//网络没有发现
    final int NET_DISCONNECT = 0xFF02;//断开链接
    final int NET_HEARTBEAT_TIMEOUT = 0xFF03;//心跳发送超时
    private String tempName="";//临时保存人员名字
    private CallbackContext tempCallbackContext;//临时的回调对象
    private static ThsElinkPlugin instance;
    public ThsElinkPlugin() {
        instance = this;
    }
    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        activity=cordova.getActivity();
        Elink.registerHandler(this);
        activity.registerReceiver(receiver,new IntentFilter(Elink.SYSTEM_MSG_ACTION));
        activity.registerReceiver(msgChangeeceiver, new IntentFilter(Elink.MSG_LIST_ACTION));
    }

    @Override
    public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
        if (action.equals("attach")) {       //用于sdk的初始化
            this.attach(callbackContext);
            return true;
        }else if(action.equals("init")){ //初始化，设置服务器IP、通信端口和HTTP端口,例如：218.241.204.175 9000  9090
            String host = args.getString(0);
            int portValue = args.getInt(1);
            int httpPort = args.getInt(2);
            this.init(host,portValue,httpPort,callbackContext);
        }else if(action.equals("login")){//登录功能 用户名、密码 userName,password
            String userName = args.getString(0);
            String password = args.getString(1);
            this.login(userName,password,callbackContext);
        }else if(action.equals("loginOut")){//退出登录当前账号
            this.loginOut(callbackContext);
        }else if(action.equals("startChart")){//根据人员名称，打开单聊界面
            String name = args.getString(0);
            //  String name = "安建华";
            this.startChart(name,callbackContext);
        }else if(action.equals("startMessageCenter")){////打开消息中心
            this.startMessageCenter(callbackContext);
        }else if(action.equals("startOrgazion")){//打开组织结构
            this.startOrgazion(callbackContext);
        }else if(action.equals("startContact")){//打开联系人
            this.startContact(callbackContext);
        }else if(action.equals("showMainPage")){//展示主界面
            boolean isMsg=args.getBoolean(0);
            boolean isOrg=args.getBoolean(1);
            boolean isCta=args.getBoolean(2);
            this.showMainPage(isMsg,isOrg,isCta,callbackContext);
        }else if(action.equals("getMessageList")){//获取消息列表
          callbackContext.success("success");
          Elink.getMessageList(new Elink.IMsgCallback() {
            @Override
            public void onSuccess(ArrayList<MesBean> mesList) {
              Log.e("mesList",mesList.toString());
              JSONArray jsonArray=list2Json(mesList);
              String format = "cordova.plugins.ThsElinkPlugin.getMessageListDataInAndroidCallback(%s);";
              final String js = String.format(format, jsonArray.toString());
              activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                  instance.webView.loadUrl("javascript:" + js);
                }
              });
            }
            @Override
            public void onFail(String msg) {
              callbackContext.error(msg);
              Log.e("MesListActivity", msg);
            }
          });
        }else if(action.equals("showFileHelper")){//打开文件助手　
            Elink.showFileHelper(activity);
            callbackContext.success("success");
        }else if(action.equals("startGroupChat")){//打开指定群聊，传递消息ID
            String msgId = args.getString(0);
            Elink.startGroupChat(activity,msgId);
            callbackContext.success("success");
        }else if(action.equals("showGroupList")){//展示讨论组，groupType： 0:临时讨论组， 1:工作组Elink
            int groupType = args.getInt(0);
            Elink.showGroupList(activity,groupType);
            callbackContext.success("success");
        }else if(action.equals("showSystemMsg")){//展示系统消息　无参数
            Elink.showSystemMsg(activity);
            callbackContext.success("success");
        }else if(action.equals("registerSystemMsgHandler")){//注册点击哪些类型的通知栏消息可打开对应的应用activity，目前指定主界面
//            int portValue = args.getInt(1);
//            int portValue = args.getInt(1);
            //组装数据
            int[] sourceTypes=new int[args.length()];
            for (int i=0;i<args.length();i++){
                sourceTypes[i]=args.getInt(i);
            }
            Elink.registerSystemMsgHandler(MainActivity.class,sourceTypes);
            callbackContext.success("success");
        }else if(action.equals("setSysMsgReaded")){//设置指定消息已读
            String msgId = args.getString(0);
            Elink.setSysMsgReaded(msgId);
            callbackContext.success("success");
        }else if(action.equals("showMsgDetail")){//打开对应的消息界面
            int msgType = args.getInt(0);
            String id = args.getString(1);
            String name = args.getString(2);
            switch (msgType) {
                case 0:
                    Elink.showFileHelper(activity);
                    break;
                case 1:
                    Elink.startChart(activity,id, name);
                    break;
                case 2:
                    Elink.startGroupChat(activity, id);
                    break;
                case 3:
                    Elink.showSystemMsg(activity);
                    break;
                case 4:
                    Elink.showNewsMsg(activity, id);
                    break;
                default:
                    break;
            }
            callbackContext.success("success");
         /**
           * 获取个人的用户id
           */
        }else if(action.equals("getSelfId")){
            UUID userID= Elink.getSelfId();
            callbackContext.success(userID.toString());
            /**
             * 获取头像基础路径
             */
          }else if(action.equals("getHeadImgPath")){
            //activity.getApplication().getApplicationContext()
            String headImgPath=activity.getApplication().getApplicationContext().getExternalFilesDir("headImg").getAbsolutePath() + "/";
            callbackContext.success(headImgPath);
          }


        return false;
    }

    /**
     * 用于sdk的初始化
     * @param callbackContext 方法调用回调
     */
    private void attach(CallbackContext callbackContext) {
        Elink.attach(activity);
        callbackContext.success("success");
    }
    /**
     * 初始化，设置服务器IP、通信端口和HTTP端口,例如：218.241.204.175 9000  9090
     * @param callbackContext 方法调用回调
     */
    private void init(String host,int portValue,int  httpPort, CallbackContext callbackContext) {
        Elink.init(host,portValue,httpPort);
        if (host != null && host.length() > 0&&portValue>0&&httpPort>0) {
            callbackContext.success("success");
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    /**
     * 登录系统
     * @param userName
     * @param password
     * @param callbackContext
     */
    private void login(String userName,String password, CallbackContext callbackContext) {
        Elink.login(userName,password);
        if (userName != null && userName.length() > 0&&password != null && password.length() > 0) {
            callbackContext.success("success");
        } else {
            callbackContext.error("Expected one non-empty string argument.");
        }
    }

    /**
     * 退出即时通信
     * @param callbackContext
     */
    private void loginOut(CallbackContext callbackContext){
        Elink.loginOut();
        callbackContext.success("success");
    }

    /**
     * 打开消息中心
     * @param callbackContext
     */
    private void startMessageCenter(CallbackContext callbackContext){
        Elink.startMessageCenter(activity);
        callbackContext.success("success");
    }
    /**
     * 打开组织结构
     * @param callbackContext
     */
    private void startOrgazion(CallbackContext callbackContext){
        Elink.startOrgazion(activity);
        callbackContext.success("success");
    }

    /**
     * 打开联系人中心
     * @param callbackContext
     */
    private void startContact(CallbackContext callbackContext){
        Elink.startContact(activity);
        callbackContext.success("success");
    }
    /**
     * 展示主界面
     * @param isMsg 展示信息中心
     * @param isOrg 展示组织机构
     * @param isCta 展示联系人
     * @param callbackContext
     */
    private void showMainPage(boolean isMsg,boolean isOrg,boolean isCta, CallbackContext callbackContext){
        ArrayList tabList = new ArrayList();
        if(isMsg){
            tabList.add(Elink.TAB_MESSAGE);
        }
        if(isOrg){
            tabList.add(Elink.TAB_ORGNIZATION);
        }
        if(isCta){
            tabList.add(Elink.TAB_CONTACT);
        }
        Elink.showMainPage(activity,tabList);
        callbackContext.success("success");
    }

    private void getUserIdByName(String name,CallbackContext callbackContext){
        UUID uid = Elink.getUserIdByName(name);
        callbackContext.success("success");
    }

    @Override
    public void handlerEvent(int command, Object data) {

        switch (command){
            case NET_NOTFOUND:
                Toast.makeText(activity,(String) data,Toast.LENGTH_SHORT).show();
                break;
            case NET_DISCONNECT:
               // Toast.makeText(activity,(String) data,Toast.LENGTH_SHORT).show();
                break;
            case NET_HEARTBEAT_TIMEOUT:
               // Toast.makeText(activity,(String) data,Toast.LENGTH_SHORT).show();
                break;
            case 0x0102:
                Log.e("Xup ",this+" command:"+Integer.toHexString(command)+",data:"+data);
                if(data == null){
                    //startActivity(new Intent(activity,SecondActivity.class));
                }else{
                    String msg = (String) data;
                    Toast.makeText(activity,msg,Toast.LENGTH_SHORT).show();
                }
                break;
            case 0x0912:
                //查询人员信息返回
                startChart(tempName,tempCallbackContext);
                break;
          case -0x0202: //查询人员信息返回
            Bundle bundle = (Bundle) data;
            String userName = bundle.getString("userName");
            UUID uid = (UUID) bundle.getSerializable("userId");
            Elink.startChart(activity,uid,userName);
            break;
            default:
                break;
        }
    }

    /**
     * 启动单聊页面
     * @param name
     */
    private void startChart(String name,CallbackContext callbackContext){
        tempName=name;
        tempCallbackContext=callbackContext;
        UUID uid = Elink.getUserIdByName(name);
        if(uid != null){
            Elink.startChart(activity,uid,name);
            callbackContext.success("success");
        }else{
            //Elink.queryUserFromServer(name);
            Elink.queryUserFromServerByLoginName(name);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Elink.unRegisterHandler(this);
        activity.unregisterReceiver(receiver);
        activity.unregisterReceiver(msgChangeeceiver);
        instance = null;
        activity=null;
    }
    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // byte  消息来源  99 messgeSource
            // byte 消息类型 messageType
            // long 发送时间 sendTime
            // String 标题 title
            // String 内容 content
            String title = intent.getStringExtra("title");
            String content = intent.getStringExtra("content");
            byte messgeSource = intent.getByteExtra("messgeSource", (byte) 0);
            byte messageType = intent.getByteExtra("messgeSource", (byte) 0);
            long sendTime = intent.getLongExtra("messgeSource", 0L);
            Toast.makeText(activity,"receiver title:"+title,Toast.LENGTH_SHORT).show();
            Log.e("BroadcastReceiver","receiver title:"+title+",content:"+content);
        }
    };

    /**
     * 有新的消息收到
     * @param message
     */
     void transmitMessageReceive(String message) {
        if (instance == null) {
            return;
        }
         JSONObject data = null;
         try {
             data = new JSONObject(message);
         } catch (JSONException e) {
             e.printStackTrace();
         }
         String format = "cordova.plugins.ThsElinkPlugin.receiveMessageInAndroidCallback(%s);";
        final String js = String.format(format, data.toString());
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instance.webView.loadUrl("javascript:" + js);
            }
        });
    }
    //收到新的消息，广播
    BroadcastReceiver msgChangeeceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            transmitMessageReceive("{}");//elinkSDK暂时无法获取消息，暂时传递空
        }
    };

  /**
   * 列表数组转json数组对象
   * @param mesList
   * @return
   */
  public  JSONArray list2Json(ArrayList<MesBean> mesList){
    /**
     * private String id;
     private String name;
     private String context;
     private int img;
     private String picPath;
     private int gender;
     private byte state;
     private byte loginMode;
     private int type;
     private String time;
     private long millisTime;
     private int unReadCount;
     private boolean hasTel = false;
     */
    JSONArray json = new JSONArray();
    for(Object pLog : mesList){
      JSONObject jo = new JSONObject();
//      jo.put("id", pLog.get());
//      jo.put("name", pLog.getBeginTime());
//      jo.put("name", pLog.getBeginTime());
//      jo.put("name", pLog.getBeginTime());
//      jo.put("name", pLog.getBeginTime());
//      json.put(jo);
      //获取参数类
      Class cls = pLog.getClass();
      //将参数类转换为对应属性数量的Field类型数组（即该类有多少个属性字段 N 转换后的数组长度即为 N）
      Field[] fields = cls.getDeclaredFields();
      for(int i = 0;i < fields.length; i ++){
        Field f = fields[i];
        f.setAccessible(true);
        f.getName();
        try {
          jo.put(f.getName(), f.get(pLog));
        }catch (Exception e){

        }
      }
      json.put(jo);
    }
    return json;
  }
}
