package name.yyx.trojanow.service;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import name.yyx.trojanow.entity.Status;

/**
 * Created by dell on 2015/4/19.
 */
public class StatusManager implements IStatus {
    private final static String STATUS_OK = "200";
    private final static String STATUS_CREATED = "201";

    @Override
    public Status create(Status status) {
        String url = "http://trojanow.yyx.name:1234/";
        JSONObject request = new JSONObject();
        JSONObject response;

        try {
            request.put("user", status.getAccount().getUsername());
            request.put("token", status.getAccount().getToken());
            request.put("content", status.getContent());
            request.put("anonymous", status.isAnonymous());
            request.put("temperature", status.getTemperature());
            request.put("location", status.getLocation());
            request.put("auth", true);

            HttpAccessor httpAccessor = new HttpAccessor();
            response = httpAccessor.post(url + "", request);
            if(response.get("statusCode").toString() == STATUS_CREATED){
                long time = (long)response.get("date");
                Date date = new Date(time);
                status.setId((long)response.get("id"));
                status.setDate(date);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }
}
