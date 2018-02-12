package cn.junechiu.firupload

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by android on 2018/2/7.
 */
class HttpClient {

    private String requestUrl                    //请求地址

    private Map<String, Object> requestParams    //参数

    private String httpMethod                   //请求方法

    private HttpCallback callback               //结果回调

    private boolean useCaches

    private int timeout

    private Map<String, String> requestProperty

    private ExecutorService threadPool = Executors.newFixedThreadPool(3)

    HttpClient(String requestUrl,
               Map<String, Object> requestParams,
               String httpMethod,
               HttpCallback callback,
               boolean useCaches,
               int timeout,
               Map<String, String> requestProperty) {
        this.requestUrl = requestUrl
        this.requestParams = requestParams
        this.httpMethod = httpMethod
        this.callback = callback
        this.useCaches = useCaches
        this.timeout = timeout
        this.requestProperty = requestProperty
    }

    //初始化连接
    private HttpURLConnection connection() {
        HttpURLConnection conn = null
        try {
            URL url = new URL(requestUrl)
            conn = (HttpURLConnection) url.openConnection()
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true)
            conn.setDoInput(true)
            conn.setUseCaches(useCaches)
            conn.setConnectTimeout(timeout == 0 ? HttpClientBuilder.TIMEOUT : timeout) //连接超时为60秒
            conn.setReadTimeout(timeout == 0 ? HttpClientBuilder.TIMEOUT : timeout)
            conn.setRequestMethod(httpMethod)
            if (requestProperty == null) {
                conn.setRequestProperty("connection", "Keep-Alive")
                conn.setRequestProperty("Charset", "UTF-8")
            } else {
                Set<String> keySet = requestProperty.keySet()
                for (Iterator<String> it = keySet.iterator(); it.hasNext();) {
                    String name = it.next()
                    conn.setRequestProperty(name, requestProperty.get(name))
                }
            }
        } catch (Exception e) {
            e.printStackTrace()
        }
        return conn
    }

    //进行请求
    void request() {
        if (httpMethod.equals(HttpClientBuilder.HTTPMETHOD_POST)) {
            threadPool.execute(doPost())
        } else if (httpMethod.equals(HttpClientBuilder.HTTPMETHOD_GET)) {
            threadPool.execute(doGet())
        }
    }

    Runnable doGet() {
        return new Runnable() {
            @Override
            void run() {
                HttpURLConnection httpURLConnection = connection()
                try {
                    print("doGet")
                    httpURLConnection.connect()
                    //获取网络输入流
                    InputStream is = httpURLConnection.getInputStream()
                    BufferedReader bf = new BufferedReader(new InputStreamReader(is, 'UTF-8'))
                    //将字节流转换为字符流
                    StringBuffer buffer = new StringBuffer()
                    String line = ""
                    while ((line = bf.readLine()) != null) {
                        buffer.append(line)
                    }
                    bf.close()
                    is.close()
                    if (callback != null) {
                        callback.onSuccess(buffer.toString())
                    }
                } catch (SocketTimeoutException e) {
                    if (callback != null) {
                        callback.onError(-1, e)
                    }
                } catch (MalformedURLException e) {
                    if (callback != null) {
                        callback.onError(-1, e)
                    }
                } catch (IOException e) {
                    if (callback != null) {
                        callback.onError(-1, e)
                    }
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect() //关闭连接
                        threadPool.shutdown()
                    }
                }
            }
        }
    }

    Runnable doPost() {
        return new Runnable() {
            @Override
            void run() {
                HttpURLConnection httpURLConnection = connection()
                try {
                    print("doPost")
                    httpURLConnection.setRequestProperty("Content-Type",
                            "multipart/form-data; boundary=" + HttpClientBuilder.BOUNDARY)
                    httpURLConnection.connect()
                    DataOutputStream ds = new DataOutputStream(httpURLConnection.getOutputStream())
                    if (requestParams != null && requestParams.size() > 0) {
                        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
                            String key = entry.getKey().toString()
                            //如果是文件
                            if (entry.getValue() instanceof File) {
                                File file = (File) entry.getValue()
                                ds.writeBytes("--" + HttpClientBuilder.BOUNDARY + "\r\n")
                                ds.writeBytes("Content-Disposition: form-data; name=\"" + key
                                        + "\"; filename=\"" + file.getName() + "\"\r\n")
                                ds.writeBytes("Content-Type:application/octet-stream" + "\r\n")
                                ds.writeBytes("\r\n")
                                ds.write(getBytes(file))
                                ds.writeBytes("\r\n")
                            } else {
                                String value = entry.getValue().toString()
                                ds.writeBytes("--" + HttpClientBuilder.BOUNDARY + "\r\n")
                                ds.writeBytes("Content-Disposition: form-data; name=\"" + key
                                        + "\"\r\n")
                                ds.writeBytes("\r\n")
                                ds.write(value.getBytes("UTF-8"))
                                ds.writeBytes("\r\n")
                            }
                        }
                    }
                    //添加结尾数据
                    ds.writeBytes("--" + HttpClientBuilder.BOUNDARY + "--" + "\r\n")
                    ds.writeBytes("\r\n")
                    ds.flush()
                    ds.close()

                    //if (httpURLConnection.getResponseCode() == 200) {
                    //获取输入流返回数据
                    InputStream is = httpURLConnection.getInputStream()
                    ByteArrayOutputStream out = new ByteArrayOutputStream()
                    int b;
                    while ((b = is.read()) != -1) {
                        out.write(b)
                    }
                    String response = new String(out.toByteArray(), "UTF-8")
                    if (callback != null) {
                        callback.onSuccess(response)
                    }
                    is.close()
                    out.close()
//                    } else {
//                        if (callback != null) {
//                            callback.onError(httpURLConnection.getResponseCode(), new IOException())
//                        }
//                    }
                } catch (SocketTimeoutException e) {
                    if (callback != null) {
                        callback.onError(-1, e)
                    }
                } catch (MalformedURLException e) {
                    if (callback != null) {
                        callback.onError(-1, e)
                    }
                } catch (IOException e) {
                    if (callback != null) {
                        callback.onError(-1, e)
                    }
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect() //关闭连接
                        threadPool.shutdown()
                    }
                }
            }
        }
    }

    Runnable doPostP() {
        return new Runnable() {
            @Override
            void run() {
                HttpURLConnection httpURLConnection = connection()
                try {
                    print("doPostP")
                    StringBuffer sb = new StringBuffer()
                    if (requestParams != null && requestParams.size() > 0) {
                        for (Map.Entry<String, Object> entry : requestParams.entrySet()) {
                            String key = entry.getKey().toString()
                            String value = entry.getValue().toString()
                            if (sb.length() != 0) {
                                sb.append("&")
                            }
                            sb.append(key).append("=").append(value)
                        }
                    }
                    httpURLConnection.setRequestProperty("accept", "*/*")
                    httpURLConnection.setRequestProperty("Content-Length", String.valueOf(sb.length()))
                    httpURLConnection.connect()

                    PrintWriter printWriter = new PrintWriter(httpURLConnection.getOutputStream())
                    // 发送请求参数
                    printWriter.write(sb.toString())
                    // flush输出流的缓冲
                    printWriter.flush()
                    printWriter.close()

//                    if (httpURLConnection.getResponseCode() == 200) {
                    //获取输入流返回数据
                    InputStream is = httpURLConnection.getInputStream()
                    ByteArrayOutputStream out = new ByteArrayOutputStream()
                    int b;
                    while ((b = is.read()) != -1) {
                        out.write(b)
                    }
                    String response = new String(out.toByteArray(), "UTF-8")
                    if (callback != null) {
                        callback.onSuccess(response)
                    }
                    is.close()
                    out.close()
//                    } else {
//                        if (callback != null) {
//                            callback.onError(httpURLConnection.getResponseCode(), new IOException())
//                        }
//                    }
                } catch (SocketTimeoutException e) {
                    if (callback != null) {
                        callback.onError(-1, e)
                    }
                } catch (MalformedURLException e) {
                    if (callback != null) {
                        callback.onError(-1, e)
                    }
                } catch (IOException e) {
                    if (callback != null) {
                        callback.onError(-1, e)
                    }
                } finally {
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect() //关闭连接
                        threadPool.shutdown()
                    }
                }
            }
        }
    }

    /**
     * 把文件转换成字节数组
     */
    private byte[] getBytes(File file) throws Exception {
        DataInputStream inputStream = new DataInputStream(new FileInputStream(file))
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] bufferOut = new byte[1024];
        int bytes = 0;
        while ((bytes = inputStream.read(bufferOut)) != -1) {
            out.write(bufferOut, 0, bytes);
        }
        inputStream.close()
        return out.toByteArray();
    }

    static HttpClientBuilder builder() {
        return new HttpClientBuilder()
    }
}
