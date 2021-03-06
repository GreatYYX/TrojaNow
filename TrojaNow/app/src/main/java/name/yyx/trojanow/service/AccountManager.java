package name.yyx.trojanow.service;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import name.yyx.trojanow.entity.Account;

public class AccountManager implements IAccount{

    public Account signIn(Account account){


        JSONObject request = new JSONObject();
        JSONObject response;

        try {
            request.put("user",account.getUsername());
            request.put("password", account.getPassword());
            request.put("ip", account.getIp());

            HttpAccessor httpAccessor = new HttpAccessor();
            response = httpAccessor.put(HttpAccessor.URL + "account/signin", request);

            if (response == null){
                Log.i("AccountImpl", "sign in HTTP error");
            }

            if (response.get("statusCode").toString().equals(HttpAccessor.STATUS_OK)){
//                long time = Long.parseLong(response.get("timestamp").toString());
//                Date timeStamp = new Date(time);
                String token = (String)response.get("token");
                account.setToken(token);
//                account.setTimestamp(timeStamp);
            }
            else{
                Log.i("AccountImpl", "sign in wrong message");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return account;
    }

    public boolean signOut(Account account){
        JSONObject response;

        try {
            String auth = account.getUsername() + ":" + account.getToken();

            HttpAccessor httpAccessor = new HttpAccessor();
            response = httpAccessor.get(HttpAccessor.URL + "account/signout", auth);

            if(response == null){
                Log.i("AccountImpl", "sign out HTTP error");
            }

            if(response.get("statusCode").toString().equals(HttpAccessor.STATUS_OK)){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean registration(Account account){
        JSONObject request = new JSONObject();
        JSONObject response;

        try {
            request.put("user", account.getUsername());
            request.put("password", account.getPassword());
            request.put("nickname", account.getNickname());

            HttpAccessor httpAccessor = new HttpAccessor();
            response = httpAccessor.post(HttpAccessor.URL + "account/reg",request);

            if(response == null){
                Log.i("AccountImpl", "register HTTP error");
            }

            if(response.get("statusCode").toString().equals(HttpAccessor.STATUS_CREATED)){
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
