package name.yyx.trojanow.entity;

import java.util.Date;

/**
 * Created by dell on 2015/4/17.
 */
public class Account {
    private  String username;

    private String password;

    private String ip;

    private String token;

    private Date timeStamp;

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getIp() {
        return ip;
    }

    public String getToken() {
        return token;
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setTimeStamp(Date timeStamp) {
        this.timeStamp = timeStamp;
    }
}
