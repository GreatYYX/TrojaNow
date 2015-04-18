package name.yyx.trojanow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import name.yyx.trojanow.controller.Controller;
import android.widget.EditText;

import name.yyx.trojanow.widget.ProgressCircle;


public class SigninActivity extends ActionBarActivity {

    private Controller controller;
    private ProgressCircle pCircle;
    private Button btnSignin;
    private Button btnRegister;
    private EditText etUser;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        controller = (Controller)getApplicationContext();

        // splash
//        startActivity(new Intent(SigninActivity.this, SplashActivity.class));

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

                // pop progress circle
                pCircle.show();

                //execute working thread
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        etUser = (EditText)findViewById(R.id.et_username);
                        etPassword = (EditText)findViewById(R.id.et_password);
                        String user = etUser.getText().toString();
                        String password = etPassword.getText().toString();

                        if(controller.signIn(user,password)) {
                            startActivity(new Intent(SigninActivity.this, MainActivity.class));
                            finish();
                        } else {
//                            Toast.makeText(SigninActivity.this,"False",Toast.LENGTH_SHORT ).show();
                        }
                        pCircle.dismiss();
                    }
                };
                new Thread(run).start();
            }
        });
        btnRegister = (Button)findViewById(R.id.btn_register);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // avoid window leak
        if(pCircle != null) {
            pCircle.dismiss();
        }
    }

}
