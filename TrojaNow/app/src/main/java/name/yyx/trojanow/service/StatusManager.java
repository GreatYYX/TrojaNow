package name.yyx.trojanow.service;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        for(int i = 0; i < statuses.size(); i++){
            Map<String, Object> map = new HashMap<String, Object>();
//            map.put("content", statuses.get(i).getContent());
            map.put("author", statuses.get(i).getAccount().getNickname());
            map.put("date", statuses.get(i).getDate());
//            map.put("temperature", statuses.get(i).getTemperature());

            String[] location = statuses.get(i).getLocation();
//            map.put("location", location[0] + ", " + location[1]);

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

    public List<Map<String, Object>> list(Account account){
        JSONObject response;
        List<Status> statuses = new ArrayList<Status>();

        String auth = account.getUsername() + ":" + account.getToken();
        HttpAccessor httpAccessor = new HttpAccessor();
        response = httpAccessor.get(URL + "statuses", auth);

        try {
            if(response.get("statusCode").toString().equals(STATUS_OK)){
                JSONArray s = (JSONArray)response.getJSONArray("statuses");
                for(int i = 0; i < s.length();i++) {
                    Status status = new Status();
                    int id = Integer.parseInt(s.getJSONObject(i).get("id").toString());
                    String author = s.getJSONObject(i).get("author").toString();
                    String authorNick = s.getJSONObject(i).get("author_nickname").toString();
//                    String content = s.getJSONObject(i).get("content").toString();
                    long time = Long.parseLong(s.getJSONObject(i).get("date").toString());
                    Date date = new Date(time);
//                    String temperature = s.getJSONObject(i).get("temperature").toString();
//                    String[] location = null;
//                    JSONArray locationArray = s.getJSONObject(i).getJSONArray("location");
//                    location[0] = locationArray.get(0).toString();
//                    location[1] = locationArray.get(1).toString();

                    status.setId(id);
                    account.setUsername(author);
                    account.setNickname(authorNick);
                    status.setAccount(account);
//                    status.setContent(content);
                    status.setDate(date);
//                    status.setTemperature(temperature);
//                    status.setLocation(location);

                    statuses.add(status);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return listAdpter(statuses);
    }
}
