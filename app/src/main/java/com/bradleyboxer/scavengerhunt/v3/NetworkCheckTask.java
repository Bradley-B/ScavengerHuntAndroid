package com.bradleyboxer.scavengerhunt.v3;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

import static com.bradleyboxer.scavengerhunt.v3.ScavengerHuntDatabase.TAG;

public class NetworkCheckTask extends AsyncTask<Void, Void, Boolean> {

    private WeakReference<Context> contextReference;

    public NetworkCheckTask(Context context) {
        this.contextReference = new WeakReference<>(context);
    }

    private void displayNetworkError(Context context) {
        Notifications.displayAlertDialog("Error", "Error contacting the network. Check your internet connection, then try again.", context);
    }

    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            // Network is present and connected
            isAvailable = true;
        }
        return isAvailable;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        Context context = contextReference.get();

        if (isNetworkAvailable(context)) {
            try {
                HttpsURLConnection urlc = (HttpsURLConnection) (new URL("https://www.google.com").openConnection());
                urlc.setRequestProperty("User-Agent", "Test");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(1500);
                urlc.connect();

                if(urlc.getResponseCode() == 200) {
                    Log.i(TAG, "internet connection check successful");
                    return true;
                } else {
                    Log.e(TAG, "internet connection check failed");
                    return false;
                }
            } catch (IOException e) {
                Log.e(TAG, "Error: ", e);
                return false;
            }
        } else {
            Log.d(TAG, "No network present");
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean successful) {
        super.onPostExecute(successful);
        if(!successful) {
            displayNetworkError(contextReference.get());
        }
    }
}
