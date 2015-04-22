package name.yyx.trojanow;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import name.yyx.trojanow.controller.Controller;
import name.yyx.trojanow.widget.ProgressCircle;

public class AddFollowDialogFragment extends DialogFragment {

    private Controller controller;
    private AlertDialog.Builder dlg;
    private Runnable run;
    private Context ctx;
    private Handler handler;
    private ProgressCircle pCircle;
    private EditText etFollow;

    public AddFollowDialogFragment() {
        super();
    }

    public static AddFollowDialogFragment newInstance() {
        return new AddFollowDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        handler = new MessageHandler();
        ctx = getActivity();
        etFollow = new EditText(ctx);
        pCircle = new ProgressCircle(ctx);
        controller = (Controller)getActivity().getApplicationContext();

        dlg = new AlertDialog.Builder(ctx)
                .setTitle("Please enter a user you want to follow:")
//                .setIcon(android.R.drawable.ic_dialog_info)
                .setView(etFollow)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        pCircle.show();
                        run = new Runnable() {
                            @Override
                            public void run() {
                                String follow = etFollow.getText().toString();
                                if(controller.follow(follow)){
                                    new Message().obtain(handler, ProgressCircle.SUCCESS).sendToTarget();

                                }else{
                                    Log.i("addFollowDialog", "controller error");
                                }
                            }
                        };
                        new Thread(run).start();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddFollowDialogFragment.this.getDialog().cancel();
                    }
                });
        return dlg.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        final Resources res = getResources();
        final int greenDark = res.getColor(R.color.green_dark);

        // Title
        final int titleId = res.getIdentifier("alertTitle", "id", "android");
        final View title = getDialog().findViewById(titleId);
        if (title != null) {
            ((TextView)title).setTextColor(greenDark);
        }

        // Title divider
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = getDialog().findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(greenDark);
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(pCircle != null) {
            pCircle.dismiss();
        }
    }

    class MessageHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch(msg.what) {
                case ProgressCircle.SUCCESS:
                    Toast.makeText(ctx, "Follow success!", Toast.LENGTH_SHORT).show();
                    break;
                case ProgressCircle.ERROR:
                    Toast.makeText(ctx, "Follow failed!", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    super.handleMessage(msg);
            }
            pCircle.dismiss();
            removeMessages(msg.what);
        }
    }
}
