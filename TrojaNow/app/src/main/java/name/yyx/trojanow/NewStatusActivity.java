package name.yyx.trojanow;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;


public class NewStatusActivity extends ActionBarActivity {
    private static final String TAG = "NewStatusActivity";
    private ProgressDialog pd;
    private EditText editTextStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_status);

        editTextStatus = (EditText)findViewById(R.id.et_status);

        // bundle anonymous checkbox with location checkbox
        CheckBox anonymous = (CheckBox)findViewById(R.id.cb_anonymous);
        anonymous.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            CheckBox location = (CheckBox)findViewById(R.id.cb_location);
            boolean location_checked;
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    location.setEnabled(false);
                    location_checked = location.isChecked();
                    location.setChecked(true);
                } else {
                    location.setChecked(location_checked);
                    location.setEnabled(true);
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
        if(!editTextStatus.getText().toString().equals("")) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.discard_new_status_confirmation))
                    .setCancelable(false)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                            NewStatusActivity.this.finish();
                            NewStatusActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        } else {
            super.onBackPressed();
        }
//        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // avoid window leak
        if(pd != null) {
            pd.dismiss();
        }
    }

    public boolean post() {
        // hide keyboard
        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editTextStatus.getWindowToken(), 0);

        // invalid status
        if(editTextStatus.getText().toString().equals("")) {
            Toast.makeText(NewStatusActivity.this, "Empty!", Toast.LENGTH_SHORT).show();
//            imm.showSoftInput(editTextStatus, 0);
            return false;
        }

        // post
        pd = new ProgressDialog(this, R.style.MyTheme);
        pd.setCancelable(false);
        pd.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        pd.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(NewStatusActivity.this, "post successfully", Toast.LENGTH_SHORT).show();
                NewStatusActivity.super.onBackPressed();
            }
        }, 2000);
        return true;
    }

}
