package com.example.airon.rsrpechhulp_hristo.activities.Map;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.airon.rsrpechhulp_hristo.R;

/**
 * Class for a custom dialog window
 */
public class CustomDialog extends Dialog implements android.view.View.OnClickListener {

    public Activity c;
    public Dialog d;
    public RelativeLayout call,closeButton;

    public CustomDialog(Activity activity) {
        super(activity);
        this.c = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE); //Removes the title from the top of the screen
        setContentView(R.layout.custom_dialog_window);

        call = findViewById(R.id.btn_map_call_confirmed);
        closeButton = findViewById(R.id.btn_map_close_dialog);
        call.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if(v ==  call) {
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:"+ 123456));
            c.startActivity(callIntent);
        }

        if(v == closeButton) {
            dismiss();
        }
    }
}
