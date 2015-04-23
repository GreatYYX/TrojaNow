package name.yyx.trojanow.service;

import java.util.List;
import java.util.Map;

import name.yyx.trojanow.entity.Account;

public interface IFollow {

    public boolean follow(Account account, String follower);

    public List<Map<String, Object>> listFollowers(Account account);

}
