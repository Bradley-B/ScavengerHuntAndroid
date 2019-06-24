package com.bradleyboxer.scavengerhunt.v2;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.bradleyboxer.scavengerhunt.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class LauncherActivity extends AppCompatActivity {

    Button startButton;
    Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.v2_activity_launcher);

        startButton = findViewById(R.id.v2_startButton);
        shareButton = findViewById(R.id.v2_shareButton);

        updateButtonStates(hasSavedHunt());

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    public void updateButtonStates(boolean savedHuntExists) {
        startButton.setEnabled(savedHuntExists);
        shareButton.setEnabled(savedHuntExists);
    }

    public boolean hasSavedHunt() {
        File savedHunt = new File(getFilesDir(), "savedScavengerHunt");
        return savedHunt.exists();
    }

    public void mergeHunt(File file, ArrayList<Clue> scavengerHunt) {
        ArrayList<Clue> oldHunt = Clues.getSafeClueList(getFilesDir());
        oldHunt.addAll(scavengerHunt);
        saveHunt(file, oldHunt);
    }

    public void saveHunt(File file, ArrayList<Clue> scavengerHunt) { //overrides existing hunt
        try {
            file.delete();
            file.createNewFile();
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(scavengerHunt);
            updateButtonStates(hasSavedHunt());
        } catch (IOException e) {
            Log.e("GEOFENCE UI", "Exception in saving scavenger hunt", e);
            e.printStackTrace();
        }

    }

    public void onStartButton(View v) {
        ArrayList<Clue> clueList = Clues.getSafeClueList(getFilesDir());
        for(Clue clue : clueList) {
            Clues.addClue(clue);
        }

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("clueList", clueList);
        startActivity(intent);
    }

    public void onCreateButton(View v) {
        Intent intent = new Intent(this, ScavengerHuntCreatorActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onLoadButton(View v) {
        Intent intent = new Intent(this, ScavengerHuntRecieverActivity.class);
        startActivityForResult(intent, 1);
    }

    public void onShareButton(View v) {
        String serializedClueList = Util.serialize(Clues.getSafeClueList(getFilesDir()));

        //String existingButtonText = shareButton.getText().toString();
        //shareButton.setText("please wait. this could take a bit");
        //shareButton.setEnabled(false);
        //something in this method takes awhile... put that method call here
        //shareButton.setText(existingButtonText);
        //shareButton.setEnabled(true);

        if(serializedClueList != null) {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, "Clue List");
            intent.putExtra(Intent.EXTRA_TEXT, serializedClueList);
            startActivity(Intent.createChooser(intent, "Share via"));
        }
    }

    public void overrideOrMerge(final File file, final ArrayList<Clue> scavengerHunt) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Existing scavenger hunt found. Would you like to override it or merge with it?");
        builder.setPositiveButton("Override", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                saveHunt(file, scavengerHunt);
            }
        });
        builder.setNegativeButton("Merge", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                mergeHunt(file, scavengerHunt);
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK) {
            ArrayList<Clue> clues = (ArrayList<Clue>) data.getSerializableExtra("clueList");
            Log.i("GEOFENCE UI", "recieved "+clues.size()+" clues");
            if(clues!=null && clues.size()>0) {
                File file = Clues.getSavedScavengerHuntLocation(getFilesDir());
                if(hasSavedHunt()) {
                    overrideOrMerge(file, clues);
                } else {
                    saveHunt(file, clues);
                }
            }
        }
    }

}
