package name.yyx.trojanow.controller;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import name.yyx.trojanow.entity.Account;
import name.yyx.trojanow.service.AccountManager;
import name.yyx.trojanow.service.IAccount;

/**
 * Created by dell on 2015/4/17.
 */
public class Controller extends Application{

    public static final String TROJANOW_PREFERENCES = "TrojanowPrefs";

    private Account account;

    private IAccount accountService;

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

    public boolean signIn(String username, String password){
        accountService = new AccountManager();
        if(account == null){
            account = new Account();
        }
        account.setUsername(username);
        account.setPassword(password);
        account.setIp("");
        account =  accountService.signIn(account);

        if (account.getToken() != null){
            write();
            Log.i("Controller", account.getToken());
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
        if(accountService.signOut(account)){
            remove();
            account = null;
            return true;
        }
        else{
            return false;
        }
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
}
