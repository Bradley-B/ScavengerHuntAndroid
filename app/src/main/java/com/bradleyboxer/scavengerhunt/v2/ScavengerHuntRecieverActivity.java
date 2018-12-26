package com.bradleyboxer.scavengerhunt.v2;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;

import com.bradleyboxer.scavengerhunt.R;

import java.util.ArrayList;

public class ScavengerHuntRecieverActivity extends AppCompatActivity {

    EditText enteredText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_activity_scavenger_hunt_reciever);

        enteredText = findViewById(R.id.v2_serializedScavengerHunt);
    }

    @Override
    public void onBackPressed() {
        try {
            Intent intent = new Intent();
            String text = enteredText.getText().toString().trim();
            ArrayList<Clue> clueList = Util.deserialize(text);
            intent.putExtra("clueList", clueList);
            setResult(RESULT_OK, intent);
            finish();
        } catch (Exception e) {
            Log.e("GEOFENCE UI", "serialization error");

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Incorrect object serialization data. Please correct and try again.");
            builder.setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    //do nothing, they will try again
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    setResult(RESULT_CANCELED);
                    finish();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

        }
    }
}
