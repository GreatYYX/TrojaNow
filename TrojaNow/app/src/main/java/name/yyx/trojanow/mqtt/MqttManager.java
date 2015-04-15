package name.yyx.trojanow.mqtt;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.StrictMode;

public class MqttManager {

    private Context ctx;
    private String user;
    private PushService service;

    public MqttManager(Context ctx, String user) {
        this.ctx = ctx;
        this.user = user;
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
    }
}
