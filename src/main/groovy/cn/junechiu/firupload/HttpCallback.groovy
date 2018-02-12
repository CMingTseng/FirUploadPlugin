package cn.junechiu.firupload

/**
 * Created by android on 2018/2/7.
 */
interface HttpCallback {
    void onSuccess(String response)

    void onError(int code, Exception e)
}
