package com.bradleyboxer.scavengerhunt.v3;

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

    public static ScavengerHunt deserialize(String serializedObject) throws Exception {
        byte b[] = Base64.decode(serializedObject.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bi = new ByteArrayInputStream(b);
        ObjectInputStream si = new ObjectInputStream(bi);
        ScavengerHunt scavengerHunt = (ScavengerHunt) si.readObject();
        return scavengerHunt;
    }
}
