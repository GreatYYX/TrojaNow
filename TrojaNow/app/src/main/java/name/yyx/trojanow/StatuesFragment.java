package name.yyx.trojanow;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import name.yyx.trojanow.controller.Controller;
import name.yyx.trojanow.widget.ProgressCircle;


public class StatuesFragment extends Fragment {

    private Controller controller;
    private SimpleAdapter adapter;
    private PullToRefreshListView pullToRefreshView;
    private List<Map<String, Object>> data;
    private Handler handler;
    private Runnable run;
    private boolean acceptAnonymous;

    public static StatuesFragment newInstance() {
        return new StatuesFragment();
    }

    public StatuesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = (Controller)getActivity().getApplicationContext();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("data", (Serializable) data);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            data = (List<Map<String, Object>>) savedInstanceState.getSerializable("data");
        } else {
            data = new ArrayList<Map<String, Object>>();
        }
        adapter = new StatusAdapter(
                getActivity(), data, R.layout.listview_item_status,
                new String[]{"content", "author", "date", "location", "temperature"},
                new int[]{R.id.status_content, R.id.status_author, R.id.status_date, R.id.status_location, R.id.status_temperature}
        );

        View rootView = inflater.inflate(R.layout.fragment_statues, container, false);
        pullToRefreshView = (PullToRefreshListView)rootView.findViewById(R.id.pull_to_refresh_listview);
        pullToRefreshView.setAdapter(adapter);
        pullToRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                // Do work to refresh the list here.

                acceptAnonymous = controller.readAccept(getActivity());
                handler = new MessageHandler();
                run = new Runnable() {
                    @Override
                    public void run() {
                        //list status
                        data.clear();
                        List<Map<String, Object>> statuses = controller.listStatuses(acceptAnonymous);
                        for(int i = 0; i < statuses.size(); i++){
                            data.add(statuses.get(i));
                        }
                        new Message().obtain(handler, ProgressCircle.END).sendToTarget();
                    }
                };
                new Thread(run).start();
            }
        });

        // auto refresh
        pullToRefreshView.setRefreshing();

        return rootView;
    }

    public class StatusAdapter extends SimpleAdapter {

        public StatusAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, R.layout.listview_item_status, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            TextView location = (TextView)view.findViewById(R.id.status_location);
            TextView temperature = (TextView)view.findViewById(R.id.status_temperature);

            // reset location and temperature icon to fix view refresh bug
            location.setCompoundDrawablesWithIntrinsicBounds(R.drawable.list_location, 0, 0, 0);
            temperature.setCompoundDrawablesWithIntrinsicBounds(R.drawable.list_temperature, 0, 0, 0);

            // delete icon if location or temperature not exists
            if(data.get(position).get("location") == null) {
                location.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            if(data.get(position).get("temperature") == null) {
                temperature.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            return view;
        }
    }

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case ProgressCircle.END:
                    adapter.notifyDataSetChanged();
//                    pullToRefreshView.requestLayout();
                    pullToRefreshView.refreshDrawableState();
                    pullToRefreshView.onRefreshComplete();
                    break;
                default:
                    super.handleMessage(msg);
            }
            removeMessages(msg.what);
        }
    }
}
