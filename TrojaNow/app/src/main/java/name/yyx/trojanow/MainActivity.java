package name.yyx.trojanow;

import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.astuetz.PagerSlidingTabStrip;

import name.yyx.trojanow.controller.Controller;
import name.yyx.trojanow.serverpush.IServerPush;
import name.yyx.trojanow.serverpush.ServerPushManager;


public class MainActivity extends ActionBarActivity {

    public static final String TAG = "MainActivity";

    private Controller controller;
    private ViewPager viewpager;
    private PagerSlidingTabStrip strip;
    private MainPagerAdapter adapter;
    private ServerPushManager pushMgr;
    private NotificationManager notifMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        controller = (Controller)getApplicationContext();

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

        // notification manager
        notifMgr = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // push notification manager
        pushMgr = new ServerPushManager(getApplicationContext(), controller.getUsername(), new IServerPush() {

            @Override
            public void newStatus() {
                addNotificationDot(0);
            }
            @Override
            public void newFollow(String user) {
                String msg = user + " follows you!";
                int id = 0;
                showNotification(id, msg);
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
//            case R.id.menu_item_new_chat:
//                break;
            case R.id.menu_item_add_follow:
//                showNotification(0, "message");//for test
                DialogFragment dlg = new AddFollowDialogFragment();
                dlg.show(getSupportFragmentManager(), "add follow dlg");
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

    public void showNotification(int id, String msg) {

        NotificationCompat.Builder mBuilder;
        mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(msg);

        Intent intent = new Intent(this, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(MainActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(intent);
//        PendingIntent resultPendingIntent =
//                stackBuilder.getPendingIntent(
//                        0,
//                        PendingIntent.FLAG_UPDATE_CURRENT
//                );
//        mBuilder.setContentIntent(resultPendingIntent);
        notifMgr.notify(id, mBuilder.build());
    }

    /**
     * Handle notification dot
     */
    public class MainPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTitleProvider {

        private int[] notificationState = {0, 0};
        private final int[] ICON = {R.drawable.notification_dot_null, R.drawable.notification_dot};
        private final String[] TITLE = {"Statuses", "Follows"};

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
                    return FollowFragment.newInstance();
                default: //not used
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