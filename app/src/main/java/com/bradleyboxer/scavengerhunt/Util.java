package com.bradleyboxer.scavengerhunt;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class Util {

    public static void displayOkDialog(Context context, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(message);
        builder.setNeutralButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                //do nothing
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
