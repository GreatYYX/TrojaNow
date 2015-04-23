package name.yyx.trojanow;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;


public class SplashActivity extends Activity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
