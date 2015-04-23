package name.yyx.trojanow;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import name.yyx.trojanow.controller.Controller;
import name.yyx.trojanow.widget.ProgressCircle;


public class RegisterActivity extends ActionBarActivity {

    public static final String TAG = "RegisterActivity";

    private Controller controller;
    private ProgressCircle pCircle;
    private Handler handler;

    private Button btnRegister;
    private EditText etUser;
    private EditText etPwd;
    private EditText etPwdConfirm;
    private EditText etNickname;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        controller = (Controller)getApplicationContext();

        etUser = (EditText)findViewById(R.id.et_reg_user);
        etPwd = (EditText)findViewById(R.id.et_reg_pwd);
        etPwdConfirm = (EditText)findViewById(R.id.et_reg_pwd_confirm);
        etNickname = (EditText)findViewById(R.id.et_reg_nickname);
        btnRegister = (Button)findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String user = etUser.getText().toString();
                final String pwd = etPwd.getText().toString();
                String pwdConfirm = etPwdConfirm.getText().toString();
                final String nickname = etNickname.getText().toString();

                if(user.equals("") || pwd.equals("") || nickname.equals("")) {
                    Toast.makeText(RegisterActivity.this, "Invalid information!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(!pwd.equals(pwdConfirm)) {
                    Toast.makeText(RegisterActivity.this, "Please check your password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                pCircle = new ProgressCircle(RegisterActivity.this);
                pCircle.show();
                Runnable run = new Runnable() {
                    @Override
                    public void run() {
                        if(controller.registration(user, pwd, nickname)) {
                            new Message().obtain(handler, ProgressCircle.SUCCESS).sendToTarget();
                        } else {
                            new Message().obtain(handler, ProgressCircle.ERROR).sendToTarget();
                        }
                    }
                };
                new Thread(run).start();
            }
        });

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

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case ProgressCircle.SUCCESS:
                    Toast.makeText(RegisterActivity.this, "Register success!", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                    break;
                case ProgressCircle.ERROR:
                    Toast.makeText(RegisterActivity.this, "Register failed!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
            pCircle.dismiss();
            removeMessages(msg.what);
        }
    }
}
