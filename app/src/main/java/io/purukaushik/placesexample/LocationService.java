package io.purukaushik.placesexample;

import android.Manifest;
import android.app.IntentService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.PlaceLikelihoodBuffer;
import com.google.android.gms.location.places.Places;

import java.text.DateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * helper methods.
 */
public class LocationService extends IntentService implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    public static final String TAG = "LocationService";

    public LocationService() {
        super("LocationService");
    }

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private String lastUpdatedTime;

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Broken Permissions.");
            return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);
        lastUpdatedTime = DateFormat.getTimeInstance().format(new Date());
        if (mLastLocation != null) {
            Log.d(TAG, String.valueOf(mLastLocation.getLatitude()));
            Log.d(TAG, String.valueOf(mLastLocation.getLongitude()));
        }

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mGoogleApiClient = new GoogleApiClient.Builder(this).addApi(Places.GEO_DATA_API).addApi(Places.PLACE_DETECTION_API).build();
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

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        lastUpdatedTime = DateFormat.getTimeInstance().format(new Date());

        /*
            Thread from PlaceService.java
         */

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
                String place = "";

                result.setResultCallback(new ResultCallback<PlaceLikelihoodBuffer>() {

                    @Override
                    public void onResult(PlaceLikelihoodBuffer likelyPlaces) {
                        Toast.makeText(getBaseContext(), String.format("LatLng : %s, %s",
                                mLastLocation.getLatitude(),mLastLocation.getLongitude()), Toast.LENGTH_SHORT);
                        Toast.makeText(getBaseContext(), String.format("You're at: %s ", likelyPlaces.get(0).getPlace().getName()), Toast.LENGTH_SHORT).show();
                        likelyPlaces.release();
                    }
                });

            }

        }, 0, 10000L);


        Log.i(TAG, "Thread Started.");
    }
}
