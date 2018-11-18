package com.timdebooij.hueapplicatie;

import android.content.Intent;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.timdebooij.hueapplicatie.adapter.RecyclerViewAdapter;
import com.timdebooij.hueapplicatie.adapter.RecyclerViewAdapterBulbs;
import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.models.LightBulb;
import com.timdebooij.hueapplicatie.services.ApiListener;
import com.timdebooij.hueapplicatie.services.VolleyService;

import org.json.JSONException;

import java.util.ArrayList;

public class BridgeDetailActivity extends AppCompatActivity implements ApiListener {
    public Bridge bridge;
    public VolleyService service;
    public SeekBar seekBar;
    public TextView connected;
    public int bridgeNumber;
    public RecyclerView recyclerView;
    public RecyclerViewAdapterBulbs adapter;
    public ArrayList<LightBulb> lightBulbs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_detail);
        Intent intent = getIntent();
        bridge = intent.getParcelableExtra("bridge");
        bridgeNumber = intent.getIntExtra("number", 0);
        lightBulbs = bridge.lightBulbs;

        seekBar = findViewById(R.id.seekBarHue);
        service = new VolleyService(this.getApplicationContext(), this);
        getBridgeInfo();
        setUpRecyclerView();
    }

    public void setUpRecyclerView(){
        recyclerView = findViewById(R.id.recyclerViewBulbs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapterBulbs(this, bridge.lightBulbs, bridge);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void getBridgeInfo(){
        TextView name = findViewById(R.id.bridgeName);
        name.setText("Bridge name: " + bridge.name);
        TextView ip = findViewById(R.id.bridgeIP);
        ip.setText("IP address: " + bridge.ipAddress);
        connected = findViewById(R.id.bridgeConnected);
        if(bridge.token != null){
            connected.setText("is Connected");
            connected.setTextColor(Color.GREEN);
        }
        else{
            connected.setText("is not connected");
            connected.setTextColor(Color.RED);
        }
    }

    public void connect(View view) throws JSONException {
        service.logIn(bridge);
    }


    public void setLightbulbOnOff(View view) throws JSONException {
        Switch lightSwitch = (Switch) findViewById(R.id.LightSwitch);
        boolean switchState = lightSwitch.isChecked();

        if (switchState){
            service.switchLightOnOff(bridge,"1",true);
        } else if (switchState == false){
            service.switchLightOnOff(bridge,"1",false);
        }

    }

    @Override
    public void onResponse(String response) {

    }

    @Override
    public void usernameReceived(Bridge bridgeWithToken) {
        Log.i("info", "Token is: " + bridgeWithToken.token);
        service.getLightsInBridge(bridge);
        connected.setText("is Connected");
        connected.setTextColor(Color.GREEN);
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
        if(bridge.lightBulbs.size() > 0){
            seekBar.setProgress(bridge.lightBulbs.get(0).hue);
        }
        lightBulbs.clear();
        lightBulbs.addAll(bridge.lightBulbs);
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.bridges.set(bridgeNumber, bridge);
        Log.i("infosave", "onpause called on: " + bridgeNumber + " with: " + bridge.token);
    }
}
