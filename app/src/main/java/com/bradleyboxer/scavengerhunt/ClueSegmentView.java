package com.bradleyboxer.scavengerhunt;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

public class ClueSegmentView extends LinearLayout {

    EditText segmentNameField;
    EditText segmentLatitudeField;
    EditText segmentLongitudeField;
    EditText segmentRadiusField;
    EditText segmentClueTextField;

    public ClueSegmentView(Context context, ClueSegment clueSegment) {
        super(context, null, 0);
        setOrientation(VERTICAL);
        init();
        segmentNameField.setText(clueSegment.getGeofenceData().getName());
        segmentLatitudeField.setText(""+clueSegment.getGeofenceData().latitude);
        segmentLongitudeField.setText(""+clueSegment.getGeofenceData().longitude);
        segmentRadiusField.setText(""+clueSegment.getGeofenceData().radius);
        segmentClueTextField.setText(clueSegment.getClueText());
    }

    public ClueSegmentView(Context context) {
        super(context, null, 0);
        setOrientation(VERTICAL);
        init();
    }

    public ClueSegmentView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClueSegmentView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setOrientation(VERTICAL);
        init();
    }

    private void init() {
        inflate(getContext(), R.layout.view_clue_segment_creator, this);

        segmentNameField = (EditText) findViewById(R.id.clueNameView);
        segmentLatitudeField = (EditText) findViewById(R.id.clueLatitudeView);
        segmentLongitudeField = (EditText) findViewById(R.id.clueLongitudeView);
        segmentRadiusField = (EditText) findViewById(R.id.clueRadiusView);
        segmentClueTextField = (EditText) findViewById(R.id.clueTextView);
    }

}
