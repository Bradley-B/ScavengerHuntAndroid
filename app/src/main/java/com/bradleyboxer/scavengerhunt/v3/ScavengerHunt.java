package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.typeadapters.RuntimeTypeAdapterFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ScavengerHunt implements Serializable {

    private List<Clue> clueList;
    private boolean inactiveCluesDisplayed;
    private final UUID uuid;
    private String name;

    private static final Gson gson;
    static {
        RuntimeTypeAdapterFactory<Clue> clueAdapterFactory = RuntimeTypeAdapterFactory.of(Clue.class, "clueType")
                .registerSubtype(GeofenceClue.class, "Geofence")
                .registerSubtype(CompassClue.class, "Compass")
                .registerSubtype(TextClue.class, "Text");
        gson = new GsonBuilder().registerTypeAdapterFactory(clueAdapterFactory).create();
    }

    public ScavengerHunt(ScavengerHunt scavengerHunt) {
        this.uuid = scavengerHunt.getUuid();
        this.clueList = new ArrayList<>();
        for(Clue c : scavengerHunt.getClueList()) {
            addClue(c.deepCopy());
        }
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

    public Clue getClue(UUID clueUuid) {
        for(Clue c : getClueList()) {
            if(clueUuid.equals(c.getUuid())) {
                return c;
            }
        }
        return null;
    }

    public void solveClue(UUID clueUuid) {
        Clue clue = getClue(clueUuid);
        clue.solved();

        //activate next clue(s)
        List<UUID> childrenIds = clue.getChildren();
        for(UUID id : childrenIds) {
            Clue child = getClue(id);
            child.activate();
        }
    }

    public static String serialize(ScavengerHunt scavengerHunt) throws Exception {
        return gson.toJson(scavengerHunt);
    }

    public static ScavengerHunt deserialize(String serializedObject, Context context) throws Exception {
        ScavengerHunt scavengerHunt = gson.fromJson(serializedObject, ScavengerHunt.class);

        GeofenceManager geofenceManager = new GeofenceManager(context);
        for(Clue clue : scavengerHunt.getClueList()) {
            if(clue.getType().equals(Clue.Type.GEOFENCE)) {
                GeofenceClue geofenceClue = (GeofenceClue) clue;
                geofenceClue.setGeofenceManager(geofenceManager);
            }
        }
        return scavengerHunt;
    }

    @Override
    @NonNull
    public String toString() {
        return getName();
    }
}
