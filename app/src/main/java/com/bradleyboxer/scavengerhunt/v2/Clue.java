package com.bradleyboxer.scavengerhunt.v2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Clue implements Serializable {

    private boolean discovered;
    private boolean simpleClue;

    private ClueSegment geofenceSegment; //"clue discovered"
    private List<ClueSegment> compassSegments = new ArrayList<>(); //"clue solved/found"

    public Clue(ClueSegment geofenceSegment, ClueSegment... compassSegments) {
        simpleClue = false;
        this.geofenceSegment = geofenceSegment;
        this.compassSegments.addAll(Arrays.asList(compassSegments));
    }

    public Clue(ClueSegment geofenceSegment, List<ClueSegment> compassSegments) {
        simpleClue = false;
        this.geofenceSegment = geofenceSegment;
        this.compassSegments.addAll(compassSegments);
    }

    /**
     * Use Clue(ClueSegment geofenceSegment, ClueSegment compassSegments)
     */
    @Deprecated
    public Clue(String geofenceText, GeofenceData geofenceClue, String compassText, GeofenceData compassClue) {
        this.geofenceSegment = new ClueSegment(geofenceText, geofenceClue);
        this.compassSegments.add(new ClueSegment(compassText, compassClue));
        simpleClue = false;
    }

    public Clue(String geofenceText, GeofenceData geofenceClue) {
        this.geofenceSegment = new ClueSegment(geofenceText, geofenceClue);
        //this.compassSegments.add(new ClueSegment(geofenceText, geofenceClue)); TODO is this needed?
        simpleClue = true;
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof Clue)) return false;
        Clue other = (Clue) obj;
        return other.geofenceSegment.equals(this.geofenceSegment) && other.compassSegments.equals(this.compassSegments);
    }

    /**
     * @return the compass clue segment farthest along in the list, that hasn't yet been solved.
     * Null if they all have been solved.
     */
    public ClueSegment getActiveCompassClueSegment() {
        for(int i=0;i<compassSegments.size();i++) {
            ClueSegment segment = compassSegments.get(i);
            if(!segment.isSolved()) {
                return segment;
            }
        }
        return null;
    }

    public boolean containsUnsolvedSegments() {
        return getActiveCompassClueSegment()!=null;
    }

    /**
     * @return the compass clue segment farthest along in the list, that has been solved.
     * Null if none of them are solved.
     */
    public ClueSegment getLastSolvedCompassClueSegment() {
        for(int i=compassSegments.size()-1;i>=0;i--) {
            ClueSegment segment = compassSegments.get(i);
            if(segment.isSolved()) {
                return segment;
            }
        }
        return null;
    }

    public GeofenceData getGeofenceGeofenceData() {
        return geofenceSegment.getGeofenceData();
    }

    public String getGeofenceDiscoveredText() {
        return geofenceSegment.getClueText();
    }

    public ClueSegment getGeofenceSegment() {
        return geofenceSegment;
    }

    public void setDiscovered() {
        discovered = true;
    }

    public boolean hasBeenDiscovered() {
        return discovered;
    }

    /**
     * @return if any compass ClueSegments have been solved
     */
    public boolean hasBeenSolved() {
        for(ClueSegment clueSegment : compassSegments) {
            if(clueSegment.isSolved()) {
                return true;
            }
        }
        return false;
    }

    public boolean isSimpleClue() {
        return simpleClue;
    }

    public int getNumberOfSegments() {
        return compassSegments.size()+1;
    }

    public List<ClueSegment> getCompassSegments() {return compassSegments;}
}
