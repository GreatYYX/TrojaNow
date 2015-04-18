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

    private JSONObject response;

    private Account account;

    private String url;

    public Account signIn(Account account){

        url = "http://trojanow.yyx.name:1234/";
        this.account = account;

//      AccountSignIn accountSignIn = new AccountSignIn();
//      accountSignIn.execute();

        JSONObject jsonObject = new JSONObject();
        String token = null;
        long timestamp = 0l;

        try {
            jsonObject.put("user",account.getUsername());
            jsonObject.put("password", account.getPassword());
            jsonObject.put("ip", account.getIp());

            HttpAccessor httpAccessor = new HttpAccessor();
            response = httpAccessor.put(url + "account/signin", jsonObject);

            if (response != null){
                Date time;
                token = (String)response.get("token");
//              timestamp = (long)response.get("timestamp");
//              time = new Date(timestamp);
                account.setToken(token);
//              account.setTimestamp(time);
            }
            else {
                Log.i("AccountImpl", "account error");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return account;
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
