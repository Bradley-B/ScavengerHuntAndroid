package com.bradleyboxer.scavengerhunt.v3;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bradleyboxer.scavengerhunt.R;

public abstract class Clue {

    abstract Drawable getDrawableIcon(Context context);
    abstract Class getActivityClass();

    private String hintText;
    private String solvedText;
    private String name;
    private Type type;
    private State state;

    public enum Type {GEOFENCE, COMPASS, WORD} //TODO remove type attribute and move functionality into subclasses
    public enum State {INACTIVE, ACTIVE, SOLVED}

    public Clue(String name, String hintText, String solvedText, Type type) {
        this.hintText = hintText;
        this.solvedText = solvedText;
        this.type = type;
        this.name = name;
        state = State.INACTIVE;
    }

    public int getStatusColor() {
        if(isSolved()) {
            return R.color.clueStatusDone;
        } else if(isActive()) {
            return R.color.clueStatusActive;
        } else {
            return R.color.clueStatusInactive;
        }
    }

    public String getHintText() {
        return hintText;
    }

    public String getSolvedText() {
        return solvedText;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public boolean isSolved() {
        return state.equals(State.SOLVED);
    }

    public boolean isActive() {
        return state.equals(State.ACTIVE);
    }

    public void activate() {
        state = State.ACTIVE;
    }

    public void solved() {
        state = State.SOLVED;
    }
}
