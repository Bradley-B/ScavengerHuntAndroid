package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;

import com.bradleyboxer.scavengerhunt.R;

import java.util.UUID;

public class CompassClue extends Clue {

    private final GeoLocation location;

    public CompassClue(String name, String hintText, String solvedText, GeoLocation location, UUID uuid) {
        super(name, hintText, solvedText, Type.COMPASS, uuid);
        this.location = location;
    }

    public boolean shouldBeSolved(float distanceToTarget) {
        return distanceToTarget<=location.getRadius();
    }

    @Override
    public Drawable getDrawableIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_menu_compass, context.getTheme());
    }

    @Override
    public void launchSolveActivity(Context context) {
        Intent intent = new Intent(context, CompassActivity.class);
        intent.putExtra("clue", this);
        context.startActivity(intent);
    }

    public GeoLocation getLocation() {
        return location;
    }

    @Override
    public Clue deepCopy() {
        return new CompassClue(getName(), getHintText(), getSolvedText(), location.deepCopy(), getUuid());
    }

}
