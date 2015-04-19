package name.yyx.trojanow.service.impl;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import name.yyx.trojanow.entity.Account;
import name.yyx.trojanow.service.HttpAccessor;
import name.yyx.trojanow.service.IAccount;

/**
 * Created by dell on 2015/4/17.
 */
public class AccountImpl implements IAccount{

    private final static int STATUS_OK = 200;
    private final static int STATUS_CREATED = 201;

    public Account signIn(Account account){

        String url = "http://trojanow.yyx.name:1234/";
        JSONObject request = new JSONObject();
        JSONObject response;

        String token = null;
        long timestamp = 0l;

        try {
            request.put("user",account.getUsername());
            request.put("password", account.getPassword());
            request.put("ip", account.getIp());

            HttpAccessor httpAccessor = new HttpAccessor();
            response = httpAccessor.put(url + "account/signin", request);

            if (response == null){
                Log.i("AccountImpl", "sign in error");
            }

            if ((int)response.get("statusCode") == STATUS_OK){
                Date time;
                token = (String)response.get("token");
//              timestamp = (long)response.get("timestamp");
//              time = new Date(timestamp);
                account.setToken(token);
//              account.setTimestamp(time);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return account;
    }

    public boolean signOut(Account account){
        String url = "http://trojanow.yyx.name:1234/";
        JSONObject request = new JSONObject();
        JSONObject response;

        try {
            request.put("user", account.getUsername());
            request.put("token", account.getToken());

            HttpAccessor httpAccessor = new HttpAccessor();
            response = httpAccessor.get(url + "account/signout", request);

            if(response == null){
                Log.i("AccountImpl", "sign out error");
            }

            if((int)response.get("statusCode") == STATUS_OK){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

//    class AccountSignIn extends AsyncTask<String, String, String>{
//
//        @Override
//        protected String doInBackground(String... params) {
//            JSONObject jsonObject = new JSONObject();
//            try {
//                jsonObject.put("username",account.getUsername());
//                jsonObject.put("password", account.getPassword());
//                jsonObject.put("ip", account.getIp());
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            HttpAccessor httpAccessor = new HttpAccessor();
//            response = httpAccessor.post("http://trojanow.yyx.name:1234/account/signin", jsonObject);
//            return null;
//        }
//    }

}
