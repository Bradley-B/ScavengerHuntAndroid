package com.bradleyboxer.scavengerhunt.v3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.bradleyboxer.scavengerhunt.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClueViewActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_clue_view);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Well, this is embarrassing... this feature is not yet implemented", Snackbar.LENGTH_LONG) //TODO
                        .setAction("Action", null).show();
            }
        });

        Intent triggeringIntent = getIntent();
        String triggeringClueName = triggeringIntent.getStringExtra("clueName"); //TODO replace String with UUID

        LinearLayout frame = findViewById(R.id.clue_view_layout);

        ScavengerHuntDatabase scavengerHuntDatabase = FileUtil.loadScavengerHuntDatabase(this);
        ScavengerHunt scavengerHunt = scavengerHuntDatabase.getActiveScavengerHunt(this);

        if(scavengerHunt!=null) {
            boolean displayInactiveClues = scavengerHunt.areInactiveCluesDisplayed();

            List<Clue> reversedList = new ArrayList<>(scavengerHunt.getClueList());
            Collections.reverse(reversedList);

            for(Clue clue : reversedList) {
                if(displayInactiveClues || clue.isActive() || clue.isSolved()) {
                    ClueIndividualView clueView = new ClueIndividualView(getApplicationContext(), clue, this);
                    frame.addView(clueView);

                    if(clue.getName().equals(triggeringClueName)) {
                        Notifications.displayAlertDialog("Solution Message", clue.getSolvedText(), this);
                    }
                }
            }
        }

        super.onCreate(savedInstanceState);
        setCheckedId(R.id.nav_clues);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == QrScanner.QR_REQUEST_CODE) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(QrScanner.QR_CODE_KEY, data.getSerializableExtra(QrScanner.QR_CODE_KEY));
            useIntent(intent);
        }
    }

}
