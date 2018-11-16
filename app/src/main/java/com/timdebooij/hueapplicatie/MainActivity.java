package com.timdebooij.hueapplicatie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;

import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.models.LightBulb;
import com.timdebooij.hueapplicatie.services.ApiListener;
import com.timdebooij.hueapplicatie.services.VolleyService;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ApiListener {

    public Bridge bridge;
    public VolleyService service;
    public ArrayList<Bridge> bridges;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bridges = new ArrayList<>();
        bridges.add(new Bridge("145.49.4.15", "emulator", "80"));
        bridges.add(new Bridge("145.48.205.33", "LA AULA", "80"));
        bridges.add(new Bridge("192.168.1.179", "MAD LA-134", "80"));
        service = new VolleyService(this.getApplicationContext(), this);
        setUpBridges();
        service.queue.start();
    }

    public void setUpBridges(){
        try {
            service.logIn(bridges.get(0));
            bridges.get(1).token = "iYrmsQq1wu5FxF9CPqpJCnm1GpPVylKBWDUsNDhB";
            service.getLightsInBridge(bridges.get(1));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void connect(View view) throws JSONException {
        service.logIn(bridges.get(0));
    }

    public void getLights(View view){
        service.getLightsInBridge(bridges.get(0));
    }

    public void setLight(View view) throws JSONException {
        SeekBar seekbar = findViewById(R.id.seekBarHue);
        for(LightBulb bulb : bridges.get(1).lightBulbs) {
            service.setLight(bridges.get(1), bulb.id, seekbar.getProgress());
        }
        Log.i("info", "amount of bulbs in " + bridges.get(1).name + ": " + bridges.get(1).lightBulbs.size());
    }

    @Override
    public void onResponse(String response) {

    }

    @Override
    public void usernameReceived(Bridge bridgeWithToken) {
        Log.i("info", "Token is: " + bridgeWithToken.token);
        service.getLightsInBridge(bridges.get(0));
    }

    @Override
    public void onError(String error) {
        Log.i("info", error);
    }

    @Override
    public void onLightBulbs(Bridge bridgeWithLightbulbs) {

        for(LightBulb bulb : bridgeWithLightbulbs.lightBulbs){
            Log.i("info", bulb.toString());
        }
    }
}
