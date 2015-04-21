package name.yyx.trojanow.service;

import java.util.List;
import java.util.Map;

import name.yyx.trojanow.entity.Account;
import name.yyx.trojanow.entity.Status;

/**
 * Created by dell on 2015/4/19.
 */
public interface IStatus {

    public Status create(Status status);

    public List<Map<String, Object>> list(Account account);

}
