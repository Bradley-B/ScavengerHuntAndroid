package com.bradleyboxer.scavengerhunt.v3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonSyntaxException;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class QrScanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private ZXingScannerView mScannerView;
    public static final String QR_CODE_KEY = "qr";
    public static final int QR_REQUEST_CODE = 2;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        mScannerView = new ZXingScannerView(this);
        setContentView(mScannerView);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register ourselves as a handler for scan results.
        mScannerView.setResultHandler(this);
        // Start camera on resume
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop camera on pause
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        // Print scan results
        Log.i("QR Scanner", rawResult.getText());
        // Prints the scan format (qrcode, pdf417 etc.)
        Log.i("QR Scanner", rawResult.getBarcodeFormat().toString());

        try {
            QrEntry entry = QrEntry.deserialize(rawResult.getText());

            Intent intent = new Intent();
            intent.putExtra(QR_CODE_KEY, entry);
            setResult(RESULT_OK, intent);
            finish();
        } catch (JsonSyntaxException e) {
            Notifications.displayAlertDialog("Error", "This qr code does not belong to ScavengerHunt. " +
                    "If you were wondering, the data stored is:\n\n " + rawResult.getText(), this);

            mScannerView.resumeCameraPreview(this);
        }

    }

}
