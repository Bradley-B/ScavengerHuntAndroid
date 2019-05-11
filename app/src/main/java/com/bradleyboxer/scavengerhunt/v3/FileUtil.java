package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class FileUtil {

    public static void saveScavengerHunt(ScavengerHunt scavengerHunt, Context context) {
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

    public static ScavengerHunt loadScavengerHunt(Context context) {
        try {
            FileInputStream fis = context.openFileInput("savedScavengerHunt");
            ObjectInputStream is = new ObjectInputStream(fis);
            ScavengerHunt scavengerHunt = (ScavengerHunt) is.readObject();
            is.close();
            fis.close();
            return scavengerHunt;
        } catch (Exception e) {
            Log.e("GEOFENCE UI", "Exception in loading scavenger hunt", e);
            e.printStackTrace();
        }
        return null;
    }
}
