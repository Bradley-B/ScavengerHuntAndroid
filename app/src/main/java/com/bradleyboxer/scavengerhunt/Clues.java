package com.bradleyboxer.scavengerhunt;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.NotSerializableException;
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

    public static File getSavedScavengerHuntLocation(File filesDirectory) {
        return new File(filesDirectory, "savedScavengerHunt");
    }

    private static ArrayList<Clue> loadClueList(File filesDirectory) {
        try {
            File clueListLocation = getSavedScavengerHuntLocation(filesDirectory);
            if (clueListLocation.exists()) {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(clueListLocation));
                ArrayList<Clue> clueList = (ArrayList<Clue>) inputStream.readObject();
                Log.i("GEOFENCE UI", "loading clue list with "+clueList.size()+" clues");
                return clueList;
            }
        } catch (IOException e) {
            Log.e("GEOFENCE UI", "Exception in getting saved scavenger hunt", e);
            e.printStackTrace();
        } catch (ClassNotFoundException ex) {
            Log.e("GEOFENCE UI", "Exception in getting saved scavenger hunt", ex);
            ex.printStackTrace();
        }
        return null;
    }

    public static ArrayList<Clue> getSafeClueList(File filesDirectory) {
        ArrayList<Clue> clueList = loadClueList(filesDirectory);
        if(clueList!=null) {
            clues.removeAll(clues);
            clues.addAll(clueList);
        }
        Log.i("GEOFENCE UI", "getting safe clue list with "+clues.size()+" clues");

        return clues;
    }

    public static void updateClueListWith(Clue newClue) {
        for(Clue clue : clues) {
            if(clue.equals(newClue)) {
                clues.set(clues.indexOf(clue), newClue);
            }
        }
    }
}
