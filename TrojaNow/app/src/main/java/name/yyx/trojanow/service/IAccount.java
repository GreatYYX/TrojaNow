package name.yyx.trojanow.service;

import name.yyx.trojanow.entity.Account;

public interface IAccount {

    public Account signIn(Account account);

    public boolean signOut(Account account);

    public boolean registration(Account account);

}
