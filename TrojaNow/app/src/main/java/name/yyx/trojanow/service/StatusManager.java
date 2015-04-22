package name.yyx.trojanow.service;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import name.yyx.trojanow.entity.Account;
import name.yyx.trojanow.entity.Status;

/**
 * Created by dell on 2015/4/19.
 */
public class StatusManager implements IStatus {
    private final static String STATUS_OK = "200";
    private final static String STATUS_CREATED = "201";
    private final static String URL = "http://trojanow.yyx.name:1234/";

    private boolean isNull(Object object){
        if(object == null){
            return true;
        }
        else{
            return false;
        }
    }

    private List<Map<String, Object>> listAdpter(List<Status> statuses){
        List<Map<String, Object>> statusList = new ArrayList<Map<String, Object>>();

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        DecimalFormat decimalFormat=new DecimalFormat(".00");

        for(int i = 0; i < statuses.size(); i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("content", statuses.get(i).getContent());
            map.put("author", statuses.get(i).getAccount().getNickname());

            calendar.setTime(statuses.get(i).getDate());
            calendar.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
            map.put("date", sdf.format(calendar.getTime()).toString());

            map.put("temperature", statuses.get(i).getTemperature());

            float[] location = statuses.get(i).getLocation();
            if(location != null) {
                map.put("location",decimalFormat.format(location[0]) + ", " + decimalFormat.format(location[1]));
            }
            else{
                map.put("location", null);
            }
            statusList.add(map);
        }
        return statusList;
    }

    @Override
    public Status create(Status status) {

        JSONObject request = new JSONObject();
        JSONObject response;

        try {
            request.put("content", status.getContent());
            request.put("anonymous", status.isAnonymous());
            request.put("temperature", isNull(status.getTemperature()) ? JSONObject.NULL : status.getTemperature());
            if(!isNull(status.getLocation())) {
                JSONArray jsonLoc = new JSONArray();
                jsonLoc.put(0, status.getLocation()[0]);
                jsonLoc.put(1, status.getLocation()[1]);
                request.put("location", jsonLoc);
            }
            else{
                request.put("location", JSONObject.NULL );
            }
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

    @Override
    public List<Map<String, Object>> list(Account account, boolean wantAnonymous) {
        JSONObject response;
        List<Status> statuses = new ArrayList<Status>();

        String auth = account.getUsername() + ":" + account.getToken();
        HttpAccessor httpAccessor = new HttpAccessor();

        if(wantAnonymous) {
            response = httpAccessor.get(URL + "statuses", auth);
        }
        else{
            response = httpAccessor.get(URL + "statuses/normal", auth);
        }

        try {
            if(response.get("statusCode").toString().equals(STATUS_OK)){
                JSONArray s = (JSONArray)response.getJSONArray("statuses");
                for(int i = 0; i < s.length();i++) {
                    Status status = new Status();
                    int id = Integer.parseInt(s.getJSONObject(i).get("id").toString());
                    String author = s.getJSONObject(i).get("author").toString();
                    String authorNick = s.getJSONObject(i).get("author_nickname").toString();
                    String content = s.getJSONObject(i).get("content").toString();
                    long time = Long.parseLong(s.getJSONObject(i).get("date").toString());
                    Date date = new Date(time * 1000);

                    String temperature =null;
                    if(s.getJSONObject(i).get("temperature") != JSONObject.NULL) {
                        temperature = s.getJSONObject(i).get("temperature").toString();
                    }

                    float[] location = null;
                    Object type = s.getJSONObject(i).get("location");
                    if (type instanceof JSONArray){
                        JSONArray locationArray = s.getJSONObject(i).getJSONArray("location");
                        location = new float[]{Float.parseFloat(locationArray.get(0).toString()),
                                Float.parseFloat(locationArray.get(1).toString())};
                    }

                    status.setId(id);
                    Account user = new Account();
                    user.setUsername(author);
                    user.setNickname(authorNick);
                    status.setAccount(user);
                    status.setContent(content);
                    status.setDate(date);
                    status.setTemperature(temperature);
                    status.setLocation(location);

                    statuses.add(status);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listAdpter(statuses);
    }

}
