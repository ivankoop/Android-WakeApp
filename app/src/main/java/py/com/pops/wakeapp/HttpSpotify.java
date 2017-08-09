package py.com.pops.wakeapp;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

/**
 * Created by ivankoop on 7/4/16.
 */


public class HttpSpotify extends AsyncTask<String,Void,String> {
    private String AuthString;
    private String URL;
    private String requestParams;
    private String method = "GET";
    private Callback successCallback;
    private Callback errorCallback;
    private int responseCode = -1;
    private String errorString = "";

    @Override
    protected String doInBackground(String... params) {
        InputStream is = null;
        String url = URL;

        if(method.equals("GET")) {
            url = URL + "?" + requestParams;

        }

        try {
            java.net.URL ajaxurl = new URL(url);
            HttpsURLConnection conn = (HttpsURLConnection) ajaxurl.openConnection();

            SSLContext sc;
            sc = SSLContext.getInstance("TLS");
            sc.init(null, null, new java.security.SecureRandom());
            conn.setSSLSocketFactory(sc.getSocketFactory());

            conn.setReadTimeout(25000);
            conn.setConnectTimeout(30000);
            conn.setRequestMethod(method);

            if(AuthString != null) {
                Log.e("sendit auth", AuthString);
                conn.setRequestProperty("Authorization", AuthString);
            }

            if(method.equals("POST")) {
                byte[] postdata = requestParams.getBytes(Charset.forName("UTF-8"));
                int postdatalength = postdata.length;
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                conn.setRequestProperty("Accept","*/*");
                conn.setRequestProperty("User-Agent","runscope/0.1");
                conn.setRequestProperty("charset", "utf-8");
                conn.setRequestProperty("Content-Length", Integer.toString(postdatalength));
                conn.setUseCaches(false);
                conn.setDoInput(true);

                if(this.isCancelled()) {
                    return null;
                }

                conn.connect();
                try {
                    DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
                    wr.write(postdata);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                conn.setDoInput(true);

                if(this.isCancelled()) {
                    return null;
                }

                conn.connect();
            }

            int responsecode = conn.getResponseCode();
            if(responsecode != 200) {
                is = conn.getErrorStream();
                errorString = readInput(is);
                responseCode = responsecode;
                return null;
            }
            System.out.println("The response code is: " + responsecode);
            responseCode = responsecode;
            is = conn.getInputStream();

            return readInput(is);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if(is != null) {
                try{
                    is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String response) {
        if(response != null) {
            successCallback.run(response);
        } else {
            errorCallback.run(errorString + " / Codigo: " +String.valueOf(responseCode));
        }
    }

    @Override
    protected void onCancelled(String result) {
        Log.d("HttpSpotify", "Request cancelled");
    }

    private String readInput(InputStream stream) throws IOException {
        Reader reader = null;
        reader = new InputStreamReader(stream,"UTF-8");
        StringBuilder output  = new StringBuilder();
        BufferedReader breader = new BufferedReader(reader);
        String read = breader.readLine();

        while(read != null) {

            if(this.isCancelled()) {
                return null;
            }

            output.append(read);
            read = breader.readLine();
        }

        return output.toString();
    }

    public static String encodeAuthHeader(String data, String prefix) {
        String auth = prefix + " " + Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);
        Log.e("Generated Auth",auth);
        return auth;
    }

    public static String parseParams(ArrayList<String> params) {
        StringBuilder output = new StringBuilder();

        for(int i = 0; i < params.size(); i++) {
            if(i < params.size()-1) {
                output.append(params.get(i) + "&");
            } else {
                output.append(params.get(i));
            }
        }

        return  output.toString();
    }

    public String getAuthString() {
        return AuthString;
    }

    public HttpSpotify setAuthString(String authString) {
        AuthString = authString;
        return this;
    }

    public String getURL() {
        return URL;
    }

    public HttpSpotify setURL(String URL) {
        this.URL = URL;
        return this;
    }

    public String getParams() {
        return requestParams;
    }

    public HttpSpotify setParams(String params) {
        this.requestParams = params;
        return this;
    }

    public String getMethod() {
        return method;
    }

    public HttpSpotify setMethod(String method) {
        this.method = method;
        return this;
    }

    public Callback getSuccessCallback() {
        return successCallback;
    }

    public HttpSpotify setSuccessCallback(Callback successCallback) {
        this.successCallback = successCallback;
        return this;
    }

    public Callback getErrorCallback() {
        return errorCallback;
    }

    public HttpSpotify setErrorCallback(Callback errorCallback) {
        this.errorCallback = errorCallback;
        return this;
    }
}

