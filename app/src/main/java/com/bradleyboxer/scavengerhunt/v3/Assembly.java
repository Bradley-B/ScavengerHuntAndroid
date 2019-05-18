package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class Assembly {

    public static ScavengerHunt assembleMorganScavengerHunt(Context context) {
        ScavengerHunt scavengerHunt = new ScavengerHunt(false);
        GeofenceManager geofenceManager = new GeofenceManager(context);
        return scavengerHunt;
    }

    public static ScavengerHunt assembleTestScavengerHunt(Context context) {
        ScavengerHunt scavengerHunt = new ScavengerHunt(false);

        GeofenceManager geofenceManager = new GeofenceManager(context);

        //home test
        GeoLocation location2 = new GeoLocation(40.590845f, -74.667190f, 1000);
        Clue clue2 = new GeofenceClue("Home Test", "hintText", "solvedText", location2, geofenceManager);
        clue2.activate();

//        //home depot test
//        GeoLocation location3 = new GeoLocation(40.575701f, -74.670116f, 1000);
//        Clue clue3 = new GeofenceClue("Home Depot", "hintText", "solvedText", location3, geofenceManager);
//        clue3.activate();

        //compass test
        GeoLocation location4 = new GeoLocation(40.590845f, -74.667190f, 20);
        Clue clue4 = new CompassClue("Home Test 2", "hintText", "solvedText", location4);
        //clue4.activate();

        //text input test
        Clue clue5 = new TextClue("Text Input Test", "hintText", "solvedText", "home");

        scavengerHunt.addClue(clue2);
//        scavengerHunt.addClue(clue3);
        scavengerHunt.addClue(clue4);
        scavengerHunt.addClue(clue5);

        return scavengerHunt;
    }
}
