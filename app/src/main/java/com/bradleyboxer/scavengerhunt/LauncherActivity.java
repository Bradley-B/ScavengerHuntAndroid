package com.bradleyboxer.scavengerhunt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LauncherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
    }

    public void onCreateButton(View v) {
        Intent intent = new Intent(this, ScavengerHuntCreatorActivity.class);
        startActivity(intent);

    }
}
