package com.bradleyboxer.scavengerhunt.v3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.bradleyboxer.scavengerhunt.R;

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
        String triggeringClueName = triggeringIntent.getStringExtra("clueName");

        LinearLayout frame = findViewById(R.id.clue_view_layout);
        ScavengerHunt scavengerHunt = FileUtil.loadScavengerHunt(this);
        boolean displayInactiveClues = scavengerHunt.areInactiveCluesDisplayed();
        for(Clue clue : scavengerHunt.getClueList()) {
            if(displayInactiveClues || clue.isActive() || clue.isSolved()) {
                ClueIndividualView clueView = new ClueIndividualView(this, clue);
                frame.addView(clueView);

                if(clue.getName().equals(triggeringClueName)) {
                    AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(this);
                    dlgAlert.setMessage(clue.getSolvedText());
                    dlgAlert.setTitle("Solution Message");
                    dlgAlert.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
                        @Override public void onClick(DialogInterface dialogInterface, int i) {}
                    });
                    dlgAlert.create().show();
                }
            }
        }

        super.onCreate(savedInstanceState);
        setCheckedId(R.id.nav_clues);
    }


}
