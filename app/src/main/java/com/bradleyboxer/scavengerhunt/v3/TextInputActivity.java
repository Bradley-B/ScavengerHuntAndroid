package com.bradleyboxer.scavengerhunt.v3;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bradleyboxer.scavengerhunt.R;

public class TextInputActivity extends MenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_text_input);

        super.onCreate(savedInstanceState);
        setCheckedId(R.id.nav_answer_text_clue);
    }

    public void onSubmitButton(View view) {
        EditText textInput = findViewById(R.id.editText3);
        String text = textInput.getText().toString();

        ScavengerHunt scavengerHunt = FileUtil.loadScavengerHunt(this);
        for(Clue clue : scavengerHunt.getClueList()) {
            if(clue.getType().equals(Clue.Type.TEXT)) {
                TextClue textClue = (TextClue) clue;
                if(textClue.shouldBeSolved(text)) {
                    scavengerHunt.solveClue(clue.getUuid());
                    Notifications.sendNotification(clue.getName(), this);
                }
            }
        }
        FileUtil.saveScavengerHunt(scavengerHunt, this);
    }
}
