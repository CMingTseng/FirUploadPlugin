package cn.junechiu.firupload

/**
 * Created by android on 2018/2/7.
 */
class HttpClientBuilder {

    public static final String HTTPMETHOD_GET = "GET"

    public static final String HTTPMETHOD_POST = "POST"

    public static final String HTTPMETHOD_PUT = "PUT"

    public static final String BOUNDARY = '---------firUploader'

    public static final int HTTPSUCCESS = 200

    public static final int TIMEOUT = 60000

    private String requestUrl                    //请求地址

    private Map<String, Object> requestParams = new HashMap<>()    //参数

    private String httpMethod                   //请求方法

    private HttpCallback callback       //结果回调

    private boolean useCaches

    private int timeout

    private Map<String, String> requestProperty

    HttpClientBuilder() {
    }

    HttpClientBuilder requestUrl(String mRequestUrl) {
        this.requestUrl = mRequestUrl
        return this
    }

    HttpClientBuilder requestParams(Map<String, Object> mRequestParams) {
        this.requestParams.clear()
        this.requestParams.putAll(mRequestParams)
        return this
    }

    HttpClientBuilder addParams(String key, Object value) {
        this.requestParams.put(key, value)
        return this
    }

    HttpClientBuilder httpMethod(String mHttpMethod) {
        this.httpMethod = mHttpMethod
        return this
    }

    HttpClientBuilder callback(HttpCallback mCallback) {
        this.callback = mCallback
        return this
    }

    HttpClientBuilder useCaches(boolean mUseCaches) {
        this.useCaches = mUseCaches
        return this
    }

    HttpClientBuilder timeout(int mTimeout) {
        this.timeout = mTimeout
        return this
    }

    HttpClientBuilder requestProperty(Map<String, String> mRequestProperty) {
        this.requestProperty = mRequestProperty
        return this
    }

    HttpClient build() {
        return new HttpClient(requestUrl,
                requestParams,
                httpMethod,
                callback,
                useCaches,
                timeout,
                requestProperty)
    }
}
