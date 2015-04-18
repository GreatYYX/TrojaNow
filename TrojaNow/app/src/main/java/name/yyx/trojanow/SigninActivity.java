package name.yyx.trojanow;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import name.yyx.trojanow.controller.Controller;


public class SigninActivity extends ActionBarActivity {

    private Controller controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);
        controller = (Controller)getApplicationContext();



        Button clickButton = (Button) findViewById(R.id.btn_singin);
        clickButton.setOnClickListener( new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        TextView textView1 = (TextView)findViewById(R.id.et_username);
                        TextView textView2 = (TextView)findViewById(R.id.et_password);
                        String username = textView1.getText().toString();
                        String password = textView2.getText().toString();

                        Log.i("signin", "in run");
                        Log.i("username", username);
                        Log.i("pwd", password);

                        if (controller.signIn(username,password)){
                            Log.i("controller", "controller is running");
                            finish();
                        }
                        else{
                            Toast.makeText(SigninActivity.this,"False",Toast.LENGTH_SHORT ).show();
                        }
                    }
                }).start();
            }
        });
    }

}
