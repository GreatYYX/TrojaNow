package name.yyx.trojanow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import org.json.JSONException;
import org.json.JSONObject;

import name.yyx.trojanow.mqtt.MqttManager;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MainActivity";

    private ViewPager viewpager;
    private PagerSlidingTabStrip strip;
    private MainPagerAdapter adapter;
    private MqttManager mqttMgr;
    private ServiceReceiver recv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // splash
//        Intent intentSplash = new Intent(this, SplashActivity.class);
//        startActivity(intentSplash);

        // sign in
        Intent intent = new Intent(this, SigninActivity.class);
        startActivity(intent);

        setContentView(R.layout.activity_main);

        // page view
        adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewpager = (ViewPager)findViewById(R.id.viewpager);
        viewpager.setOffscreenPageLimit(3);
        viewpager.setAdapter(adapter);

        // slide tap strip
        strip = (PagerSlidingTabStrip)findViewById(R.id.tabstrip);
        strip.setShouldExpand(true);
        strip.setViewPager(viewpager);
        strip.setDividerColor(Color.TRANSPARENT);
        strip.setUnderlineHeight(3);
        strip.setIndicatorHeight(6);
        strip.setIndicatorColor(getResources().getColor(R.color.green_dark));
        strip.setBackgroundColor(getResources().getColor(R.color.green_light));

        mqttMgr = new MqttManager(getApplicationContext(), "yyx");
        mqttMgr.start();

        recv = new ServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("name.yyx.trojanow.mqtt.RECV_MSG");
        registerReceiver(recv, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mqttMgr.stop();
        unregisterReceiver(recv);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent = null;
        switch (id) {
            case R.id.menu_item_new_status:
                intent = new Intent(this, NewStatusActivity.class);
                startActivity(intent);
                break;
            case R.id.menu_item_new_chat:
                addNotificationDot(0);
                break;
            case R.id.menu_item_add_friend:
                removeNotificationDot(0);
                break;
            case R.id.menu_item_settings:
                intent = new Intent(this, SettingActivity.class);
                startActivity(intent);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addNotificationDot(int position) {
        adapter.addNotificationDot(position);
        strip.notifyDataSetChanged();
    }

    public void removeNotificationDot(int position) {
        adapter.removeNotificationDot(position);
        strip.notifyDataSetChanged();
    }

    /**
     * Handle broadcast message from service
     */
    public class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d(TAG, "receive from service");
            Bundle bundle = intent.getExtras();
            String data = bundle.getString("data");

            try {
                JSONObject serviceMsg = new JSONObject(data);
                String type = serviceMsg.getString("type");
                if(type.equals("NEW_STATUS")) {
                    addNotificationDot(0);
                }
            } catch(JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Handle notification dot
     */
    public class MainPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTitleProvider {

        private int[] notificationState = {0, 0, 0};
        private final int[] ICON = {R.drawable.notification_dot_null, R.drawable.notification_dot};
        private final String[] TITLE = {"Statuses", "Friends", "Chat"};

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLE[position];
        }

        @Override
        public int getCount() {
            return TITLE.length;
        }

        @Override
        public int getPageIconResId(int position) {
            return ICON[notificationState[position]];
        }

        @Override
        public Fragment getItem(int i) {
            switch(i) {
                case 0:
                    return StatuesFragment.newInstance();
                case 1:
                    return FriendFragment.newInstance();
                default:
                    return BlankFragment.newInstance(TITLE[i]);
            }
        }

        public void addNotificationDot(int position) {
            notificationState[position] = 1;
        }

        public void removeNotificationDot(int position) {
            notificationState[position] = 0;
        }
    }
}