package name.yyx.trojanow.serverpush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.StrictMode;

import org.json.JSONException;
import org.json.JSONObject;

public class ServerPushManager {

    private Context ctx;
    private String user;
    private IServerPush callback;
    private ServiceReceiver recv;

    public ServerPushManager(Context ctx, String user) {
        this.ctx = ctx;
        this.user = user;

        recv = new ServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PushService.ACTION_BROADCAST);
        ctx.registerReceiver(recv, filter);
    }

    public ServerPushManager(Context ctx, String user, IServerPush callback) {
        this(ctx, user);
        this.callback = callback;
    }

    public void start() {

        // hack wmqtt sdk version problem
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        SharedPreferences.Editor editor = ctx.getSharedPreferences(PushService.TAG, Context.MODE_PRIVATE).edit();
        editor.putString(PushService.PREF_MQTT_USER, user);
        editor.commit();
        PushService.actionStart(ctx);
    }

    public void stop() {
        PushService.actionStop(ctx);
        ctx.unregisterReceiver(recv);
    }

    /**
     * Handle broadcast message from service
     * Parser protocol
     */
    public class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Bundle bundle = intent.getExtras();
            String data = bundle.getString("data");

            try {
                JSONObject serviceMsg = new JSONObject(data);
                String type = serviceMsg.getString("type");
                if(type.equals("NEW_STATUS")) {
                    callback.newStatus();
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
