package name.yyx.trojanow.controller;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import name.yyx.trojanow.entity.Account;
import name.yyx.trojanow.entity.Status;
import name.yyx.trojanow.service.AccountManager;
import name.yyx.trojanow.service.FollowManager;
import name.yyx.trojanow.service.IAccount;
import name.yyx.trojanow.service.IFollow;
import name.yyx.trojanow.service.IStatus;
import name.yyx.trojanow.service.StatusManager;

/**
 * Created by dell on 2015/4/17.
 */
public class Controller extends Application{

    public static final String TROJANOW_PREFERENCES = "TrojanowPrefs";

    private Account account;

    private IAccount accountService;

    private Status status;

    private IStatus statusService;

    private IFollow followService;

    @Override
    public void onCreate() {
        super.onCreate();
        read();
    }

    private void read(){

        if(account == null){
            account = new Account();
        }
        SharedPreferences sharedPref = getSharedPreferences(TROJANOW_PREFERENCES, MODE_PRIVATE);
        String username = sharedPref.getString("user", null);
        String token = sharedPref.getString("token", null);

        account.setUsername(username);
        account.setToken(token);
    }

    private void write(){
        SharedPreferences sharedPref = getSharedPreferences(TROJANOW_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user", account.getUsername());
        editor.putString("token", account.getToken());
        editor.commit();
    }

    private void remove(){
        SharedPreferences sharedPref = getSharedPreferences(TROJANOW_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.remove("user");
        editor.remove("token");
        editor.commit();
    }

    public boolean readAccept(Context context){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean accept_anonymous = sharedPref.getBoolean("accept_anonymous",false);
        return accept_anonymous;
    }

    public String getUsername(){
        return account.getUsername();
    }

/* ======================== account ============================*/

    public boolean signIn(String username, String password){
        accountService = new AccountManager();
        if(account == null){
            account = new Account();
        }
        account.setUsername(username);
        account.setPassword(password);
        account.setIp("");

        Account user = accountService.signIn(account);
        account.setToken(user.getToken());

        if (account.getToken() != null){
            write();
            return true;
        }
        else{
            return false;
        }
    }

    public boolean isSignedIn(){
        if(account == null){
            return false;
        }
        if(account.getToken() != null){
            return true;
        }
        else{
            return false;
        }
    }

    public boolean signOut(){
        accountService = new AccountManager();
        accountService.signOut(account);
        remove();
        account = null;
        return true;
//        if(accountService.signOut(account)){
//            account = null;
//            return true;
//        }
//        else{
//            return false;
//        }
    }

    public boolean registration(String username, String password, String nickname){
        account = new Account();
        accountService = new AccountManager();

        account.setUsername(username);
        account.setPassword(password);
        account.setNickname(nickname);

        if(accountService.registration(account)){
            return true;
        }
        else{
            return false;
        }
    }

/* ======================== status ============================*/

    public boolean createStatus(String content, boolean isAnonymous, String temperature, String[] location){

        status = new Status();
        statusService = new StatusManager();

        status.setAccount(account);
        status.setContent(content);
        status.setAnonymous(isAnonymous);
        status.setTemperature(temperature);
        float[] loc = null;
        if(location != null) {
            loc = new float[]{Float.parseFloat(location[0]), Float.parseFloat(location[1])};
        }
        status.setLocation(loc);

        Status result = statusService.create(status);
        status.setId(result.getId());
        status.setDate(result.getDate());
        Date d = status.getDate();

        if(status.getId() != 0){
            return true;
        }
        else{
            return false;
        }
    }

    public List<Map<String, Object>> listStatuses(boolean wantAnonymous){
        statusService = new StatusManager();
        return statusService.list(account, wantAnonymous);
    }

/* ======================== friend ============================*/

    public boolean follow(String followee){
        followService = new FollowManager();
        if(followService.follow(account, followee)){
            return true;
        }
        else{
            return false;
        }
    }

    public List<Map<String, Object>> listFollowees(){
        followService = new FollowManager();
        return followService.listFollowees(account);
    }

}


