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

    public void solveClue(String clueName) {
        for(Clue clue : getClueList()) {
            if(clue.getName().equals(clueName)) {
                clue.solved();
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
