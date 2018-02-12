package cn.junechiu.firupload

import groovy.json.JsonSlurper
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * Created by android on 2018/2/5.
 */
class UploadTask extends DefaultTask {

    @TaskAction
    def uploadApk() {
        // 取得外部参数 For android application.
        if (project.android.hasProperty("applicationVariants")) {
            def applicationId = ''
            def variantName = ''
            def apkPath = ''
            def versionName = ''
            def versionCode = 1
            project.android.applicationVariants.all { variant ->
                variant.outputs.each { variantOutput ->
                    def outputFile = variantOutput.outputFile
                    if (outputFile != null && outputFile.name.endsWith('.apk')) {
                        if (variant.buildType.name == "release") {
                            applicationId = variant.applicationId
                            variantName = variantOutput.name.capitalize() //Release
                            apkPath = outputFile
                            versionName = variant.generateBuildConfig.versionName
                            versionCode = variant.generateBuildConfig.versionCode
                            getUploadInfo(applicationId, apkPath, versionName, versionCode)
                        }
                    }
                }
            }
        }
    }

    void getUploadInfo(def applicationId, def apkPath, def versionName, def versionCode) {
        def url = 'http://api.fir.im/apps'
        def type = 'android'
        def apiToken = project.uploadArgs.apiToken
        def bundleId = project.uploadArgs.bundleId
        if (apiToken == null || apiToken.equals("")) {
            print('apiToken is invalid')
            return
        }
        if (bundleId == null || bundleId.equals("")) {
            bundleId = applicationId
        }

        HttpClient.builder().requestUrl(url)
                .httpMethod("POST")
                .addParams("type", type)
                .addParams("api_token", apiToken)
                .addParams("bundle_id", bundleId)
                .callback(new HttpCallback() {
            @Override
            void onSuccess(String response) {
                if (response != null && !"".equals(response)) {
                    def data = new JsonSlurper().parseText(response)
                    uploadIcon(data.cert.icon)
                    uploadBinary(data.cert.binary, apkPath, versionName, versionCode)
                }
            }

            @Override
            void onError(int code, Exception e) {
                print(String.valueOf(code) + e)
            }
        }).build().request()
    }

    void uploadIcon(def data) {
        def url = data.upload_url
        def key = data.key
        def token = data.token
        def iconpath = project.uploadArgs.iconPath
        if (iconpath == null || iconpath.equals('')) {
            print('iconpath is invalid')
            return
        }
        HttpClient.builder().requestUrl(url)
                .httpMethod("POST")
                .addParams("key", key)
                .addParams("token", token)
                .addParams("file", new File(iconpath))
                .callback(new HttpCallback() {
            @Override
            void onSuccess(String response) {
                print(response)
            }

            @Override
            void onError(int code, Exception e) {
                print(String.valueOf(code) + e)
            }
        }).build().request()
    }

    void uploadBinary(def data, def apkPath, def versionName, def versionCode) {
        def url = data.upload_url
        def key = data.key
        def token = data.token
        def changelog = "更新于:" + new SimpleDateFormat("yyy-MM-dd HH:mm:ss")
                .format(new Date())
        def appname = project.uploadArgs.appName
        def appversion = project.uploadArgs.versionName
        def buildnum = project.uploadArgs.buildNum
        def apkpath = project.uploadArgs.apkPath

        if (appname == null || appname.equals('')) {
            print('appname is invalid')
            return
        }
        if (apkpath == null || apkpath.equals('')) {
            apkpath = apkPath
        }
        if (appversion == null || appversion.equals('')) {
            appversion = versionName
        }
        if (buildnum == 0) {
            buildnum = versionCode
        }
        HttpClient.builder().requestUrl(url)
                .httpMethod("POST")
                .addParams("key", key.toString())
                .addParams("token", token.toString())
                .addParams("x:changelog", changelog.toString())
                .addParams("x:name", appname.toString())
                .addParams("x:version", appversion.toString())
                .addParams("x:build", buildnum.toString())
                .addParams("file", new File(apkpath.toString()))
                .callback(new HttpCallback() {
            @Override
            void onSuccess(String response) {
                print(response)
            }

            @Override
            void onError(int code, Exception e) {
                print(String.valueOf(code) + e)
            }
        }).build().request()
    }
}



