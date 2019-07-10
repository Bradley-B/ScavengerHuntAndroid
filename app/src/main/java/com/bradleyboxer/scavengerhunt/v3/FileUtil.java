package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtil {

    public static synchronized void saveScavengerHuntDatabase(ScavengerHuntDatabase scavengerHuntDatabase, Context context) {
        try {
            Log.i("GEOFENCE UI", "saving scavenger hunt ");
            FileOutputStream fos = context.openFileOutput("savedScavengerHunts", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(scavengerHuntDatabase);
            os.close();
            fos.close();
        } catch (Exception e) {
            Log.e("GEOFENCE UI", "Exception in saving scavenger hunt database", e);
            e.printStackTrace();
        }
    }

    public static synchronized ScavengerHuntDatabase loadScavengerHuntDatabase(Context context) {
        try {
            FileInputStream fis = context.openFileInput("savedScavengerHunts");
            ObjectInputStream is = new ObjectInputStream(fis);
            ScavengerHuntDatabase scavengerHuntDatabase = (ScavengerHuntDatabase) is.readObject();
            is.close();
            fis.close();

            scavengerHuntDatabase.resetRemoteDb();

            GeofenceManager geofenceManager = new GeofenceManager(context);
            for(ScavengerHunt scavengerHunt : scavengerHuntDatabase.getScavengerHunts()) {
                for(Clue clue : scavengerHunt.getClueList()) {
                    if(clue.getType().equals(Clue.Type.GEOFENCE)) {
                        GeofenceClue geofenceClue = (GeofenceClue) clue;
                        geofenceClue.setGeofenceManager(geofenceManager);
                    }
                }
            }
            return scavengerHuntDatabase;
        } catch (Exception e) {
            Log.e("GEOFENCE UI", "Exception in loading scavenger hunt", e);
        }
        return null;
    }
}
