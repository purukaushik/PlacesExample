package io.purukaushik.placesexample;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.ResultTransform;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceLikelihood;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class PlacesService extends IntentService implements GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;

    public static final String TAG = "PlacesService";

    List<Integer> places = new ArrayList<>();
    public PlacesService() {
        super("PlacesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Starting thread.");


        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {

            @Override
            public void run() {
                if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED) {
                    Log.e("PlacesService: Thread", "Broken Permissions.");
                    return;
                }
                Log.i(TAG, "Getting Your Current Place.");

                Log.i(TAG, "Hearbeat");
                Log.i(TAG, "mGoogleApiClient: " + mGoogleApiClient.toString());

                if (mGoogleApiClient.isConnected()) {
                    Log.i(TAG, "Connected to API server.");
                } else {
                    Log.i(TAG, "not connected. Connecting...");
                    mGoogleApiClient.connect();
                    Log.i(TAG, "Connected again.");
                }
                PendingResult<PlaceLikelihoodBuffer> result = Places.PlaceDetectionApi
                        .getCurrentPlace(mGoogleApiClient, null);
                Log.i(TAG, "PlaceApiObject: " + result.toString());
//                Log.i(TAG, "Awaiting Results");
//                result.await();
//                Log.i(TAG, "Result ready");
                String place = "";

                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {

                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                        Toast.makeText(getBaseContext(), String.format("You're at: %s ", likelyPlaces.get(0).getPlace().getName()), Toast.LENGTH_SHORT).show();
                        places = likelyPlaces.get(0).getPlace().getPlaceTypes();
                        Log.i(TAG, String.format("", places));

                        likelyPlaces.release();
                    }
                });
                for(Integer i : places){
                    Log.i(TAG,"Place Id: "+i);
                }

            }

        }, 0, 10000L);


        Log.i(TAG, "Thread Started.");

    }

    public void onCreate() {
        super.onCreate();
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).build();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Toast.makeText(this, "Starting to identify places.", Toast.LENGTH_SHORT).show();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "No Api connection");
    }

}
