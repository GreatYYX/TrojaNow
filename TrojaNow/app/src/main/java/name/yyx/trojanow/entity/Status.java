package name.yyx.trojanow.entity;

import java.util.Date;

/**
 * Created by dell on 2015/4/19.
 */
public class Status {

    private int id;

    private Date date;

    private String content;

    private boolean anonymous;

    private String temperature;

    private float[] location;

    private Account account;

    public int getId() {
        return id;
    }

    public Date getDate() {
        return date;
    }

    public String getContent() {
        return content;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public String getTemperature() {
        return temperature;
    }

    public float[] getLocation() {
        return location;
    }

    public Account getAccount() {
        return account;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public void setLocation(float[] location) {
        this.location = location;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
