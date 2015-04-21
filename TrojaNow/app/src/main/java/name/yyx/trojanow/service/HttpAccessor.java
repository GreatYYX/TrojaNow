package name.yyx.trojanow.service;

import android.util.Log;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import name.yyx.trojanow.entity.Account;

/**
 * Created by dell on 2015/4/11.
 */
public class HttpAccessor {
    public JSONObject post(String url, JSONObject request) {
        return post(url, request, null);
    }

    public JSONObject post(String url, JSONObject request, String auth) {

        HttpClient client = new DefaultHttpClient();
        JSONObject response = null;

        try {
            //set request header and body
            HttpPost httpPost = new HttpPost(url);
            StringEntity stringEntity = new StringEntity(request.toString());

            if(auth != null){
                Header[] headers = new Header[2];
                Header header1 = new BasicHeader("Content-Type","application/json");
                Header header2 = new BasicHeader("Authorization", auth);
                headers[0] = header1;
                headers[1] = header2;
                httpPost.setHeaders(headers);
            }
            stringEntity.setContentType("application/json");
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPost.setEntity(stringEntity);

            HttpResponse httpResponse = client.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            String respMsg = EntityUtils.toString(httpEntity);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();


            if(respMsg.equals("[]")){
                response = new JSONObject();
            }
            else {
                response = new JSONObject(respMsg);
            }
            response.put("statusCode",statusCode);
            Log.i("respMsg", respMsg);

        } catch (JSONException e) {
            return null;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public JSONObject get(String url,String auth){

        HttpClient client = new DefaultHttpClient();
        JSONObject response = null;

        try {
            HttpGet httpGet = new HttpGet(url);
            Header[] headers = new Header[2];
            Header header1 = new BasicHeader("Content-Type","application/json");
            Header header2 = new BasicHeader("Authorization", auth);
            headers[0] = header1;
            headers[1] = header2;
            httpGet.setHeaders(headers);

            HttpResponse httpResponse = client.execute(httpGet);
            HttpEntity httpEntity = httpResponse.getEntity();
            String respMsg = EntityUtils.toString(httpEntity);
            StatusLine statusLine= httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();

            if(respMsg.equals("[]")){
                response = new JSONObject();
            }
            else {
                response = new JSONObject(respMsg);
            }
            response.put("statusCode", statusCode);

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return response;
    }

    public JSONObject put(String url,JSONObject request){
        return put(url,request, null);
    }

    public JSONObject put(String url,JSONObject request, String auth){

        HttpClient client = new DefaultHttpClient();
        JSONObject response = null;

        StringEntity stringEntity = null;
        try {
            HttpPut httpPut = new HttpPut(url);
            stringEntity = new StringEntity(request.toString());

            if(auth != null){
                Header[] headers = new Header[2];
                Header header1 = new BasicHeader("Content-Type","application/json");
                Header header2 = new BasicHeader("Authorization", auth);
                headers[0] = header1;
                headers[1] = header2;
                httpPut.setHeaders(headers);
            }
            stringEntity.setContentType("application/json");
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httpPut.setEntity(stringEntity);

            HttpResponse httpResponse = client.execute(httpPut);
            HttpEntity httpEntity = httpResponse.getEntity();
            String respMsg = EntityUtils.toString(httpEntity);
            StatusLine statusLine = httpResponse.getStatusLine();
            int statusCode = statusLine.getStatusCode();


            if(respMsg.equals("[]")){
                response = new JSONObject();
            }
            else {
                response = new JSONObject(respMsg);
            }
            response.put("statusCode", statusCode);
            Log.i("respMsg", respMsg);

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return response;
    }

    public JSONObject delete(String url,JSONObject request){
        return null;
    }

}
