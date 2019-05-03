package com.bradleyboxer.scavengerhunt.v3;

import java.util.ArrayList;
import java.util.List;

public class ScavengerHunt {

    private List<Clue> clueList;

    public ScavengerHunt() {
        clueList = new ArrayList<>();
    }

    public void addClue(Clue clue) {
        clueList.add(clue);
    }

    public List<Clue> getClueList() {
        return clueList;
    }

    public float getProgressPercent() {
        int numSolved = 0;
        for(Clue clue : clueList) {
            if(clue.isSolved()) {
                numSolved++;
            }
        }
        if(clueList.size()==0) {
            return 1;
        }
        return numSolved/(float)clueList.size();
    }
}
