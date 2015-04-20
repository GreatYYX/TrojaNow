package name.yyx.trojanow.widget;

import android.app.ProgressDialog;
import android.content.Context;

import name.yyx.trojanow.R;

public class ProgressCircle extends ProgressDialog {

    public static final int START = 0;
    public static final int END = 1;
    public static final int SUCCESS = 2;
    public static final int ERROR = 3;

    public ProgressCircle(Context context) {
        super(context, R.style.ProgressCircle);
        setCancelable(false);
        setProgressStyle(android.R.style.Widget_ProgressBar_Small);
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }
}
