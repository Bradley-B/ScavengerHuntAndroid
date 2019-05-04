package com.bradleyboxer.scavengerhunt.v3;

public abstract class Clue {

    abstract boolean isSolved();
    abstract boolean isActive();
    abstract void solved();
    abstract void activate();

    private String hintText;
    private String solvedText;
    private String name;
    private Type type;

    boolean active = false;
    boolean solved = false;

    public enum Type {GEOFENCE, COMPASS, WORD}

    public Clue(String name, String hintText, String solvedText, Type type) {
        this.hintText = hintText;
        this.solvedText = solvedText;
        this.type = type;
        this.name = name;
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
