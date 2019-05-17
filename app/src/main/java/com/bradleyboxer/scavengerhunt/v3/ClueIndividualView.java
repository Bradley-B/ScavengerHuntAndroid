package com.bradleyboxer.scavengerhunt.v3;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.ColorFilter;
import android.media.Image;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bradleyboxer.scavengerhunt.R;

public class ClueIndividualView extends LinearLayout {

    private Clue clue;
    private ImageView clueIcon;
    private Button clueHintButton;
    private Button clueSolveButton;
    private TextView clueName;

    private int iconTouches = 0;

    @Deprecated
    public ClueIndividualView(Context context, AttributeSet attrs) {
        super(context, null, 0);
    }

    public ClueIndividualView(Context context, final Clue clue) {
        super(context, null, 0);
        this.clue = clue;
        inflate(getContext(), R.layout.content_clue_individual_view, this);

        clueIcon = (ImageView) findViewById(R.id.clue_view_icon);
        clueHintButton = (Button) findViewById(R.id.clue_hint_button);
        clueSolveButton = (Button) findViewById(R.id.clue_solve_button);
        clueName = (TextView) findViewById(R.id.clue_name);

        clueIcon.setImageDrawable(clue.getDrawableIcon(context));
        clueIcon.setColorFilter(ContextCompat.getColor(context, clue.getStatusColor()));
        clueName.setText(clue.getName());
        clueIcon.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iconTouches>50) {
                    ScavengerHunt scavengerHunt = FileUtil.loadScavengerHunt(getContext());
                    scavengerHunt.solveClue(clue.getName());
                    FileUtil.saveScavengerHunt(scavengerHunt, getContext());
                    Notifications.sendNotification(clue.getName(), getContext());
                }
                iconTouches++;
            }
        });

        clueHintButton.setEnabled(clue.isActive() || clue.isSolved());
        clueHintButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                viewHint();
            }
        });

        clueSolveButton.setText(clue.isSolved() ? "View Solution Message" : "Solve");
        clueSolveButton.setEnabled(clue.isSolved() || (clue.isActive() && !clue.getType().equals(Clue.Type.GEOFENCE)));
        clueSolveButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clue.isSolved()) {
                    viewSolution();
                } else {
                    clue.launchSolveActivity(getContext());
                }
            }
        });
    }

    public void viewHint() {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
        dlgAlert.setMessage(clue.getHintText());
        dlgAlert.setTitle("Hint Message");
        dlgAlert.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {}
        });
        dlgAlert.create().show();
    }

    public void viewSolution() {
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(getContext());
        dlgAlert.setMessage(clue.getSolvedText());
        dlgAlert.setTitle("Solution Message");
        dlgAlert.setPositiveButton("Got it", new DialogInterface.OnClickListener() {
            @Override public void onClick(DialogInterface dialogInterface, int i) {}
        });
        dlgAlert.create().show();
    }

}
