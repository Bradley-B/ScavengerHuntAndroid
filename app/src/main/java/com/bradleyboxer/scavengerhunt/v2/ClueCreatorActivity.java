package com.bradleyboxer.scavengerhunt.v2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.bradleyboxer.scavengerhunt.R;

import java.util.ArrayList;
import java.util.List;

public class ClueCreatorActivity extends AppCompatActivity {

    LinearLayout scrollableClueSegments;

    ClueSegmentView geofenceSegment;
    List<ClueSegmentView> compassClueSegments;
    List<Button> deleteButtons;
    int requestCode = -1;
    int clueNumber = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clue_creator);

        geofenceSegment = (ClueSegmentView) findViewById(R.id.geofenceClueSegmentView);
        scrollableClueSegments = (LinearLayout) findViewById(R.id.scrollableClueSegments);
        deleteButtons = new ArrayList<>();
        compassClueSegments = new ArrayList<>();

        Intent intent = getIntent();
        requestCode = intent.getIntExtra("requestCode", -1);
        clueNumber = intent.getIntExtra("clueNumber", -1);
        if(requestCode>=0) {
            Clue clueToEdit = (Clue) intent.getSerializableExtra("clue");
            setGeofenceClueSegment(clueToEdit.getGeofenceSegment());
            for(ClueSegment compassSegment : clueToEdit.getCompassSegments()) {
                newCompassClueSegment(compassSegment);
            }
        }

    }

    public void onNewCompassClueSegment(View v) {
        newCompassClueSegment(null);
    }

    public void setGeofenceClueSegment(ClueSegment geofenceClueSegment) {
        ((ClueSegmentView)findViewById(R.id.geofenceClueSegmentView)).setClueSegmentData(geofenceClueSegment);
    }

    public void newCompassClueSegment(ClueSegment clueSegment) {
        ClueSegmentView clueSegmentView;

        if(clueSegment!=null) {
            clueSegmentView = new ClueSegmentView(this, clueSegment);
        } else {
            clueSegmentView = new ClueSegmentView(this);
        }

        Button deleteButton = new Button(this);

        deleteButton.setText(" ^ Delete Above Compass Segment ^ ");
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmRemoveDeleteButton(view);
            }
        });

        deleteButtons.add(deleteButton);
        compassClueSegments.add(clueSegmentView);

        scrollableClueSegments.addView(clueSegmentView);
        scrollableClueSegments.addView(deleteButton);
    }

    public void confirmRemoveDeleteButton(final View v) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to delete this clue segment?");
        builder.setPositiveButton("Yes, delete this segment!", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                removeDeleteButton(v);
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

    public void removeDeleteButton(View v) {
        for(int i=0;i<deleteButtons.size();i++) {
            Button button = deleteButtons.get(i);
            if(button.equals(v)) {
                scrollableClueSegments.removeView(button);
                scrollableClueSegments.removeView(compassClueSegments.get(i));

                deleteButtons.remove(i);
                compassClueSegments.remove(i);
            }
        }
    }

    public Clue getClue() throws NumberFormatException {
        List<ClueSegment> compassSegments = new ArrayList<>();
        for(ClueSegmentView view : compassClueSegments) {
            compassSegments.add(view.getClueSegment());
        }
        Clue clue;
        if(compassSegments.size()>0) {
            clue = new Clue(geofenceSegment.getClueSegment(), compassSegments);
        } else {
            clue = new Clue(geofenceSegment.getClueSegment().getClueText(), geofenceSegment.getClueSegment().getGeofenceData());
        }
        return clue;
    }

    @Override
    public void onBackPressed() {
        final Intent intent = new Intent();
        try {
            intent.putExtra("clue", getClue());

            if(clueNumber >=0 && requestCode>=0) {
                intent.putExtra("clueNumber", clueNumber);
            }

            setResult(RESULT_OK, intent);
            finish();
        } catch (NumberFormatException e) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Syntax error in clue segment.");
            builder.setPositiveButton("I'll fix it", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do nothing, they will try again
                }
            });
            builder.setNegativeButton("Discard clue", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
        }
    }
}
