package com.bradleyboxer.scavengerhunt.v3;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;

import com.bradleyboxer.scavengerhunt.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Clue implements Serializable {

    abstract Drawable getDrawableIcon(Context context);
    abstract Clue deepCopy();

    private String hintText;
    private String solvedText;
    private String name;
    private Type type;
    private State state;

    private final List<UUID> activationList;
    private final UUID uuid;

    public enum Type {GEOFENCE, COMPASS, TEXT, CHECKBOX} //TODO remove type attribute and move functionality into subclasses
    public enum State {INACTIVE, ACTIVE, SOLVED}

    public Clue(String name, String hintText, String solvedText, Type type, UUID uuid) {
        this.hintText = hintText;
        this.solvedText = solvedText;
        this.type = type;
        this.name = name;
        this.activationList = new ArrayList<>();
        this.uuid = uuid;
        resetState();
    }

    public UUID getUuid() {
        return uuid;
    }

    public State getState() {
        return state;
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

    public void addChild(Clue child) {
        activationList.add(child.getUuid());
    }

    public void addChild(UUID childId) {
        activationList.add(childId);
    }

    public List<UUID> getChildren() {
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
        return other.getUuid().equals(getUuid());

//        return getType().equals(other.getType()) && getName().equals(other.getName()) &&
//                getHintText().equals(other.getHintText()) && getSolvedText().equals(other.getSolvedText());
    }

    public void resetState() {
        setState(State.INACTIVE);
    }

    public void setState(State state) {
        this.state = state;
    }

}
