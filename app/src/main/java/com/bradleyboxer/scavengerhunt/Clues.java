package com.bradleyboxer.scavengerhunt;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bradley on 4/17/2018.
 */

public class Clues {
    public static List<Clue> clues = new ArrayList<>(); //this is probably a bad idea, but I don't know what else to do

    public static void addClue(Clue newClue) {
        boolean exists = false;
        for(Clue clue : clues) {
            if(clue.equals(newClue)) {
                exists = true;
            }
        }
        if(!exists) {
            clues.add(newClue);
        }
    }

    public static void updateClueListWith(Clue newClue) {
        for(Clue clue : clues) {
            if(clue.equals(newClue)) {
                clues.set(clues.indexOf(clue), newClue);
            }
        }
    }
}
