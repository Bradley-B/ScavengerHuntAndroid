package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.bradleyboxer.scavengerhunt.R;

public class TextClue extends Clue {

    final String solution;

    public TextClue(String name, String hintText, String solvedText, String solution) {
        super(name, hintText, solvedText, Type.TEXT);
        this.solution = solution;
    }

    public boolean shouldBeSolved(String proposedSolution) {
        return proposedSolution.toLowerCase().trim().equals(solution.toLowerCase().trim());
    }

    @Override
    public Drawable getDrawableIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_menu_pen, context.getTheme());
    }

    @Override
    public void launchSolveActivity(Context context) {
        Intent intent = new Intent(context, TextInputActivity.class);
        context.startActivity(intent);
    }

    @Override
    public Clue deepCopy() {
        return new TextClue(getName(), getHintText(), getSolvedText(), solution);
    }

}
