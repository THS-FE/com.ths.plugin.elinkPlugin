var exec = require('cordova/exec');

module.exports = {
/**
 * 【success】 ： 成功回调 
【error 】 ： 失败回调 
【ThsElinkPlugin】 ： 服务名（必须和 plugin.xml定义的服务名保持一致） 
【attach】 ： Action 动作名。(底层实现根据Action字符串调用不同的方法) 
【arg0】： 参数，必须是数组。 可以是json的数据：[{“key”:”value”,”key”:”value”}] 
————— 也可以是[“value1”,”value2”,”value3”,…]
调用事例：cordova.plugins.ThsElinkPlugin.attach(arg0, success, error)
 */
//用于sdk的初始化
attach:function(arg0, success, error) {
    cordova.exec(success, error, "ThsElinkPlugin", "attach", arg0);
},
//初始化，设置服务器IP、通信端口和HTTP端口
init:function(arg0, success, error) {
    cordova.exec(success, error, "ThsElinkPlugin", "init", arg0);
},
//登录 userName,password
login:function(arg0, success, error) {
    cordova.exec(success, error, "ThsElinkPlugin", "login", arg0);
},
//退出登录当前账号
loginOut:function(arg0, success, error) {
    cordova.exec(success, error, "ThsElinkPlugin", "loginOut", arg0);
},
////根据人员名称，从本地查询人员ID
//thsElinkPlugin.prototype.getUserIdByName = function(arg0, success, error) {
//    exec(success, error, "ThsElinkPlugin", "getUserIdByName", arg0);
//};
////从服务器模糊查询人员信息
//thsElinkPlugin.prototype.queryUserFromServer = function(arg0, success, error) {
//    exec(success, error, "ThsElinkPlugin", "queryUserFromServer", arg0);
//};
//打开聊天页面
startChart:function(arg0, success, error) {
    cordova.exec(success, error, "ThsElinkPlugin", "startChart", arg0);
},
//打开消息中心
startMessageCenter:function(arg0, success, error) {
    cordova.exec(success, error, "ThsElinkPlugin", "startMessageCenter", arg0);
},
//打开组织结构
startOrgazion:function(arg0, success, error) {
    cordova.exec(success, error, "ThsElinkPlugin", "startOrgazion", arg0);
},
//打开联系人
startContact:function(arg0, success, error) {
    cordova.exec(success, error, "ThsElinkPlugin", "startContact", arg0);
},
//展示主页面，需要传递参数，[true,true,true] isMsg 展示信息中心 isOrg 展示组织机构 isCta 展示联系人
showMainPage:function(arg0, success, error) {
    cordova.exec(success, error, "ThsElinkPlugin", "showMainPage", arg0);
},
//展示讨论组，groupType： 0:临时讨论组， 1:工作组Elink.showGroupList(context,groupType)

showGroupList:function(arg0, success, error) {
    cordova.exec(success, error, "ThsElinkPlugin", "showGroupList", arg0);
},
//获取消息列表
getMessageList:function(arg0, success, error) {
    exec(success, error, "ThsElinkPlugin", "getMessageList", arg0);
},
//Java调取js返回消息列表数据
getMessageListDataInAndroidCallback:function(data) {
  //  data = JSON.stringify(data)
  //  console.log('ElinkPlugin:getMessageListDataInAndroidCallback: ' + data)
   cordova.fireDocumentEvent('elink.getMessageListData', data)
},
//打开文件助手　　无参数
showFileHelper :function(arg0, success, error) {
    exec(success, error, "ThsElinkPlugin", "showFileHelper", arg0);
},
//打开指定群聊，传递消息ID
startGroupChat:function(arg0, success, error) {
    exec(success, error, "ThsElinkPlugin", "startGroupChat", arg0);
},
//展示系统消息　无参数
showSystemMsg:function(arg0, success, error) {
    exec(success, error, "ThsElinkPlugin", "showSystemMsg", arg0);
},
//设置自定消息已经，传递消息ID
setSysMsgReaded:function(arg0, success, error) {
    exec(success, error, "ThsElinkPlugin", "setSysMsgReaded", arg0);
},
//收到消息时进行回调
receiveMessageInAndroidCallback:function (data) {
  //data = JSON.stringify(data)
  //console.log('ElinkPlugin:receiveMessageInAndroidCallback: ' + data)
  cordova.fireDocumentEvent('elink.receiveMessage', data);
}
,
//打开对应的信息界面
showMsgDetail:function(arg0, success, error){
    exec(success, error, "ThsElinkPlugin", "showMsgDetail", arg0);
},
//注册系统消息处理机制
registerSystemMsgHandler:function(arg0, success, error){
    exec(success, error, "ThsElinkPlugin", "registerSystemMsgHandler", arg0);
},
//获取当前用户ID
registerSystemMsgHandler:function(arg0, success, error){
    exec(success, error, "ThsElinkPlugin", "registerSystemMsgHandler", arg0);
},
//获取登录个人ID
getSelfId:function(arg0, success, error){
    exec(success, error, "ThsElinkPlugin", "getSelfId", arg0);
},
//获取头像基本路径
getHeadImgPath:function(arg0, success, error){
    exec(success, error, "ThsElinkPlugin", "getHeadImgPath", arg0);
}
};
