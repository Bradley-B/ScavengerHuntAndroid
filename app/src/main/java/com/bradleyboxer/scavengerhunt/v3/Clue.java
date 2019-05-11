package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bradleyboxer.scavengerhunt.R;

public abstract class Clue {

    abstract boolean isSolved();
    abstract boolean isActive();
    abstract void solved();
    abstract void activate();
    abstract Drawable getDrawableIcon(Context context);

    private String hintText;
    private String solvedText;
    private String name;
    private Type type;
    //private State state;

    boolean active = false;
    boolean solved = false;

    public enum Type {GEOFENCE, COMPASS, WORD}
    //public enum State {INACTIVE, ACTIVE, SOLVED}

    public Clue(String name, String hintText, String solvedText, Type type) {
        this.hintText = hintText;
        this.solvedText = solvedText;
        this.type = type;
        this.name = name;
    }

    public int getStatusColor() {
        if(!active && !solved) {
            return R.color.clueStatusInactive;
        } else if(active && !solved) {
            return R.color.clueStatusActive;
        } else if(solved) {
            return R.color.clueStatusDone;
        }
        return R.color.clueStatusInactive;
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
}
