package name.yyx.trojanow.entity;

import java.util.Date;

public class Account implements Cloneable{
    private  String username;

    private String nickname;

    private String password;

    private String ip;

    private String token;

    private Date timeStamp;

    public String getUsername() {
        return username;
    }

    public String getNickname() {
        return nickname;
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

    public void setNickname(String nickname) {
        this.nickname = nickname;
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
