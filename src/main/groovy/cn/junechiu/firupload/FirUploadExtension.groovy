package cn.junechiu.firupload

/**
 * Created by android on 2018/2/5.
 * 创建参数实体
 * 接收外部参数
 */
class FirUploadExtension {
    //fir api参数
    String apiToken    //必须参数
    //上传需要的参数
    String bundleId   //包名字
    String appName    //app名字      必须参数
    String versionName    //版本号
    int buildNum   //build号
    String iconPath   //app图标      必须参数
    String apkPath    //apk路径
    String changelog  //日志
    //插件参数
    boolean autoUpload  //是否自动上传
}
