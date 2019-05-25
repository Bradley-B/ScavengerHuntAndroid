package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class Assembly {

    public static ScavengerHunt assembleMorganScavengerHunt(Context context) {
        ScavengerHunt scavengerHunt = new ScavengerHunt(false);
        GeofenceManager geofenceManager = new GeofenceManager(context);

        final String CLUE_1_NAME = "Beeautiful";
        final String CLUE_2_NAME = "One Step Forward, Two Steps Back";
        final String CLUE_3_NAME = "Me @ U";

        GeoLocation location1 = new GeoLocation(40.592046f, -74.664458f, 20);
        Clue clue1 = new CompassClue(CLUE_1_NAME,
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
        clue1.addChild(CLUE_2_NAME);
        scavengerHunt.addClue(clue1);

        GeoLocation location2 = new GeoLocation(40.590845f, -74.667190f, 250);
        Clue clue2 = new GeofenceClue(CLUE_2_NAME,
                "We're out and about; we need to backtrack\n\n" +
                 "This was a great warmup but we need a (back)pack\n\n" +
                 "Don't think of this as too much a setback\n\n" +
                 "With all this walking you'll get a six-pack",
                "Grab all you're stuff! Time to get ready!\n\n" +
                         "Water, sunblock, backpack, go steady\n\n" +
                         "When you're ready to advance\n\n" +
                         "Give luck a chance\n\n" +
                         "And read this poem I've written already", location2, geofenceManager);
        clue2.addChild(CLUE_3_NAME);
        scavengerHunt.addClue(clue2);

        Clue clue3 = new TextClue(CLUE_3_NAME,
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

        final String CLUE_2_NAME = "Home Test";
        final String CLUE_4_NAME = "Home Test 2";
        final String CLUE_5_NAME = "Text Input Test";

        GeofenceManager geofenceManager = new GeofenceManager(context);

        //home test
        GeoLocation location2 = new GeoLocation(40.590845f, -74.667190f, 1000);
        Clue clue2 = new GeofenceClue(CLUE_2_NAME, "hintText", "solvedText", location2, geofenceManager);
        clue2.activate();
        clue2.addChild(CLUE_4_NAME);
        scavengerHunt.addClue(clue2);

        //compass test
        GeoLocation location4 = new GeoLocation(40.590845f, -74.667190f, 20);
        Clue clue4 = new CompassClue(CLUE_4_NAME, "hintText", "solvedText", location4);
        clue4.addChild(CLUE_5_NAME);
        scavengerHunt.addClue(clue4);

        //text input test
        Clue clue5 = new TextClue(CLUE_5_NAME, "hintText", "solvedText", "home");
        scavengerHunt.addClue(clue5);

        return scavengerHunt;
    }
}
