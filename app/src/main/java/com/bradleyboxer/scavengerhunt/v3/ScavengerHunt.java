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
import java.util.UUID;

public class ScavengerHunt implements Serializable {

    private List<Clue> clueList;
    private boolean inactiveCluesDisplayed;
    private final UUID uuid;
    private String name;

    public ScavengerHunt(ScavengerHunt scavengerHunt) {
        this.uuid = scavengerHunt.getUuid();
        this.clueList = new ArrayList<>(scavengerHunt.getClueList());
        this.inactiveCluesDisplayed = scavengerHunt.inactiveCluesDisplayed;
        this.name = scavengerHunt.getName();
    }

    public ScavengerHunt(boolean displayInactiveClues, String name) {
        clueList = new ArrayList<>();
        this.inactiveCluesDisplayed = displayInactiveClues;
        this.uuid = UUID.randomUUID();
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public UUID getUuid() {
        return uuid;
    }

    public boolean areInactiveCluesDisplayed() {
        return inactiveCluesDisplayed;
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

    public Clue getClue(String clueName) {
        for(Clue c : getClueList()) {
            if(clueName.equals(c.getName())) {
                return c;
            }
        }
        return null;
    }

    public void solveClue(String clueName) {
        Clue clue = getClue(clueName);
        clue.solved();

        //activate next clue
        List<String> clueChildrenNames = clue.getChildren();
        for(String name : clueChildrenNames) {
            Clue child = getClue(name);
            child.activate();
        }
    }

    public static String serialize(ScavengerHunt scavengerHunt) throws Exception {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ObjectOutputStream so = new ObjectOutputStream(bo);
        so.writeObject(scavengerHunt);
        so.flush();
        return new String(Base64.encode(bo.toByteArray(), Base64.DEFAULT));
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
