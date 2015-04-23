package name.yyx.trojanow;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class BlankFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private String title;

    public static BlankFragment newInstance(String title) {
        BlankFragment fragment = new BlankFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        fragment.setArguments(args);
        return fragment;
    }

    public BlankFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(ARG_TITLE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_blank, container, false);
        TextView tv = (TextView)rootView.findViewById(R.id.textTitle);
        tv.setText(this.title);
        return rootView;
    }

}
