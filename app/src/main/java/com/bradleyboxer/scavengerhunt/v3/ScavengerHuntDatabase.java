package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScavengerHuntDatabase {

    public static final String TAG = "FIRESTORE";

    public static final String DB_KEY_DATA = "scavengerHunt";
    public static final String DB_KEY_NAME = "name";

    private List<ScavengerHunt> localScavengerHunts;
    private FirebaseFirestore remoteDb;

    public ScavengerHuntDatabase() {
        remoteDb = FirebaseFirestore.getInstance();
        localScavengerHunts = new ArrayList<>();
    }

    public void addScavengerHunt(ScavengerHunt scavengerHunt) {
        localScavengerHunts.add(scavengerHunt);
    }

    public void downloadScavengerHunt(final UUID uuid, final Context context) {
        remoteDb.collection("scavengerHunts").document(uuid.toString()).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "document downloaded with id: " + uuid.toString());
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> dbEntry = document.getData();

                            try {
                                ScavengerHunt scavengerHunt = ScavengerHunt.deserialize((String) dbEntry.get(DB_KEY_DATA), context);
                                addScavengerHunt(scavengerHunt);
                            } catch (Exception e) {
                                Notifications.displayAlertDialog("Error", "Error decrypting scavenger hunt. Please confirm it was uploaded correctly.", context);
                            }

                        } else {
                            Log.w(TAG, "error getting document", task.getException());
                            Notifications.displayAlertDialog("Error", "Error downloading scavenger hunt. Check your internet connection, then try again.", context);
                        }
                    }
                });
    }

    public void uploadScavengerHunt(ScavengerHunt scavengerHunt, final Context context) {
        final ScavengerHunt scavengerHuntToUpload = new ScavengerHunt(scavengerHunt);
        List<Clue> clueList = scavengerHunt.getClueList();

        //to store, mark all clues as inactive except the first
        for(Clue clue : clueList) {
            clue.resetState();
            scavengerHuntToUpload.addClue(clue);
        }

        if(scavengerHuntToUpload.getClueList().isEmpty()) {
            Notifications.displayAlertDialog("Error", "You can't upload a scavenger hunt with no clues.", context);
            return;
        }
        scavengerHuntToUpload.getClueList().get(0).activate(); //TODO specify starting clues instead of always activating the first

        Map<String, Object> dbEntry = new HashMap<>();
        try {
            String toUpload = ScavengerHunt.serialize(scavengerHuntToUpload);
            dbEntry.put(DB_KEY_DATA, toUpload);
            dbEntry.put(DB_KEY_NAME, scavengerHuntToUpload.getName());

            //upload to firestore
            remoteDb.collection("scavengerHunts")
                    .document(scavengerHuntToUpload.getUuid().toString()).set(dbEntry)
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "error adding document", e);
                            Notifications.displayAlertDialog("Error", "Error uploading scavenger hunt. Check your internet connection, then try again.", context);
                        }
                    })
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void v) {
                            Log.d(TAG, "document added with id " + scavengerHuntToUpload.getUuid().toString());
                        }
                    });
        } catch (Exception e) {
            Notifications.displayAlertDialog("Error", "Error encrypting scavenger hunt. Please try again.", context);
        }
    }
}
