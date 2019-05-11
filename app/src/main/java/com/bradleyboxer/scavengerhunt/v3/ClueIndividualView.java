package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.graphics.ColorFilter;
import android.media.Image;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bradleyboxer.scavengerhunt.R;

public class ClueIndividualView extends LinearLayout {

    private ImageView clueIcon;
    private Button clueHintButton;
    private Button clueSolveButton;
    private TextView clueName;

    @Deprecated
    public ClueIndividualView(Context context, AttributeSet attrs) {
        super(context, null, 0);
    }

    public ClueIndividualView(Context context, Clue clue) {
        super(context, null, 0);

        inflate(getContext(), R.layout.content_clue_individual_view, this);

        clueIcon = (ImageView) findViewById(R.id.clue_view_icon);
        clueHintButton = (Button) findViewById(R.id.clue_hint_button);
        clueSolveButton = (Button) findViewById(R.id.clue_solve_button);
        clueName = (TextView) findViewById(R.id.clue_name);

        clueIcon.setImageDrawable(clue.getDrawableIcon(context));
        clueIcon.setColorFilter(clue.getStatusColor());
        clueName.setText(clue.getName());

    }
}
