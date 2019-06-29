package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.bradleyboxer.scavengerhunt.R;

import java.util.UUID;

public class TextClue extends Clue {

    final String solution;

    public TextClue(String name, String hintText, String solvedText, String solution, UUID uuid) {
        super(name, hintText, solvedText, Type.TEXT, uuid);
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
        TextClue clue = new TextClue(getName(), getHintText(), getSolvedText(), solution, getUuid());
        for(UUID child : getChildren()) {
            clue.addChild(child);
        }
        return clue;
    }

}
