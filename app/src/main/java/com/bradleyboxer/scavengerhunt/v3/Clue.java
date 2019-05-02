package com.bradleyboxer.scavengerhunt.v3;

public abstract class Clue {

    abstract boolean isSolved();
    abstract boolean isActive();
    abstract void solved();
    abstract void activate();

    private String hintText;
    private String solvedText;

    public Clue(String hintText, String solvedText) {
        this.hintText = hintText;
        this.solvedText = solvedText;
    }

    public String getHintText() {
        return hintText;
    }

    public String getSolvedText() {
        return solvedText;
    }

}
