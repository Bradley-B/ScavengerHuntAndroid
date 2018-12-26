package com.bradleyboxer.scavengerhunt.v2;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.bradleyboxer.scavengerhunt.R;

public class ClueSegmentView extends LinearLayout {

    private EditText segmentNameField;
    private EditText segmentLatitudeField;
    private EditText segmentLongitudeField;
    private EditText segmentRadiusField;
    private EditText segmentClueTextField;

    public ClueSegmentView(Context context, ClueSegment clueSegment) {
        super(context, null, 0);
        setOrientation(VERTICAL);
        init();
        setClueSegmentData(clueSegment);
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
        inflate(getContext(), R.layout.v2_view_clue_segment_creator, this);

        segmentNameField = (EditText) findViewById(R.id.v2_clueNameView);
        segmentLatitudeField = (EditText) findViewById(R.id.v2_clueLatitudeView);
        segmentLongitudeField = (EditText) findViewById(R.id.v2_clueLongitudeView);
        segmentRadiusField = (EditText) findViewById(R.id.v2_clueRadiusView);
        segmentClueTextField = (EditText) findViewById(R.id.v2_clueTextView);

        segmentNameField.setMaxWidth(segmentNameField.getWidth());
        segmentLatitudeField.setMaxWidth(segmentLatitudeField.getWidth());
        segmentLongitudeField.setMaxWidth(segmentLongitudeField.getWidth());
        segmentRadiusField.setMaxWidth(segmentRadiusField.getWidth());

    }

    public void setClueSegmentData(ClueSegment clueSegment) {
        segmentNameField.setText(clueSegment.getGeofenceData().getName());
        segmentLatitudeField.setText(""+clueSegment.getGeofenceData().latitude);
        segmentLongitudeField.setText(""+clueSegment.getGeofenceData().longitude);
        segmentRadiusField.setText(""+clueSegment.getGeofenceData().radius);
        segmentClueTextField.setText(clueSegment.getClueText());
    }

    public boolean verifyTextFields() {
        try {
            Double.parseDouble(segmentLatitudeField.getText().toString());
            Double.parseDouble(segmentLongitudeField.getText().toString());
            Float.parseFloat(segmentRadiusField.getText().toString());
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ClueSegment getClueSegment() throws NumberFormatException {
        String name = segmentNameField.getText().toString();
        double latitude = Double.parseDouble(segmentLatitudeField.getText().toString());
        double longitude = Double.parseDouble(segmentLongitudeField.getText().toString());
        float radius = Float.parseFloat(segmentRadiusField.getText().toString());
        String clueText = segmentClueTextField.getText().toString();
        return new ClueSegment(clueText, new GeofenceData(latitude, longitude, radius, name));
    }

}
