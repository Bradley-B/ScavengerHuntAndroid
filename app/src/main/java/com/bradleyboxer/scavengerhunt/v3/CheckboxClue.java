package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;

import com.bradleyboxer.scavengerhunt.R;

import java.util.List;
import java.util.UUID;

public class CheckboxClue extends Clue {

    public CheckboxClue(String name, String hintText, String solvedText, UUID uuid) {
        super(name, hintText, solvedText, Type.CHECKBOX, uuid);
    }

    @Override
    Drawable getDrawableIcon(Context context) {
        return context.getResources().getDrawable(R.drawable.ic_menu_checkbox, context.getTheme());
    }

    @Override
    public void launchSolveActivity(Context context) {
        ScavengerHuntDatabase scavengerHuntDatabase = FileUtil.loadScavengerHuntDatabase(context);
        ScavengerHunt activeScavengerHunt = scavengerHuntDatabase.getActiveScavengerHunt(context);
        List<UUID> children = activeScavengerHunt.solveClue(getUuid());
        FileUtil.saveScavengerHuntDatabase(scavengerHuntDatabase, context);

        Intent intent = new Intent(context, ClueViewActivity.class);
        if(!children.isEmpty() && children.get(0) != null) {
            intent.putExtra("clueName", scavengerHuntDatabase.getClue(children.get(0)).getName());
        }
        context.startActivity(intent);
    }

    @Override
    Clue deepCopy() {
        Clue clue = new CheckboxClue(getName(), getHintText(), getSolvedText(), getUuid());
        for(UUID child : getChildren()) {
            clue.addChild(child);
        }
        return clue;
    }
}
