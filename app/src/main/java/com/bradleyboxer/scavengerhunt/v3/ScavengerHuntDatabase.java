package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ScavengerHuntDatabase implements Serializable {

    public static final String TAG = "FIRESTORE";

    public static final String DB_KEY_DATA = "scavengerHunt";
    public static final String DB_KEY_NAME = "name";

    public static final String PREFERENCES_KEY = "com.bradleyboxer.scavengerhunt.preferences";
    public static final String ACTIVE_SCAVENGER_HUNT_KEY = "activeScavengerHunt";

    private List<ScavengerHunt> localScavengerHunts;
    private transient FirebaseFirestore remoteDb;

    public ScavengerHuntDatabase() {
        remoteDb = FirebaseFirestore.getInstance();
        localScavengerHunts = new ArrayList<>();
    }

    public void resetRemoteDb() {
        remoteDb = FirebaseFirestore.getInstance();
    }

    public synchronized int[] getTotalClueStates() {
        int inactive = 0;
        int active = 0;
        int solved = 0;
        for(ScavengerHunt scavengerHunt : getScavengerHunts()) {
            int[] clueStates = scavengerHunt.getClueStates();
            inactive += clueStates[0];
            active += clueStates[1];
            solved += clueStates[2];

        }
        return new int[] {inactive, active, solved};
    }

    public float getTotalProgressPercent() {
        int[] totalClueStates = getTotalClueStates();
        int totalClues = totalClueStates[0]+totalClueStates[1]+totalClueStates[2];

        if(totalClues==0) {
            return 1;
        }
        return totalClueStates[2]/(float)totalClues;
    }

    public synchronized void setActiveScavengerHunt(Context context, UUID scavengerHuntId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        sharedPreferences.edit().putString(ACTIVE_SCAVENGER_HUNT_KEY, scavengerHuntId.toString()).commit();
    }

    @Nullable
    public synchronized ScavengerHunt getActiveScavengerHunt(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
        String activeScavengerHuntIdString = sharedPreferences.getString(ACTIVE_SCAVENGER_HUNT_KEY, "null");

        if(activeScavengerHuntIdString.equals("null")) {
            if(localScavengerHunts.isEmpty()) {
                return null;
            }
            setActiveScavengerHunt(context, localScavengerHunts.get(0).getUuid());
            return localScavengerHunts.get(0);
        }
        return getScavengerHunt(UUID.fromString(activeScavengerHuntIdString));
    }

    @Nullable
    public synchronized ScavengerHunt getScavengerHunt(UUID scavengerHuntId) {
        for(ScavengerHunt scavengerHunt : getScavengerHunts()) {
            if(scavengerHuntId.equals(scavengerHunt.getUuid())) {
                return scavengerHunt;
            }
        }
        return null;
    }

    @Nullable
    public synchronized Clue getClue(UUID clueId) {
        for(ScavengerHunt scavengerHunt : getScavengerHunts()) {
            Clue clue = scavengerHunt.getClue(clueId);
            if(clue!=null) {
                return clue;
            }
        }
        return null;
    }

    public synchronized void solveClue(UUID clueId) {
        List<ScavengerHunt> scavengerHunts = getScavengerHunts();
        for(ScavengerHunt scavengerHunt : scavengerHunts) {
            scavengerHunt.solveClue(clueId);
        }
    }

    public synchronized void solveClue(Clue clue) {
        solveClue(clue.getUuid());
    }

    public synchronized List<ScavengerHunt> getScavengerHunts() {
        return localScavengerHunts;
    }

    public synchronized void addScavengerHunt(ScavengerHunt scavengerHunt) {
        localScavengerHunts.add(scavengerHunt);
    }

    /**
     * Asynchronously downloads a scavenger hunt, adds it to this database, then saves this database to a file.
     * @param uuid the UUID of the scavenger hunt to download
     * @param callingActivity the activity that called this method
     */
    public synchronized void downloadScavengerHunt(final UUID uuid, final MainActivity callingActivity) {
        Task<DocumentSnapshot> downloadTask = remoteDb.collection("scavengerHunts").document(uuid.toString()).get();
        downloadTask.addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()) {
                            Log.d(TAG, "document downloaded with id: " + uuid.toString());
                            DocumentSnapshot document = task.getResult();
                            Map<String, Object> dbEntry = document.getData();

                            try {
                                final ScavengerHunt newScavengerHunt = ScavengerHunt.deserialize((String) dbEntry.get(DB_KEY_DATA), callingActivity);

                                //make sure there are no conflicts
                                if(getScavengerHunts().contains(newScavengerHunt)) {
                                    handleConflict(callingActivity, newScavengerHunt);
                                } else {
                                    localScavengerHunts.add(newScavengerHunt);
                                    FileUtil.saveScavengerHuntDatabase(ScavengerHuntDatabase.this, callingActivity);
                                    Notifications.displayAlertDialog("Success", "Scavenger hunt downloaded successfully!", callingActivity);

                                    callingActivity.setClueDisplays(ScavengerHuntDatabase.this);
                                    callingActivity.animateProgressBar(ScavengerHuntDatabase.this);
                                }

                            } catch (Exception e) {
                                Notifications.displayAlertDialog("Error", "Error decrypting scavenger hunt. Please confirm it was uploaded correctly.", callingActivity);
                            }

                        } else {
                            Log.w(TAG, "error getting document", task.getException());
                            Notifications.displayAlertDialog("Error", "Error downloading scavenger hunt. Check your internet connection, then try again.", callingActivity);
                        }
                    }
                });
    }

    private synchronized void handleConflict(final MainActivity callingActivity, final ScavengerHunt newScavengerHunt) {
        //conflict. Ask to override or merge

        AlertDialog.Builder builder = new AlertDialog.Builder(callingActivity);
        builder.setTitle("Action Required");
        builder.setMessage("This scavenger hunt is already downloaded. Would you like to preserve your current progress, or overwrite it?");
        builder.setPositiveButton("Preserve Progress", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ScavengerHunt oldScavengerHunt = getScavengerHunt(newScavengerHunt.getUuid());
                if(oldScavengerHunt != null) {
                    oldScavengerHunt.mergeWith(newScavengerHunt);

                    FileUtil.saveScavengerHuntDatabase(ScavengerHuntDatabase.this, callingActivity);
                    Notifications.displayAlertDialog("Success", "Scavenger hunt downloaded successfully!", callingActivity);

                    callingActivity.setClueDisplays(ScavengerHuntDatabase.this);
                    callingActivity.animateProgressBar(ScavengerHuntDatabase.this);
                }
            }
        });
        builder.setNegativeButton("Cancel Import", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {}
        });
        builder.setNeutralButton("Overwrite Progress", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                localScavengerHunts.remove(newScavengerHunt);
                localScavengerHunts.add(newScavengerHunt);

                FileUtil.saveScavengerHuntDatabase(ScavengerHuntDatabase.this, callingActivity);
                Notifications.displayAlertDialog("Success", "Scavenger hunt downloaded successfully!", callingActivity);

                callingActivity.setClueDisplays(ScavengerHuntDatabase.this);
                callingActivity.animateProgressBar(ScavengerHuntDatabase.this);
            }
        });
        builder.create().show();
    }

    public synchronized void uploadScavengerHunt(ScavengerHunt scavengerHunt, final Context context) {
        final ScavengerHunt scavengerHuntToUpload = new ScavengerHunt(scavengerHunt);

        //to store, mark all clues as inactive except the first
        for(Clue clue : scavengerHuntToUpload.getClueList()) {
            clue.resetState();
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
            Log.e(TAG, "oops", e);
        }
    }
}
