package name.yyx.trojanow.widget;

import android.app.ProgressDialog;
import android.content.Context;

import name.yyx.trojanow.R;

public class ProgressCircle extends ProgressDialog {

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
