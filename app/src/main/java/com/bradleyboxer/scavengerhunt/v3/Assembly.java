package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class Assembly {

    public static ScavengerHunt assembleMorganScavengerHunt(Context context) {
        ScavengerHunt scavengerHunt = new ScavengerHunt(false);
        GeofenceManager geofenceManager = new GeofenceManager(context);

        GeoLocation location1 = new GeoLocation(40.592046f, -74.664458f, 20);
        Clue clue1 = new CompassClue("Beeautiful",
                "Come one, come all, it's time to begin\n\n" +
                "Discover the treasures that lie herein\n\n" +
                "We'll start really simple and do some walk'n\n\n" +
                "It's nearby enough, but my shoes, they'll break-in\n\n" +
                "Take a left down walter's - a park, it's akin",
                "You passed the first test\n\n" +
                         "wasn't that hard I guessed\n\n" +
                         "hope you aren't at rest \n\n" +
                         "(you) haven't won the contest (yet)", location1);
        clue1.activate();
        scavengerHunt.addClue(clue1);

        GeoLocation location2 = new GeoLocation(40.590845f, -74.667190f, 250);
        Clue clue2 = new GeofenceClue("One Step Forward, Two Steps Back",
                "We're out and about; we need to backtrack\n\n" +
                 "This was a great warmup but we need a (back)pack\n\n" +
                 "Don't think of this as too much a setback\n\n" +
                 "With all this walking you'll get a six-pack",
                "Grab all you're stuff! Time to get ready!\n\n" +
                         "Water, sunblock, backpack, go steady\n\n" +
                         "When you're ready to advance\n\n" +
                         "Give luck a chance\n\n" +
                         "And read this poem I've written already", location2, geofenceManager);
        scavengerHunt.addClue(clue2);

        Clue clue3 = new TextClue("Me @ U",
                "It's pretty much time to get out of here\n\n" +
                "We just have a gate from what I hear is near\n\n" +
                "To solve this next clue\n\n" +
                "You must look inside you\n\n" +
                "And see that I ____ you head over heel(s)",
                "Go you! You've completed the tutorial\n\n" +
                    "I hope my hints weren't to rhetorical\n\n" +
                    "Ready to start?\n\n" +
                    "I've done my part\n\n" +
                    "I'm sure the adventure will be historical", "love");
        scavengerHunt.addClue(clue3);

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
