package com.timdebooij.hueapplicatie;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import com.timdebooij.hueapplicatie.adapter.RecyclerViewAdapterBulbs;
import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.models.LightBulb;
import com.timdebooij.hueapplicatie.services.ApiListener;
import com.timdebooij.hueapplicatie.services.VolleyService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class BridgeDetailActivity extends AppCompatActivity implements ApiListener {
    public Bridge bridge;
    public VolleyService service;
    public TextView connected;
    public int bridgeNumber;
    public RecyclerView recyclerView;
    public RecyclerViewAdapterBulbs adapter;
    public ArrayList<LightBulb> lightBulbs;
    public Spinner spinner;
    public ArrayAdapter<String> spinnerAdapter;
    public ArrayList<String> colorSchemeNames;
    public Map<String, com.timdebooij.hueapplicatie.models.Color> colorSchemes;
    public Switch lightSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_detail);
        Intent intent = getIntent();
        bridge = intent.getParcelableExtra("bridge");
        bridgeNumber = intent.getIntExtra("number", 0);
        lightBulbs = bridge.lightBulbs;
        service = new VolleyService(this.getApplicationContext(), this);
        getBridgeInfo();
        setUpRecyclerView();
        setUpSpinner();
    }

    public void setUpSpinner(){
        colorSchemeNames = new ArrayList<>();
        colorSchemeNames.add("Honolunu scheme");
        colorSchemeNames.add("Wake Up scheme");
        colorSchemeNames.add("Good Night scheme");
        colorSchemes = new HashMap<String, com.timdebooij.hueapplicatie.models.Color>();
        colorSchemes.put("Honolunu scheme", new com.timdebooij.hueapplicatie.models.Color(1713,254,254));
        colorSchemes.put("Wake Up scheme", new com.timdebooij.hueapplicatie.models.Color(8557, 156, 254));
        colorSchemes.put("Good Night scheme", new com.timdebooij.hueapplicatie.models.Color(47801,254,180));
        spinner = findViewById(R.id.colorSchemeSpinner);
        spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, colorSchemeNames);
        spinner.setAdapter(spinnerAdapter);
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
        Log.i("info", "amount of bulbs: " + bridge.lightBulbs.size());
        if(bridge.lightBulbs.size() != 0){
            connected.setText("is Connected");
            connected.setTextColor(Color.GREEN);
        }
        else{
            connected.setText("is not connected");
            connected.setTextColor(Color.RED);
        }
        lightSwitch = (Switch) findViewById(R.id.LightSwitchAllOn);
        for(LightBulb bulb : bridge.lightBulbs){
            if(bulb.on){
                lightSwitch.setChecked(true);
            }
        }
    }

    public void connect(View view) throws JSONException {
        Log.i("info", "trying to log in");
        service.logIn(bridge);
    }

    public void setAllLights(View view){
        com.timdebooij.hueapplicatie.models.Color color = colorSchemes.get(spinner.getSelectedItem().toString());
        for(LightBulb bulb : bridge.lightBulbs){
            try {
                service.setLight(bridge, bulb.id, color.hue, color.sat, color.bri);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void setLightbulbOnOff(View view) throws JSONException {
        boolean switchState = lightSwitch.isChecked();

        if (switchState){
            for(LightBulb bulb : bridge.lightBulbs) {
                service.switchLightOnOff(bridge, bulb.id, true);
            }
        } else if (switchState == false){
            for(LightBulb bulb : bridge.lightBulbs) {
                service.switchLightOnOff(bridge, bulb.id, false);
            }
        }

    }

    @Override
    public void onResponse(String response) {

    }

    @Override
    public void usernameReceived(Bridge bridgeWithToken) {
        SharedPreferences preferences = getSharedPreferences("BridgeTokens", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(bridgeWithToken.name, bridgeWithToken.token);
        editor.apply();
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
        lightBulbs.clear();
        lightBulbs.addAll(bridge.lightBulbs);
        adapter.notifyDataSetChanged();
        for(LightBulb bulb : bridge.lightBulbs){
            if(bulb.on){
                lightSwitch.setChecked(true);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.bridges.set(bridgeNumber, bridge);
        Log.i("infosave", "onpause called on: " + bridgeNumber + " with: " + bridge.token);
    }
}
