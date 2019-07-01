package com.bradleyboxer.scavengerhunt.v3;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.Spinner;

import com.bradleyboxer.scavengerhunt.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class GenerateQrActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        ScavengerHunt scavengerHunt = (ScavengerHunt) parent.getItemAtPosition(pos);

        ImageView imageView = findViewById(R.id.generated_qr_image);
        try {
            QrEntry qrEntry = new QrEntry(QrEntry.Type.SCAVENGER_HUNT, scavengerHunt.getUuid());
            Bitmap bitmap = encodeAsBitmap(qrEntry.serialize());
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        //TODO upload scavenger hunt here
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        ImageView imageView = findViewById(R.id.generated_qr_image);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_menu_qr, getTheme());
        imageView.setImageDrawable(drawable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ImageView imageView = findViewById(R.id.generated_qr_image);
        Drawable drawable = getResources().getDrawable(R.drawable.ic_menu_qr, getTheme());
        imageView.setImageDrawable(drawable);

        ScavengerHuntDatabase scavengerHuntDatabase = FileUtil.loadScavengerHuntDatabase(this);
        List<ScavengerHunt> scavengerHunts = scavengerHuntDatabase.getScavengerHunts();

        Spinner spinner = (Spinner) findViewById(R.id.scavengerhunt_spinner);
        ArrayAdapter<ScavengerHunt> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, scavengerHunts);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private Bitmap encodeAsBitmap(String str) throws WriterException {
        final int WIDTH = 300;

        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, WIDTH, WIDTH, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, WIDTH, 0, 0, w, h);
        return bitmap;
    }

}
