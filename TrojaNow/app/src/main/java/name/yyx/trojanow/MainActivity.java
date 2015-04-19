package name.yyx.trojanow;

import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import name.yyx.trojanow.serverpush.IServerPush;
import name.yyx.trojanow.serverpush.ServerPushManager;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MainActivity";

    private ViewPager viewpager;
    private PagerSlidingTabStrip strip;
    private MainPagerAdapter adapter;
    private ServerPushManager pushMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        pushMgr = new ServerPushManager(getApplicationContext(), "yyx", new IServerPush() {
            @Override
            public void newStatus() {
                addNotificationDot(0);
            }
        });
//        pushMgr.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        pushMgr.stop();
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