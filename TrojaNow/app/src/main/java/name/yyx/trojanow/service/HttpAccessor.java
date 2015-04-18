package name.yyx.trojanow.service;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
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

    public HttpAccessor(){
    }

    public JSONObject post(String url, JSONObject request) {

        HttpClient client = new DefaultHttpClient();
//        String url = "http://trojanow.yyx.name:1234/test";
//        JSONObject request = new JSONObject();
        JSONObject response = null;

        try {
//            request.put("test", "test");

            //set request header and body
            StringEntity stringEntity = new StringEntity(request.toString());
            stringEntity.setContentType("application/json");
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(stringEntity);

            HttpResponse httpResponse = client.execute(httpPost);
            HttpEntity httpEntity = httpResponse.getEntity();
            String respMsg = EntityUtils.toString(httpEntity);

            response = new JSONObject(respMsg);
            //        if (jsonObject.getInt("success") == 200){
            //            Log.i("lalala", "lalalla");
            //        }
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

    public JSONObject get(String url){

        HttpClient client = new DefaultHttpClient();
        JSONObject response = new JSONObject();

         return null;
    }

    public JSONObject put(String url,JSONObject request){

        HttpClient client = new DefaultHttpClient();
        JSONObject response = null;

        StringEntity stringEntity = null;
        try {
            stringEntity = new StringEntity(request.toString());
            stringEntity.setContentType("application/json");
            stringEntity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));

            HttpPut httpPut = new HttpPut(url);
            httpPut.setEntity(stringEntity);

            HttpResponse httpResponse = client.execute(httpPut);
            HttpEntity httpEntity = httpResponse.getEntity();
            String respMsg = EntityUtils.toString(httpEntity);

            response = new JSONObject(respMsg);
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

}
