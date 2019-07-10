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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;

import com.bradleyboxer.scavengerhunt.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClueViewActivity extends MenuActivity implements AdapterView.OnItemSelectedListener {

    ScavengerHuntDatabase scavengerHuntDatabase;
    LinearLayout frame;

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

        frame = findViewById(R.id.clue_view_layout);
        scavengerHuntDatabase = FileUtil.loadScavengerHuntDatabase(this);
        ScavengerHunt activeScavengerHunt = scavengerHuntDatabase.getActiveScavengerHunt(this);

        if(activeScavengerHunt!=null) {
            populateClueList(activeScavengerHunt, triggeringClueName);

            List<ScavengerHunt> scavengerHunts = scavengerHuntDatabase.getScavengerHunts();
            Spinner spinner = (Spinner) findViewById(R.id.scavengerhunt_spinner_clueview);
            ArrayAdapter<ScavengerHunt> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, scavengerHunts);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);
            spinner.setSelection(adapter.getPosition(activeScavengerHunt));

        }

        super.onCreate(savedInstanceState);
        setCheckedId(R.id.nav_clues);
    }

    private void populateClueList(ScavengerHunt scavengerHunt, String triggeringClueName) {
        boolean displayInactiveClues = scavengerHunt.areInactiveCluesDisplayed();
        List<Clue> reversedList = new ArrayList<>(scavengerHunt.getClueList());
        Collections.reverse(reversedList);
        frame.removeAllViews();

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == QrScanner.QR_REQUEST_CODE) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra(QrScanner.QR_CODE_KEY, data.getSerializableExtra(QrScanner.QR_CODE_KEY));
            useIntent(intent);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        ScavengerHunt scavengerHunt = (ScavengerHunt) parent.getItemAtPosition(pos);
        populateClueList(scavengerHunt, null);
        scavengerHuntDatabase.setActiveScavengerHunt(this, scavengerHunt.getUuid());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        frame.removeAllViews();
    }

}
