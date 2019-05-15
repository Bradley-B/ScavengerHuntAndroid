package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class FileUtil {

    public static synchronized void saveScavengerHunt(ScavengerHunt scavengerHunt, Context context) {
        try {
            Log.i("GEOFENCE UI", "saving scavenger hunt: "+scavengerHunt.getClueList().size());
            FileOutputStream fos = context.openFileOutput("savedScavengerHunt", Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(scavengerHunt);
            os.close();
            fos.close();
        } catch (Exception e) {
            Log.e("GEOFENCE UI", "Exception in saving scavenger hunt", e);
            e.printStackTrace();
        }
    }

    public static synchronized ScavengerHunt loadScavengerHunt(Context context) {
        try {
            FileInputStream fis = context.openFileInput("savedScavengerHunt");
            ObjectInputStream is = new ObjectInputStream(fis);
            ScavengerHunt scavengerHunt = (ScavengerHunt) is.readObject();
            is.close();
            fis.close();

            GeofenceManager geofenceManager = new GeofenceManager(context);
            for(Clue clue : scavengerHunt.getClueList()) {
                if(clue.getType().equals(Clue.Type.GEOFENCE)) {
                    GeofenceClue geofenceClue = (GeofenceClue) clue;
                    geofenceClue.setGeofenceManager(geofenceManager);
                }
            }
            return scavengerHunt;
        } catch (Exception e) {
            Log.e("GEOFENCE UI", "Exception in loading scavenger hunt", e);
            e.printStackTrace();
        }
        return null;
    }
}
