package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.graphics.drawable.Drawable;

public class Assembly {

    public static ScavengerHunt assembleMorganScavengerHunt(Context context) {
        ScavengerHunt scavengerHunt = new ScavengerHunt(false);
        GeofenceManager geofenceManager = new GeofenceManager(context);

        //intro sequence
        final String CLUE_1_NAME = "Beeautiful";
        final String CLUE_2_NAME = "One Step Forward, Two Steps Back";
        final String CLUE_3_NAME = "Me @ U";

        //white oak
        final String CLUE_4_NAME = "Crypto? Like Bitcoin?";
        final String CLUE_5_NAME = "White Oak";
        final String CLUE_6_NAME = "What A Fucking Shame";
        final String CLUE_7_NAME = "Walkie Talkie";
        final String CLUE_8_NAME = "Bad News";

        //near fork - colonial park sequence
        final String CLUE_A_TITLE = "Oh Fork - Near";
        final String CLUE_A1_NAME = "A Familiar Place";
        final String CLUE_A2_NAME = "Come Back On The Weekend";
        final String CLUE_A3_NAME = "Come Back On The Weekend 2";
        final String CLUE_A4_NAME = "It's A Racket";
        final String CLUE_A5_NAME = "A Kind Of Smelly Smell";
        final String CLUE_A6_NAME = "I'm Hungry";

        //far fork - norvin green state forest
        final String CLUE_B_TITE = "Oh Fork - Far";
        final String CLUE_B1_NAME = "A Serious Hike";
        final String CLUE_B2_NAME = "Hi Point";


        //intro sequence
        GeoLocation beeautifulLocation = new GeoLocation(40.592046f, -74.664458f, 20);
        Clue beeautifulClue = new CompassClue(CLUE_1_NAME,
                "Come one, come all, it's time to begin\n\n" +
                "Discover the treasures that lie herein\n\n" +
                "We'll start really simple and do some walk'n\n\n" +
                "It's nearby enough, but my shoes, they'll break-in\n\n" +
                "Take a left down walter's - a park, it's akin",
                "You passed the first test\n\n" +
                         "wasn't that hard I guessed\n\n" +
                         "hope you aren't at rest \n\n" +
                         "(you) haven't won the contest (yet)", beeautifulLocation);
        beeautifulClue.activate();
        beeautifulClue.addChild(CLUE_2_NAME);
        scavengerHunt.addClue(beeautifulClue);

        GeoLocation oneStepLocation = new GeoLocation(40.590845f, -74.667190f, 250);
        Clue oneStepClue = new GeofenceClue(CLUE_2_NAME,
                "We're out and about; we need to backtrack\n\n" +
                 "This was a great warmup but we need a (back)pack\n\n" +
                 "Don't think of this as too much a setback\n\n" +
                 "With all this walking you'll get a six-pack",
                "Grab all you're stuff! Time to get ready!\n\n" +
                         "Water, sunblock, backpack, go steady\n\n" +
                         "When you're ready to advance\n\n" +
                         "Give luck a chance\n\n" +
                         "And read this poem I've written already", oneStepLocation, geofenceManager);
        oneStepClue.addChild(CLUE_3_NAME);
        scavengerHunt.addClue(oneStepClue);

        Clue loveClue = new TextClue(CLUE_3_NAME,
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
        loveClue.addChild(CLUE_4_NAME);
        scavengerHunt.addClue(loveClue);

        //white oak clues
        Clue caesarCipherClue = new TextClue(CLUE_4_NAME,
                "Where to first?\n" +
                        "You'll Need some mild cryptography\n" +
                        "ThIs enCryption was usEd\n" +
                        "NoT in a democRacY\n" +
                        "\n" +
                        "It didn't help much\n" +
                        "But their society was lush\n" +
                        "And they made some good policy\n" +
                        "\n" +
                        "Their namesake leader did say\n" +
                        "Et tu brute?\n" +
                        "As he was stabbed by the aristocracy\n" +
                        "\n" +
                        "author's notes\n" +
                        "- To solve, decrypt: zklwh rdn sdun\n" +
                        "- The fifth letter is e.",
                "A caesar cipher, how did you guess?\n" +
                        "\n" +
                        "I knew I could count on you to rise above the rest\n" +
                        "\n" +
                        "Let's go, and quick!\n" +
                        "\n" +
                        "Before the weather gets hot and thick", "white oak park");
        caesarCipherClue.addChild(CLUE_5_NAME);
        scavengerHunt.addClue(caesarCipherClue);

        GeoLocation whiteOakGeofenceLocation = new GeoLocation(40.570997f, -74.712000f, 1000);
        Clue whiteOakArrivalClue = new GeofenceClue(CLUE_5_NAME,
                "Hop to it!",
                "You made it!\n\n" +
                    "Continue on to discover all that's in store\n\n" +
                    "Don't get down, there's so much more!\n\n", whiteOakGeofenceLocation, geofenceManager);
        whiteOakArrivalClue.addChild(CLUE_6_NAME);
        scavengerHunt.addClue(whiteOakArrivalClue);

        GeoLocation whiteOakPlaygroundLocation = new GeoLocation(40.572673f, -74.714259f, 20);
        Clue whiteOakPlaygroundClue = new CompassClue(CLUE_6_NAME, "Have I explained my sentimental attachment to this park? " +
                "When Wendy and I were young'uns, we never really came here. " +
                "It's farther than North Branch or Duke Island and so we mostly went there. " +
                "Maybe a few times a year my mom would travel a bit farther and we would come here. " +
                "As such, I never really learned the name of the park. " +
                "It was a magical place that we ended up sometimes. " +
                "And this magical place, boy, it was great. The tallest slides. " +
                "The fast spinning thing that doesn't exist anymore because it's a \"liability\". " +
                "Endless fun. Danger. Adventure. \n\n" +
                "And then at some point they brutally ripped out all that (and my heart) and replaced it with this. " +
                "What a fucking shame.\n\n" +
                "Thank you for coming to my TED talk.", "Sorry, I'm still salty about it.", whiteOakPlaygroundLocation);
        whiteOakPlaygroundClue.addChild(CLUE_7_NAME);
        scavengerHunt.addClue(whiteOakPlaygroundClue);

        GeoLocation whiteOakWoodsWalkLocation = new GeoLocation(40.570836f, -74.709674f, 15);
        Clue whiteOakWoodsWalkClue = new CompassClue(CLUE_7_NAME,
                "Hey, wanna get lost in the woods?\n\n" +
                        "It's not as bad as getting caught in the hood\n\n" +
                        "We can't get that lost, we're right next to a neighborhood\n\n" +
                        "We'll have our phones, which are durable goods",
                "See? We're fine. I know the way out\n\n" +
                        "There's a tennis court to the left, no doubt\n\n" +
                        "I wouldn't get you lost - when I plan these, I go all out\n\n" +
                        "But while we're back here, wanna make out?\n\n" +
                        "Just kidding, you know I'm too devout\n\n" +
                        "We wouldn't risk being caught by a cub scout\n\n" +
                        "We'd have to flee, and that would not be far-out.", whiteOakWoodsWalkLocation);
        whiteOakWoodsWalkClue.addChild(CLUE_8_NAME);
        scavengerHunt.addClue(whiteOakWoodsWalkClue);

        //fork clues
        Clue badNewsClue = new TextClue(CLUE_8_NAME, "I've got bad news.\n\n" +
                "All I've planned for today, we can't complete\n\n" +
                "Especially if it's going to thunder at three\n\n" +
                "You'll need to choose a path for you and me\n\n" +
                "We can resume another day, I'm sure you'll agree\n\n\n" +
                "Input \"ok\" when you're back at the car.",
                "A fork in the road? It appears here we are\n\n" +
                        "Well? We're not doing much good sitting in the car...\n\n" +
                        "One far\n" +
                        "One near\n" +
                        "Oh dear!\n" +
                        "Oh dear!\n" +
                        "\n" +
                        "Take the long way and spend the day\n" +
                        "Or take the short and go another way\n" +
                        "\n" +
                        "The choice is yours \n" +
                        "But you must move fast \n" +
                        "Input 'near' or 'far' to choose our paths", "ok");
        badNewsClue.addChild(CLUE_A_TITLE);
        badNewsClue.addChild(CLUE_B_TITE);
        scavengerHunt.addClue(badNewsClue);

        Clue forkClueNear = new TextClue(CLUE_A_TITLE,
                "Pick me! Pick me!\n" +
                        "I've got lots to do and see\n\n" +
                        "I don't super long drive\n" +
                        "And there'll be plenty of fun when you arrive!\n", ":)", "near");
        forkClueNear.addChild(CLUE_A1_NAME);
        scavengerHunt.addClue(forkClueNear);

        Clue forkClueFar = new TextClue(CLUE_B_TITE,
                "Pick me! Pick me!\n" +
                        "I've got lots to do and see\n\n" +
                        "I know I have a super long drive\n" +
                        "But it'll be worth it! It'll be one for the archive(s)!", ":)", "far");
        forkClueFar.addChild(CLUE_B1_NAME);
        scavengerHunt.addClue(forkClueFar);


        //colonial park fork clues
        GeoLocation colonialLocation = new GeoLocation(40.509270f, -74.573873f, 1000);
        Clue colonialClue = new GeofenceClue(CLUE_A1_NAME,
                "We've been here before - several times in fact\n\n" +
                 "For this community day favorite you won't need a contract [money]\n\n" +
                 "With family fun this park is jam-packed\n\n" +
                 "Dogs love it to, for their leash, you'll retract\n\n" +
                 "Hope you've got bug spray or bees you'll attract",
                "You made it!\n\n" +
                    "Continue on to discover all that's in store\n\n" +
                    "Don't get down, there's so much more!\n\n", colonialLocation, geofenceManager);
        colonialClue.addChild(CLUE_A2_NAME);
        colonialClue.addChild(CLUE_A3_NAME);
        colonialClue.addChild(CLUE_A4_NAME);
        colonialClue.addChild(CLUE_A5_NAME);
        colonialClue.addChild(CLUE_A6_NAME);
        scavengerHunt.addClue(colonialClue);

        GeoLocation paddleBoatLocation = new GeoLocation(40.510262f, -74.560895f, 10);
        Clue paddleBoatClue = new CompassClue(CLUE_A2_NAME,
                "Okay, remember when I said you didn't need money?\n\n" +
                        "I may have lied just to get a little rhy-mey\n\n" +
                        "But don't fret\n\n" +
                        "I've got you set\n\n" +
                        "For this potentially wet activity",
                "Good thing we did all that biking\n\n" +
                "You'll need the strength for this cycling (paddling)\n\n" +
                "Now, it's no hang gliding\n\n" +
                "But it still should be exciting", paddleBoatLocation);
        scavengerHunt.addClue(paddleBoatClue);

        GeoLocation minigolfLocation = new GeoLocation(40.510862f, -74.560938f, 10);
        Clue minigolfClue = new CompassClue(CLUE_A3_NAME,
                "Okay, remember when I said you didn't need money?\n\n" +
                        "I may have lied just to get a little rhy-mey\n\n" +
                        "But don't fret\n\n" +
                        "I've got you set\n\n" +
                        "For this patience-involving activity",
                "Wanna go minigolfing?", minigolfLocation);
        scavengerHunt.addClue(minigolfClue);

        GeoLocation tennisLocation = new GeoLocation(40.509757f, -74.566910f, 20);
        Clue tennisClue = new CompassClue(CLUE_A4_NAME,
                "This clue involves a little bit of sport\n\n" +
                        "Like a lot of things, it's played on a court\n\n" +
                        "A favorite of the french, you can play it if you're short (haha, napoleon joke)\n\n" +
                        "Hit low to high or your ball will fall short",
                "Wanna play tennis?\n" +
                        "It occurs to me now that we'll need to go get rackets, " +
                        "unless I managed to hide them in my backpack somehow.", tennisLocation);
        scavengerHunt.addClue(tennisClue);

        GeoLocation roseGardenLocation = new GeoLocation(40.508941f, -74.573140f, 20);
        Clue roseGardenClue = new CompassClue(CLUE_A5_NAME,
                "Do you smell it? That smell. A kind of smelly smell. A smelly smell that smells... smelly\n\n" +
                        "Roses!", "I don't remember what roses smell like. " +
                "If they smell good, i'd say \"they smell almost as good as you\"," +
                "and if they smell bad, i'd say \"dang these roses smell bad\".", roseGardenLocation);
        scavengerHunt.addClue(roseGardenClue);

        GeoLocation picnicLocation = new GeoLocation(40.510704f, -74.564166f, 20);
        Clue picnicClue = new CompassClue(CLUE_A6_NAME,
                "After all that, I've really worked up an apatite. Good thing I packed lunch!",
                "Oh, you thought I packed some for you? Smh, millennials these days and their handouts", picnicLocation);
        scavengerHunt.addClue(picnicClue);

        //norvin green fork clues
        Clue dummyClueOne = new TextClue(CLUE_B1_NAME, "Hey, Morgan," +
                "it's going to rain today and I don't want to get stuck out there. Another day.", "" ,"dummy thicc");
        scavengerHunt.addClue(dummyClueOne);

        Clue dummyClueTwo = new TextClue("Dummy Clue Two", "", "", "dummy thicc2");
        scavengerHunt.addClue(dummyClueTwo);

        Clue dummyClueThree = new TextClue("Dummy Clue Three", "", "", "dummy thicc3");
        scavengerHunt.addClue(dummyClueThree);

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
