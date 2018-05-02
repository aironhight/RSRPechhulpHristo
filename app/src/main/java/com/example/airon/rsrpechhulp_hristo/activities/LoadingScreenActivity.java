package com.example.airon.rsrpechhulp_hristo.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.example.airon.rsrpechhulp_hristo.R;
import com.example.airon.rsrpechhulp_hristo.activities.Main.MainActivity;

public class LoadingScreenActivity extends AppCompatActivity implements Runnable{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_screen);

        //Starting a thread which will wait 2 seconds and then start the main activity, acting as a loading/splash screen
        Thread t = new Thread(LoadingScreenActivity.this);
        t.start();
        getSupportActionBar().hide();
    }

    @Override
    public void run() {
        try {
            Thread.sleep(2000);
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
