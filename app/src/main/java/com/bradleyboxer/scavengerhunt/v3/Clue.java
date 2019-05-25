package com.bradleyboxer.scavengerhunt.v3;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bradleyboxer.scavengerhunt.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class Clue implements Serializable {

    abstract Drawable getDrawableIcon(Context context);

    private String hintText;
    private String solvedText;
    private String name;
    private Type type;
    private State state;

    private final List<String> activationList;

    public enum Type {GEOFENCE, COMPASS, TEXT} //TODO remove type attribute and move functionality into subclasses
    public enum State {INACTIVE, ACTIVE, SOLVED}

    public Clue(String name, String hintText, String solvedText, Type type) {
        this.hintText = hintText;
        this.solvedText = solvedText;
        this.type = type;
        this.name = name;
        this.activationList = new ArrayList<>();
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

    public void addChild(String childName) {
        activationList.add(childName);
    }

    public List<String> getChildren() {
        return activationList;
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

    public void launchSolveActivity(Context context) {}

    @Override
    public boolean equals(Object o) {
        if(o == this) return true;
        if(!(o instanceof Clue)) return false;

        Clue other = (Clue) o;
        return getType().equals(other.getType()) && getName().equals(other.getName()) &&
                getHintText().equals(other.getHintText()) && getSolvedText().equals(other.getSolvedText());
    }

}
