package cn.junechiu.firupload

import groovy.json.JsonOutput
import groovy.json.JsonSlurper

/**
 * Created by android on 2018/2/8.
 */
class Test {
    static void main(String[] args) {
        httpPost()
//        File file = new File('/Users/android/work/qianlongC/app/build/outputs/apk/release/app-release.apk')
//        if (file.exists()){
//            print('ssss')
//        }
    }

    def static httpGet() {
        def url = 'https://www.baidu.com/'
        HttpClient.builder().requestUrl(url)
                .timeout(60000)
                .httpMethod("GET")
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

    def static httpPost() {
        def url = "http://api.fir.im/apps"
        def type = "android"
        def appid = "5a7bfb7b548b7a1b9634ae45"
        def apiToken = "0683987d8590da79d1408bb90ac85d12"
        def bundleId = "info.qianlong.meizi"

        HttpClient.builder().requestUrl(url)
                .httpMethod("POST")
                .addParams("type", type)
                .addParams("api_token", apiToken)
                .addParams("bundle_id", bundleId)
                .callback(new HttpCallback() {
            @Override
            void onSuccess(String response) {
                def data = new JsonSlurper().parseText(response)
//                print(data.cert.icon.upload_url)
                uploadIcon(data.cert.icon)
                uploadBinary(data.cert.binary)
            }

            @Override
            void onError(int code, Exception e) {
                print(String.valueOf(code) + e)
            }
        }).build().request()
    }

    def static httpPostP() {
        def url = "http://api.fir.im/apps"
        def type = "android"
        def appid = "5a7bfb7b548b7a1b9634ae45"
        def apiToken = "0683987d8590da79d1408bb90ac85d12"
        def bundleId = "info.qianlong.meizi"

        HttpClient.builder().requestUrl(url)
                .httpMethod("POST")
                .addParams("type", type)
                .addParams("api_token", apiToken)
                .addParams("bundle_id", bundleId)
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

    static void uploadIcon(def data) {
        print(data.upload_url)
        def url = data.upload_url
        def key = data.key
        def token = data.token
        def iconpath = "/Users/android/work/IkickerLottery/LotteryAndroid/app/src/main/res/mipmap-xxhdpi/ic_launcher.png"
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

    static void uploadBinary(def data) {
        def url = data.upload_url
        def key = data.key
        def token = data.token
        def changelog = "更新"
        def appname = "测试"
        def appversion = "1.0"
        int buildnum = "1"
        def apkpath = "/Users/android/work/qianlongC/app/release/app-release.apk"

        if (apkpath == null || apkpath.equals('')) {
            print('apkpath is invalid')
            return
        }
        if (appname == null || appname.equals('')) {
            print('appname is invalid')
            return
        }
        if (appversion == null || appversion.equals('')) {
            print('appversion is invalid')
            return
        }
        HttpClient.builder().requestUrl(url)
                .httpMethod("POST")
                .addParams("key", key)
                .addParams("token", token)
                .addParams("x:name", appname)
                .addParams("x:version", appversion)
                .addParams("x:changelog", changelog)
                .addParams("x:build", buildnum)
                .addParams("file", new File(apkpath))
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
