package com.example.airon.rsrpechhulp_hristo.activities.About;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.airon.rsrpechhulp_hristo.R;
import com.example.airon.rsrpechhulp_hristo.activities.Main.MainActivity;

import org.w3c.dom.Text;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout homeUP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbarTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarTitle.setText(R.string.label_activity_about);
        homeUP = (LinearLayout)findViewById(R.id.toolbar_home_up);
        homeUP.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }

    @Override
    public void onClick(View v) {
        if(v == homeUP) {
            onBackPressed();
        }
    }
}
