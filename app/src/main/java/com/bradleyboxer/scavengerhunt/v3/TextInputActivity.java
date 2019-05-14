package com.bradleyboxer.scavengerhunt.v3;

import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bradleyboxer.scavengerhunt.R;

public class TextInputActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_text_input);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_answer_text_clue);
        setCheckedId(R.id.nav_answer_text_clue);

        super.onCreate(savedInstanceState);
    }
}
