package com.bradleyboxer.scavengerhunt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Util {

    public static String serialize(ArrayList<Clue> clueList) {
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            ObjectOutputStream so = new ObjectOutputStream(bo);
            so.writeObject(clueList);
            so.flush();
            return new String(Base64.encode(bo.toByteArray(), Base64.DEFAULT));
        } catch (Exception e) {
            Log.e("GEOFENCE UI", "serialization error", e);
        }
        return null;
    }

    public static ArrayList<Clue> deserialize(String serializedObject) throws Exception {
        byte b[] = Base64.decode(serializedObject.getBytes(), Base64.DEFAULT);
        ByteArrayInputStream bi = new ByteArrayInputStream(b);
        ObjectInputStream si = new ObjectInputStream(bi);
        ArrayList<Clue> clueList = (ArrayList<Clue>) si.readObject();
        return clueList;
    }

}
