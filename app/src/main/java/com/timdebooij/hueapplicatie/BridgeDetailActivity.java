package com.timdebooij.hueapplicatie;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Parcelable;
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
import android.widget.Toast;

import com.timdebooij.hueapplicatie.adapter.RecyclerViewAdapterBulbs;
import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.models.ColorScheme;
import com.timdebooij.hueapplicatie.database.DatabaseColorScheme;
import com.timdebooij.hueapplicatie.models.LightBulb;
import com.timdebooij.hueapplicatie.services.ApiListener;
import com.timdebooij.hueapplicatie.services.VolleyService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BridgeDetailActivity extends AppCompatActivity implements ApiListener {
    public static Bridge bridge;
    public VolleyService service;
    public TextView connected;
    public int bridgeNumber;
    public RecyclerView recyclerView;
    public static RecyclerViewAdapterBulbs adapter;
    public static ArrayList<LightBulb> lightBulbsAdapterSet;
    public Spinner spinner;
    public static ArrayAdapter<String> spinnerAdapter;
    public static ArrayList<String> colorSchemeNames;
    public static Map<String, ColorScheme> colorSchemes;
    public Switch lightSwitch;
    private static final String DATABASE_NAME = "ColorSchemes_db";
    private DatabaseColorScheme database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bridge_detail);
        Intent intent = getIntent();
        lightBulbsAdapterSet = new ArrayList<>();
        colorSchemeNames = new ArrayList<>();
        colorSchemes = new HashMap<>();
        bridge = intent.getParcelableExtra("bridge");
        bridgeNumber = intent.getIntExtra("number", 0);
        service = new VolleyService(this.getApplicationContext(), this);
        getBridgeInfo();
        setUpRecyclerView();
        setUpSpinner();

        database = Room.databaseBuilder(getApplicationContext(), DatabaseColorScheme.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

        setUpReturnThread();
    }

    public void addNewScheme(View view) {
        Intent intent = new Intent(this, SchemeAdder.class);
        startActivity(intent);
    }

    public void setUpReturnThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<ColorScheme> schemes = new ArrayList<>();
                schemes = database.daoAccess().getAllSchemes();
                Log.i("infodata", "database size: " + schemes.size());
                for(ColorScheme scheme : schemes){
                    colorSchemes.put(scheme.getSchemeName(), scheme);
                    colorSchemeNames.add(scheme.getSchemeName());
                }
                spinnerAdapter.notifyDataSetChanged();
            }
        }) .start();

    }


    public void setUpSpinner(){
        spinner = findViewById(R.id.colorSchemeSpinner);
        spinnerAdapter = new ArrayAdapter<String>(this, R.layout.spinner_item, colorSchemeNames);
        spinner.setAdapter(spinnerAdapter);
    }

    public void setUpRecyclerView(){
        recyclerView = findViewById(R.id.recyclerViewBulbs);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RecyclerViewAdapterBulbs(this, lightBulbsAdapterSet, bridge);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public void getBridgeInfo(){
        TextView name = findViewById(R.id.bridgeName);
        name.setText(bridge.name);
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
        lightBulbsAdapterSet.clear();
        lightBulbsAdapterSet.addAll(bridge.lightBulbs);
    }

    public void deleteScheme(View view){
        final ColorScheme color = colorSchemes.get(spinner.getSelectedItem().toString());
        if(colorSchemeNames.indexOf(color.getSchemeName())>2){
            colorSchemeNames.remove(color.getSchemeName());
            colorSchemes.remove(color.getSchemeName());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    database.daoAccess().deleteColorScheme(color);
                }
            }) .start();
            spinnerAdapter.notifyDataSetChanged();
        }
        else{
            Toast.makeText(this, "Can't delete standard schemes", Toast.LENGTH_SHORT).show();
        }
    }

    public void connect(View view) throws JSONException {
        Log.i("info", "trying to log in");
        if (bridge.token != null && bridge.token != "") {
            service.getLightsInBridge(bridge);

        } else
            {
            service.logIn(bridge);
        }
    }

    public void setAllLights(View view){
        ColorScheme color = colorSchemes.get(spinner.getSelectedItem().toString());
        for(LightBulb bulb : lightBulbsAdapterSet){
            try {
                lightBulbsAdapterSet.get(bridge.lightBulbs.indexOf(bulb)).hue = color.getHue();
                lightBulbsAdapterSet.get(bridge.lightBulbs.indexOf(bulb)).sat = color.getSat();
                lightBulbsAdapterSet.get(bridge.lightBulbs.indexOf(bulb)).bri = color.getBri();
                service.setLight(bridge, bulb.id, color.getHue(), color.getSat(), color.getBri());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        adapter.notifyDataSetChanged();
    }

    public void setLightbulbOnOff(View view) throws JSONException {
        boolean switchState = lightSwitch.isChecked();

        if (switchState){
            for(LightBulb bulb : bridge.lightBulbs) {
                bridge.lightBulbs.get(bridge.lightBulbs.indexOf(bulb)).on = true;
                service.switchLightOnOff(bridge, bulb.id, true);
                adapter.notifyDataSetChanged();
            }
        } else if (switchState == false){
            for(LightBulb bulb : bridge.lightBulbs) {
                bridge.lightBulbs.get(bridge.lightBulbs.indexOf(bulb)).on = false;
                service.switchLightOnOff(bridge, bulb.id, false);
                adapter.notifyDataSetChanged();
            }
        }

    }

    public void fillDatabase()
    {
        new Thread(new Runnable() {
            @Override
            public void run() {
                ColorScheme scheme = new ColorScheme();
                scheme.setSchemeName("Honolulu scheme");
                scheme.setHue(1713);
                scheme.setBri(254);
                scheme.setSat(254);
                database.daoAccess().insertColorScheme(scheme);
                ColorScheme scheme2 = new ColorScheme();
                scheme2.setSchemeName("Wake Up scheme");
                scheme2.setHue(8557);
                scheme2.setBri(254);
                scheme2.setSat(156);
                database.daoAccess().insertColorScheme(scheme2);
                ColorScheme scheme3 = new ColorScheme();
                scheme3.setSchemeName("Goodnight scheme");
                scheme3.setHue(47801);
                scheme3.setBri(180);
                scheme3.setSat(254);
                database.daoAccess().insertColorScheme(scheme3);
                spinnerAdapter.notifyDataSetChanged();
            }
        }).start();

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

    }

    @Override
    public void onError(String error) {
        Log.i("info", error);
    }

    @Override
    public void onLightBulbs(Bridge bridgeWithLightbulbs) {
        Log.i("info", "amount of bulbs received: " +  bridgeWithLightbulbs.lightBulbs.size());
        for(LightBulb bulb : bridgeWithLightbulbs.lightBulbs){
            Log.i("info", bulb.toString());
        }
        //bridge.lightBulbs.clear();
        //bridge.lightBulbs.addAll(bridgeWithLightbulbs.lightBulbs);
        Log.i("info", "amount of bulbs in bridge: " +  bridge.lightBulbs.size());
        lightBulbsAdapterSet.clear();
        lightBulbsAdapterSet.addAll(bridge.lightBulbs);
        adapter.notifyDataSetChanged();
        for(LightBulb bulb : bridge.lightBulbs){
            Log.i("info", "Reached");
            if(bulb.on){
                lightSwitch.setChecked(true);
            }
        }
        connected.setText("is Connected");
        connected.setTextColor(Color.GREEN);
    }

    @Override
    public void onNewScheme(ColorScheme scheme) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        MainActivity.bridges.set(bridgeNumber, bridge);
        Log.i("infosave", "onpause called on: " + bridgeNumber + " with: " + bridge.token);
    }
}
