package com.bradleyboxer.scavengerhunt;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

public class ScavengerHuntCreatorActivity extends AppCompatActivity {

    LinearLayout clueDisplay;
    List<ClueView> clueViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scavenger_hunt_creator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        clueDisplay = findViewById(R.id.clueViewContainer);
        clueViewList = new ArrayList<>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newClue();
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //       .setAction("Action", null).show();
            }
        });

    }

    private void newClue() {
        Intent intent = new Intent(this, ClueCreatorActivity.class);
        startActivityForResult(intent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            Clue clue = (Clue) data.getSerializableExtra("clue");
            if(clue!=null) {
                //display clue
                ClueView clueView = new ClueView(this, clue);
                clueDisplay.addView(clueView);
                clueViewList.add(clueView);
            }
        }
    }

    public ClueView getClueViewFromButton(View v) {
        if(v instanceof Button) {
            Object obj = v.getParent().getParent().getParent();
            if(obj instanceof ClueView) {
                return (ClueView) obj;
            }
        }
        return null;
    }

    public void onClueDelete(View v) {
        ClueView clueView = getClueViewFromButton(v);
        clueViewList.remove(clueView);
        clueDisplay.removeView(clueView);
    }

    public void onClueMoveUp(View v) {
        ClueView clueView = getClueViewFromButton(v);
        int position = clueViewList.indexOf(clueView);
        if(position>0) {
            clueViewList.remove(position);
            clueDisplay.removeViewAt(position);

            clueViewList.add(position-1, clueView);
            clueDisplay.addView(clueView, position-1);
        }
    }
}
