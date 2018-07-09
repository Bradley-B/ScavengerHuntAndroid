package com.bradleyboxer.scavengerhunt;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Bradley on 4/17/2018.
 */

public class Clues {
    public static ArrayList<Clue> clues = new ArrayList<>(); //this is probably a bad idea, but I don't know what else to do

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

    public static ArrayList<Clue> loadClueList(File filesDirectory) {
        try {
            File savedHunt = new File(filesDirectory, "savedScavengerHunt");
            boolean savedHuntExists = !savedHunt.createNewFile();
            if (savedHuntExists) {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(savedHunt));
                return (ArrayList<Clue>) inputStream.readObject();
            }
        } catch (Exception e) {
            Log.e("GEOFENCE UI", "Exception in getting saved scavenger hunt", e);
            e.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Clue> getSafeClueList(File filesDirectory) {
        if(clues!=null && clues.size()>0) {
            return clues;
        } else {
            ArrayList<Clue> clueList = loadClueList(filesDirectory);
            if(clueList!=null && clues.size()>0) {
                clues = clueList;
            }
            return clues;
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
