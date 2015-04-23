package name.yyx.trojanow;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import name.yyx.trojanow.controller.Controller;
import name.yyx.trojanow.widget.ProgressCircle;


public class SigninActivity extends ActionBarActivity {

    private Controller controller;
    private ProgressCircle pCircle;
    private Button btnSignin;
    private Button btnRegister;
    private EditText etUser;
    private EditText etPassword;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = (Controller)getApplicationContext();

        // splash
        startActivity(new Intent(SigninActivity.this, SplashActivity.class));

        // judge if already signed in
        if(controller.isSignedIn()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_signin);
        setTitle(getString(R.string.title_activity_signin));

        pCircle = new ProgressCircle(SigninActivity.this);
        btnSignin = (Button)findViewById(R.id.btn_signin);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // hide keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                //execute working thread
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        new Message().obtain(handler, ProgressCircle.START).sendToTarget();

                        etUser = (EditText)findViewById(R.id.et_username);
                        etPassword = (EditText)findViewById(R.id.et_password);
                        String user = etUser.getText().toString();
                        String password = etPassword.getText().toString();

                        if(controller.signIn(user, password)) {
                            new Message().obtain(handler, ProgressCircle.SUCCESS).sendToTarget();
                        } else {
                            new Message().obtain(handler, ProgressCircle.ERROR).sendToTarget();
                        }

                        new Message().obtain(handler, ProgressCircle.END).sendToTarget();
                    }
                };
                new Thread(run).start();
            }
        });
        btnRegister = (Button)findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SigninActivity.this, RegisterActivity.class));
            }
        });

        // update UI
        handler = new MessageHandler();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // avoid window leak
        if(pCircle != null) {
            pCircle.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case ProgressCircle.START:
                    pCircle.show();
                    break;
                case ProgressCircle.END:
                    pCircle.dismiss();
                    break;
                case ProgressCircle.SUCCESS:
                    startActivity(new Intent(SigninActivity.this, MainActivity.class));
                    finish();
                    break;
                case ProgressCircle.ERROR:
                    Toast.makeText(SigninActivity.this, "Sign in failed!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
            removeMessages(msg.what);
        }
    }
}
