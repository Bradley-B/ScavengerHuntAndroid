package com.bradleyboxer.scavengerhunt;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ShareActionProvider;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class LauncherActivity extends AppCompatActivity {

    File savedHunt;
    boolean savedHuntExists;
    ArrayList<Clue> scavengerHunt;

    Button startButton;
    Button shareButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);

        startButton = findViewById(R.id.startButton);
        shareButton = findViewById(R.id.shareButton);

        scavengerHunt = getSavedHunt();
        updateButtonStates(savedHuntExists);

    }

    public void updateButtonStates(boolean savedHuntExists) {
        startButton.setEnabled(savedHuntExists);
        shareButton.setEnabled(savedHuntExists);
    }

    public ArrayList<Clue> getSavedHunt() {
        try {
            savedHunt = new File(getFilesDir(), "savedScavengerHunt");
            savedHuntExists = !savedHunt.createNewFile();
            if (savedHuntExists) {
                ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(savedHunt));
                return (ArrayList<Clue>) inputStream.readObject();
            }
        } catch (Exception e) {
            Log.e("GEOFENCE UI", "Exception in getting saved scavenger hunt", e);
            e.printStackTrace();
        }
        return null;
    }

    public void mergeHunt(File file, ArrayList<Clue> scavengerHunt) {
        ArrayList<Clue> oldHunt = getSavedHunt();
        oldHunt.addAll(scavengerHunt);
        saveHunt(file, oldHunt);
    }

    public void saveHunt(File file, ArrayList<Clue> scavengerHunt) { //overrides existing hunt
        try {
            file.delete();
            file.createNewFile();
            ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file));
            outputStream.writeObject(scavengerHunt);
            this.scavengerHunt = scavengerHunt;
            savedHuntExists = true;
            updateButtonStates(true);
        } catch (IOException e) {
            Log.e("GEOFENCE UI", "Exception in saving scavenger hunt", e);
            e.printStackTrace();
        }

    }

    public void onStartButton(View v) {
        for(Clue clue : scavengerHunt) {
            Clues.addClue(clue);
        }

        Intent intent = new Intent(this, MainActivity.class);
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
        String serializedClueList = Util.serialize(scavengerHunt);

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
                if(savedHuntExists) {
                    overrideOrMerge(savedHunt, clues);
                } else {
                    saveHunt(savedHunt, clues);
                }
            }
        }
    }
}
