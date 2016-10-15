package io.purukaushik.placesexample;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    Intent placesService;
    Handler placesHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            //TODO: Print location on screen
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        placesService = new Intent(MainActivity.this, PlacesService.class);
        startService(placesService);
    }


}
