package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.bradleyboxer.scavengerhunt.R;

public class CompassClue extends Clue {

    private final GeoLocation location;

    public CompassClue(String name, String hintText, String solvedText, GeoLocation location) {
        super(name, hintText, solvedText, Type.COMPASS);
        this.location = location;
    }

    @Override
    public Drawable getDrawableIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_menu_compass, context.getTheme());
    }

    @Override
    public Class getActivityClass() {
        return CompassActivity.class;
    }

    public GeoLocation getLocation() {
        return location;
    }

}
