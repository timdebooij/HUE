package com.timdebooij.hueapplicatie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;

import com.timdebooij.hueapplicatie.adapter.RecyclerViewAdapter;
import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.models.LightBulb;
import com.timdebooij.hueapplicatie.services.ApiListener;
import com.timdebooij.hueapplicatie.services.VolleyService;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ApiListener {

    public VolleyService service;
    public static ArrayList<Bridge> bridges;
    public RecyclerView recyclerView;
    public RecyclerViewAdapter adapter;
    public SeekBar seekbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bridges = new ArrayList<>();
        seekbar = findViewById(R.id.seekBarHue);

        service = new VolleyService(this.getApplicationContext(), this);
        setUpBridges();
        setUpRecyclerView();
        service.queue.start();

    }

    public void setUpRecyclerView(){
        recyclerView = findViewById(R.id.bridgeRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapter(this, bridges);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void setUpBridges(){
        try {
            bridges.add(new Bridge("192.168.2.17", "emulator", "80"));
            bridges.add(new Bridge("145.48.205.33", "LA AULA", "80"));
            bridges.add(new Bridge("192.168.1.179", "MAD LA-134", "80"));
            service.logIn(bridges.get(0));
            bridges.get(1).token = "iYrmsQq1wu5FxF9CPqpJCnm1GpPVylKBWDUsNDhB";
            logIn();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void logIn(){
        for(Bridge bridge : bridges){
            Log.i("info", "token: " + bridge.token);
            if(bridge.token != null){
                service.getLightsInBridge(bridge);
            }
        }
    }

    public void connect(View view) throws JSONException {
        service.logIn(bridges.get(0));
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
        Log.i("info", "amount of bulbs: " +  bridgeWithLightbulbs.lightBulbs.size());
        for(LightBulb bulb : bridgeWithLightbulbs.lightBulbs){
            Log.i("info", bulb.toString());
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.notifyDataSetChanged();
        Log.i("infosave", "onrestart called");
    }
}
