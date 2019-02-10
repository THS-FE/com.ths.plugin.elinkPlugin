1、执行build操作时，要在platforms/android/build.gradle   android>defaultConfig下添加以下配置。不然会报错，加载不到对应的so文件
ndk{
        abiFilters "armeabi"
}
2、V4和V7要注意是相同的版本的,目前是26

3、要确定Android目录下是否存在aar中的so文件，如果不存在需要手动拷贝出来。

4、ThsElinkPlugin  修改import cn.com.ths.thsOAOA.MainActivity;//要换成对应的应用包名