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
    private final static String URL = "http://trojanow.yyx.name:1234/";

    public boolean isNull(Object object){
        if(object == null){
            return true;
        }
        else{
            return false;
        }
    }
    @Override
    public Status create(Status status) {

        JSONObject request = new JSONObject();
        JSONObject response;

        try {
            request.put("content", status.getContent());
            request.put("anonymous", status.isAnonymous());
            request.put("temperature", isNull(status.getTemperature()) ? JSONObject.NULL : status.getTemperature());
            request.put("location", isNull(status.getLocation()) ? JSONObject.NULL : status.getLocation());

            String auth = status.getAccount().getUsername() + ":" + status.getAccount().getToken();

            HttpAccessor httpAccessor = new HttpAccessor();
            response = httpAccessor.post(URL + "statuses", request, auth);
            if(response.get("statusCode").toString().equals(STATUS_CREATED)){
                long time = Long.parseLong(response.get("date").toString());
                Date date = new Date(time);
                status.setId(Integer.parseInt(response.get("id").toString()));
                status.setDate(date);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return status;
    }
}
