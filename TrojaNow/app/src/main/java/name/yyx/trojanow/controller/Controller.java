package name.yyx.trojanow.controller;

import android.app.Application;
import android.util.Log;

import name.yyx.trojanow.entity.Account;
import name.yyx.trojanow.service.IAccount;
import name.yyx.trojanow.service.impl.AccountImpl;

/**
 * Created by dell on 2015/4/17.
 */
public class Controller extends Application{

    private Account account;

    private IAccount accountService;

    public boolean signIn(String username, String password){
        account = new Account();
        accountService = new AccountImpl();

        account.setUsername(username);
        account.setPassword(password);
        account.setIp("");
        account =  accountService.signIn(account);

        if (account.getToken().equals("")){
            return false;
        }

        Log.i("Controller", account.getToken());
        return true;

    }

}
