package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ScavengerHunt implements Serializable {

    private ArrayList<Clue> clueList;

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

    public Clue getEarliestUnsolved(Clue.Type type) {
        for(Clue clue : getClueList()) {
            if(clue.getType().equals(type) && clue.isActive() && !clue.isSolved()) {
                return clue;
            }
        }
        return null;
    }

    public Clue getEarliestInactive() {
        for(Clue clue : getClueList()) {
            if(!clue.isActive() && !clue.isSolved()) {
                return clue;
            }
        }
        return null;
    }

    public int[] getClueStates() {
        int inactive = 0;
        int active = 0;
        int solved = 0;
        for(Clue clue : getClueList()) {
            if(clue.isSolved()) {
                solved++;
            } else if(clue.isActive()) {
                active++;
            } else {
                inactive++;
            }
        }
        return new int[] {inactive, active, solved};
    }

    public void solveClue(String clueName) {
        List<Clue> clueList = getClueList();

        for(int i=0;i<clueList.size();i++) {
            Clue clue =  clueList.get(i);
            if(clue.getName().equals(clueName)) {
                clue.solved();

                //activate next clue
                Clue nextClue = getEarliestInactive();
                if(nextClue!=null) {
                    nextClue.activate();
                }

                return; //stop looking for clues to mark solved, since we already did one
            }
        }
    }

    public static String serialize(ScavengerHunt scavengerHunt) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(scavengerHunt);
            so.flush();
            return new String(Base64.encode(bo.toByteArray(), Base64.DEFAULT));
        } catch (Exception e) {
            Log.e("GEOFENCE UI", "serialization error", e);
        }
        return null;
    }

    public static ScavengerHunt deserialize(String serializedObject, Context context) throws Exception {
        byte b[] = Base64.decode(serializedObject.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bi = new ByteArrayInputStream(b);
        ObjectInputStream si = new ObjectInputStream(bi);
        ScavengerHunt scavengerHunt = (ScavengerHunt) si.readObject();

        GeofenceManager geofenceManager = new GeofenceManager(context);
        for(Clue clue : scavengerHunt.getClueList()) {
            if(clue.getType().equals(Clue.Type.GEOFENCE)) {
                GeofenceClue geofenceClue = (GeofenceClue) clue;
                geofenceClue.setGeofenceManager(geofenceManager);
            }
        }
        return scavengerHunt;
    }
}
