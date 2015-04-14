package name.yyx.trojanow;

import android.content.Context;
import android.util.Log;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class StatuesFragment extends Fragment {

    private SimpleAdapter adapter;
    private PullToRefreshListView pullToRefreshView;
    private List<Map<String, Object>> data;

    public static StatuesFragment newInstance() {
        return new StatuesFragment();
    }

    public StatuesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            // list content
            data = new ArrayList<Map<String, Object>>();
            for (int i = 0; i < 20; i++) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("content", "abcdefgabcdefgabcdefgabcdefgabcdefgabcdefg");
                map.put("author", "yyx");
                map.put("date", "2015-04");
                map.put("location", "10,200");
                map.put("temperature", "9");
                data.add(map);
            }
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
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("content", "newnewnew");
                        map.put("author", "Great");
                        map.put("date", "2015-09");
                        map.put("location", "");
                        map.put("temperature", "");

                        data.add(0, map);
                        adapter.notifyDataSetChanged();
                        pullToRefreshView.refreshDrawableState();
                        pullToRefreshView.onRefreshComplete();
                    }
                }, 2000);
            }
        });

        pullToRefreshView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                Toast.makeText(getActivity(), "position " + position + " clicked", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

    public class StatusAdapter extends SimpleAdapter {

        public StatusAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, R.layout.listview_item_status, from, to);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // delete icon if location or temperature not exists
            View view = super.getView(position, convertView, parent);
            TextView location = (TextView)view.findViewById(R.id.status_location);
            TextView temperature = (TextView)view.findViewById(R.id.status_temperature);
            if(location.getText().toString().equals("")) {
                location.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            if(temperature.getText().toString().equals("")) {
                temperature.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            }
            return view;
        }
    }

}
