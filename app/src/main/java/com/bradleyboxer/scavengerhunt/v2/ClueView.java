package com.bradleyboxer.scavengerhunt.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bradleyboxer.scavengerhunt.R;

import java.io.Serializable;

public class ClueView extends LinearLayout implements Serializable {

    TextView textView;
    TextView textView2;
    Button upButton;
    Button downButton;
    Button editButton;
    Button deleteButton;
    Clue clue;

    public ClueView(Context context, Clue clue) {
        super(context, null, 0);
        init();
        if(clue!=null) {
            setClue(clue);
        }
    }

    public ClueView(Context context) {
        super(context, null, 0);
        init();
    }

    public ClueView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        init();
    }

    public ClueView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        inflate(getContext(), R.layout.v2_view_clue_creator, this);

        textView = findViewById(R.id.v2_clueSummaryView);
        textView2 = findViewById(R.id.v2_clueSummaryView2);
        upButton = findViewById(R.id.v2_clueUpButton);
        downButton = findViewById(R.id.v2_clueDownButton);
        editButton = findViewById(R.id.v2_clueEditButton);
        deleteButton = findViewById(R.id.v2_clueRemoveButton);
    }

    public void updateText() {
        textView.setText(""+clue.getGeofenceGeofenceData().getName()+"\n"+
        "("+clue.getGeofenceGeofenceData().latitude+", "+clue.getGeofenceGeofenceData().longitude+")");
        textView2.setText("... and "+(clue.getNumberOfSegments()-1)+" chained compass clue(s)");
    }

    public void setClue(Clue clue) {
        this.clue = clue;
        updateText();
    }

    public Clue getClue() {
        return clue;
    }
}
