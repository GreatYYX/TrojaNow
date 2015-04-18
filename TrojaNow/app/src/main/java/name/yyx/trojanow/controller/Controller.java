package name.yyx.trojanow.controller;

import android.app.Application;
import android.content.SharedPreferences;
import android.util.Log;

import name.yyx.trojanow.entity.Account;
import name.yyx.trojanow.service.IAccount;
import name.yyx.trojanow.service.impl.AccountImpl;

/**
 * Created by dell on 2015/4/17.
 */
public class Controller extends Application{

    public static final String TROJANOW_PREFERENCES = "TrojanowPrefs";

    private Account account;

    private IAccount accountService;

    public Controller(){
        account = new Account();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        account = read();
    }

    private Account read(){
        SharedPreferences sharedPref = getSharedPreferences(TROJANOW_PREFERENCES, MODE_PRIVATE);
        String username = sharedPref.getString("user", null);
        String token = sharedPref.getString("token", null);

        account.setUsername(username);
        account.setToken(token);

        return account;
    }

    private void write(){
        SharedPreferences sharedPref = getSharedPreferences(TROJANOW_PREFERENCES, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("user", account.getUsername());
        editor.putString("token", account.getToken());
//        editor.putLong("timeStamp",)
        editor.commit();
    }

    public boolean signIn(String username, String password){
        accountService = new AccountImpl();

        account.setUsername(username);
        account.setPassword(password);
        account.setIp("");
        account =  accountService.signIn(account);

        if (account.getToken() == null){
            return false;
        }
        Log.i("Controller", account.getToken());
        write();
        return true;
    }

    public boolean isSignedIn(){
        if(account == null){
            return false;
        }

        if(account.getToken() == null){
            return false;
        }
        else{
            return true;
        }
    }



}
