### firuploadPlugin

fir.im测试分发平台 apk上传gradle插件

如果使用python脚本上传可参考:
https://github.com/abc20899/pythonScript/tree/master/fir_upload

#### use:

在项目根目录下build.gradle下添加

```
repositories {
    google()
    jcenter()

    //本地测试仓库
    maven {
        url uri('../repo')
    }
}

dependencies {
	//远程仓库
	//classpath 'cn.junechiu.firupload:1.0.0'
	
	//本地测试仓库
	classpath group: 'cn.junechiu.firupload',
	          name: 'firupload',
	          version: '1.0.0'
}
```

在app module下的build.gradle下(根节点)添加

```
apply plugin: 'FirUpload'  //./gradlew uploadApk
......
......
uploadArgs {
    //必要参数
    apiToken = 'your fir apiToken'
    iconPath = getBuildDir().parent + "/src/main/res/mipmap-xxhdpi/ic_launcher.png"
    appName = '测试'
    
   //可选参数
   bundleId
   versionName
   buildNum
   apkPath
   changelog
}
```
最后执行

```
./gradlew uploadApk
//会先执行assembleRelease任务，然后上传apk
//预先配置好
buildTypes {
   signingConfig   //签名项
}
```

