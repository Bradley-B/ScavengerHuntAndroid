package com.bradleyboxer.scavengerhunt.v2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bradleyboxer.scavengerhunt.R;

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
            Clue newClue = (Clue) data.getSerializableExtra("clue");
            if(newClue!=null) {
                //display clue
                ClueView clueView = new ClueView(this, newClue);
                clueDisplay.addView(clueView);
                clueViewList.add(clueView);

            }
        } else if(requestCode == 2 && resultCode == RESULT_OK) {
            Clue editedClue = (Clue) data.getSerializableExtra("clue");
            int clueNum = data.getIntExtra("clueNumber", -1);
            if(editedClue!=null && clueNum>=0) {
                ClueView clueView = new ClueView(this, editedClue);
                clueDisplay.removeViewAt(clueNum);
                clueViewList.remove(clueNum);
                clueDisplay.addView(clueView, clueNum);
                clueViewList.add(clueNum, clueView);
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

    public void onClueEdit(View v) {
        ClueView clueView = getClueViewFromButton(v);
        Intent intent = new Intent(this, ClueCreatorActivity.class);
        intent.putExtra("clue", clueView.getClue());
        intent.putExtra("requestCode", 2);
        intent.putExtra("clueNumber", clueViewList.indexOf(clueView));
        startActivityForResult(intent, 2);
    }

    public void deleteClue(View v) {
        ClueView clueView = getClueViewFromButton(v);
        clueViewList.remove(clueView);
        clueDisplay.removeView(clueView);
    }

    public void onClueDelete(final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this clue?");
        builder.setPositiveButton("Yes, delete this clue", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteClue(v);
            }
        });
        builder.setNegativeButton("No, wait, dont!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //user cancelled, do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit scavenger hunt creation? Once you leave this screen, the clues cannot be viewed or modified.");
        builder.setPositiveButton("Finalize Scavenger Hunt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ArrayList<Clue> clueList = new ArrayList<>();
                for(ClueView clueView : clueViewList) {
                    clueList.add(clueView.getClue());
                }

                final Intent intent = new Intent();
                intent.putExtra("clueList", clueList);
                setResult(RESULT_OK, intent);
                finish();

            }
        });
        builder.setNegativeButton("Keep Working", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //do nothing, they will try again
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }
}
