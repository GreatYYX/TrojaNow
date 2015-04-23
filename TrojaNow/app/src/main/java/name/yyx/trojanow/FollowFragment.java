package name.yyx.trojanow;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.andraskindler.quickscroll.QuickScroll;
import com.andraskindler.quickscroll.Scrollable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import name.yyx.trojanow.controller.Controller;
import name.yyx.trojanow.widget.ProgressCircle;

public class FollowFragment extends Fragment {

    private Controller controller;
    private FollowAdapter adapter;
    private List<Map<String, Object>> data;
    private QuickScroll scroll;
    private EditText search;
    private ListView list;
    private Handler handler;
    private Runnable run;

    public static FollowFragment newInstance() {
        FollowFragment fragment = new FollowFragment();
        return fragment;
    }

    public FollowFragment() {
        // Required empty public constructor
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if (savedInstanceState != null) {
            data = (List<Map<String, Object>>) savedInstanceState.getSerializable("data");
        } else {
            data = new ArrayList<Map<String, Object>>();
        }
        adapter = new FollowAdapter(
                getActivity(), data, R.layout.listview_item_follow,
                new String[]{"user", "nickname"},
                new int[]{R.id.follow_username, R.id.follow_nickname}
        );

        ViewGroup rootView = (ViewGroup)inflater.inflate(R.layout.fragment_follow, container, false);
        list = (ListView)rootView.findViewById(R.id.list_follow);
        scroll = (QuickScroll)rootView.findViewById(R.id.quickscroll);
        search = (EditText)rootView.findViewById(R.id.et_search);
        list.setAdapter(adapter);

        scroll.init(QuickScroll.TYPE_INDICATOR_WITH_HANDLE, list, adapter, QuickScroll.STYLE_HOLO);
        scroll.setFixedSize(1);
        scroll.setHandlebarColor(QuickScroll.GREY_DARK, QuickScroll.GREY_LIGHT, QuickScroll.GREY_SCROLLBAR);
        scroll.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 48);

        search.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        handler = new MessageHandler();
        refreshList();

        return rootView;
    }

    public void refreshList() {
        run = new Runnable() {
            @Override
            public void run() {
                data.clear();
                List<Map<String, Object>> followers = controller.listFollowers();
                for(int i = 0; i < followers.size(); i++) {
                    data.add(followers.get(i));
                }
                new Message().obtain(handler, ProgressCircle.END).sendToTarget();
            }
        };
        new Thread(run).start();
    }

    public class FollowAdapter extends SimpleAdapter implements Scrollable {

        public FollowAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
            super(context, data, resource, from, to);
        }

        @Override
        public String getIndicatorForPosition(int childposition, int groupposition) {
            String str = data.get(childposition).get("user").toString();
            return Character.toString(str.charAt(0));
        }

        @Override
        public int getScrollPosition(int childposition, int groupposition) {
            return childposition;
        }
    }

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case ProgressCircle.END:
                    adapter.notifyDataSetChanged();
                    break;
                default:
                    super.handleMessage(msg);
            }
            removeMessages(msg.what);
        }
    }
}
