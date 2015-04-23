package name.yyx.trojanow.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.yyx.trojanow.entity.Account;

public class FollowManager implements IFollow{

    private List<Map<String, Object>> listAdapter(List<Account>  followers){
        List<Map<String, Object>> listFollowers = new ArrayList<Map<String, Object>>();

        for(int i = 0; i < followers.size(); i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("user", followers.get(i).getUsername());
            map.put("nickname", followers.get(i).getNickname());

            listFollowers.add(map);
        }

        return listFollowers;
    }

    @Override
    public boolean follow(Account account,String follower) {
        JSONObject request = new JSONObject();
        JSONObject response;

        String auth = account.getUsername() + ":" + account.getToken();
        HttpAccessor httpAccessor = new HttpAccessor();

        try {
            request.put("follow", follower);

            response = httpAccessor.post(HttpAccessor.URL+ "follows", request, auth);

            if(response == null){
                Log.i("FollowManager", "follow HTTP error");
            }

            if(response.get("statusCode").toString().equals(HttpAccessor.STATUS_CREATED)){
                return true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public List<Map<String, Object>> listFollowers(Account account) {
        List<Account> followers = new ArrayList<Account>();
        JSONObject response;

        String auth = account.getUsername() + ":" + account.getToken();
        HttpAccessor httpAccessor = new HttpAccessor();

        response = httpAccessor.get(HttpAccessor.URL + "follows", auth);

        try {
            if(response.get("statusCode").toString().equals(HttpAccessor.STATUS_OK)){
                JSONArray f = response.getJSONArray("follows");

                for(int i = 0; i < f.length(); i++){
                    Account follower = new Account();
                    follower.setUsername(f.getJSONObject(i).get("user").toString());
                    follower.setNickname(f.getJSONObject(i).get("nickname").toString());

                    followers.add(follower);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return listAdapter(followers);
    }
}
