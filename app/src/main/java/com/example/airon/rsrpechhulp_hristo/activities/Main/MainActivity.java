package com.example.airon.rsrpechhulp_hristo.activities.Main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;


import com.example.airon.rsrpechhulp_hristo.R;
import com.example.airon.rsrpechhulp_hristo.activities.About.AboutActivity;
import com.example.airon.rsrpechhulp_hristo.activities.Map.MapActivity;


public class MainActivity extends AppCompatActivity  implements View.OnClickListener{

    private RelativeLayout mapButton, infoButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Check for location services permissions
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //Request permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        mapButton =  findViewById(R.id.activity_main_btn_map);
        mapButton.setOnClickListener(this);

        //Initializes the info button in case that the device is a Tablet
        if (isTablet(MainActivity.this)) {
            infoButton = (RelativeLayout) findViewById(R.id.activity_main_btn_info);
            infoButton.setOnClickListener(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults.length > 0
                        && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    //permission denied - close the app
                    onBackPressed();
                }
            return;
            }
        }
    }


    @Override
    public void onClick(View view) {
        if (view == mapButton) {
            startActivity(new Intent(getApplicationContext(), MapActivity.class));
            finish();
        }

        if (view == infoButton) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            finish();
        }
    }

    /**
     * Returns if the device is tablet or not. Source code from Google I/O
     * @param context The activity that checks
     * @return true if the device is a tablet
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!isTablet(MainActivity.this)) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.toolbar_menu, menu);
            // return true so that the menu pop up is opened
            return true;
        }
        return false;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Starts About activity and destroys the current activity
        if (item.getItemId() == R.id.activity_main_btn_info) {
            startActivity(new Intent(getApplicationContext(), AboutActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
