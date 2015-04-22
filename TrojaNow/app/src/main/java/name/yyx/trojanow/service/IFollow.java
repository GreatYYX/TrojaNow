package name.yyx.trojanow.service;

import java.util.List;
import java.util.Map;

import name.yyx.trojanow.entity.Account;

/**
 * Created by dell on 2015/4/22.
 */
public interface IFollow {

    public boolean follow(Account account, String followee);

    public List<Map<String, Object>> listFollowees(Account account);

}
