package name.yyx.trojanow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import name.yyx.trojanow.controller.Controller;
import name.yyx.trojanow.service.ISensor;
import name.yyx.trojanow.service.Sensor;
import name.yyx.trojanow.widget.ProgressCircle;


public class NewStatusActivity extends ActionBarActivity {
    private static final String TAG = "NewStatusActivity";
    private Controller controller;
    private ProgressCircle pCircle;
    private Sensor sensor;
    private EditText etStatus;
    private CheckBox cbAnonymous;
    private CheckBox cbLocation;
    private CheckBox cbTemperature;

    private int temperature;
    private String[] location;

    private Handler handler;
    private Runnable run;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_status);
        controller = (Controller)getApplicationContext();

        etStatus = (EditText)findViewById(R.id.et_status);
        cbAnonymous = (CheckBox)findViewById(R.id.cb_anonymous);
        cbLocation = (CheckBox)findViewById(R.id.cb_location);
        cbTemperature = (CheckBox)findViewById(R.id.cb_temperature);

        // bind anonymous checkbox with location checkbox
        cbAnonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            boolean location_checked;
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    cbLocation.setEnabled(false);
                    location_checked = cbLocation.isChecked();
                    cbLocation.setChecked(true);
                } else {
                    cbLocation.setChecked(location_checked);
                    cbLocation.setEnabled(true);
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_status, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            // back button on actionbar
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.menu_item_post:
                post();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // draft
        if(!etStatus.getText().toString().equals("")) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.discard_new_status_confirmation))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            NewStatusActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        } else {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // avoid window leak
        if(pCircle != null) {
            pCircle.dismiss();
        }
    }

    public void post() {
        // hide keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        // get content
        final String status = etStatus.getText().toString();
        final boolean isAnonymous = cbAnonymous.isChecked();
        final boolean hasLocation = cbLocation.isChecked();
        final boolean hasTemperature = cbTemperature.isChecked();

        // invalid status
        if(status.equals("")) {
            Toast.makeText(NewStatusActivity.this, "Empty status!", Toast.LENGTH_SHORT).show();
            return;
        }

        // set sensor
        handler = new MessageHandler();
        sensor = new Sensor(getApplicationContext(), new ISensor() {
            @Override
            public void onDataReceived() {
                if(sensor.canGetInfo()) {
                    temperature = sensor.getTemperature();
                    location = sensor.getLocation();
                    new Thread(run).start();
                } else {
                    pCircle.dismiss();
                    Toast.makeText(getApplicationContext(), "Can not get start data", Toast.LENGTH_SHORT).show();
                }
                sensor.removeListener(); // remove listener after get first message
            }
            @Override
            public void onFail() {
                pCircle.dismiss();
                Toast.makeText(getApplicationContext(), "Can not get sensor data", Toast.LENGTH_SHORT).show();
                sensor.removeListener();
            }
        });

        // set post thread
        run = new Runnable() {
            @Override
            public void run() {

                boolean isSuccess = false;
                if (hasTemperature && hasLocation) {
                    isSuccess = controller.createStatus(status, isAnonymous, Integer.toString(temperature), location);
                } else if (!hasTemperature && hasLocation) {
                    isSuccess = controller.createStatus(status, isAnonymous, null, location);
                } else if (!hasLocation && hasTemperature) {
                    isSuccess = controller.createStatus(status, isAnonymous, Integer.toString(temperature), null);
                }

                if (isSuccess) {
                    new Message().obtain(handler, ProgressCircle.SUCCESS).sendToTarget();
                } else {
                    new Message().obtain(handler, ProgressCircle.ERROR).sendToTarget();
                }
            }
        };

        // start post
        pCircle = new ProgressCircle(this);
        pCircle.show();
        if(hasLocation || hasTemperature) {
            sensor.update();
        } else {
            new Thread(run).start();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case ProgressCircle.SUCCESS:
                    Toast.makeText(NewStatusActivity.this, "post successfully", Toast.LENGTH_SHORT).show();
                    finish();
                    break;
                case ProgressCircle.ERROR:
                    Toast.makeText(NewStatusActivity.this, "Post failed!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
            pCircle.dismiss();
            removeMessages(msg.what);
        }
    }

}
