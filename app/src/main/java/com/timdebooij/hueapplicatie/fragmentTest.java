package com.timdebooij.hueapplicatie;

import android.app.Activity;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.timdebooij.hueapplicatie.adapter.RecyclerViewAdapter;
import com.timdebooij.hueapplicatie.adapter.RecyclerViewAdapterBulbs;
import com.timdebooij.hueapplicatie.database.DatabaseColorScheme;
import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.models.ColorScheme;
import com.timdebooij.hueapplicatie.models.LightBulb;
import com.timdebooij.hueapplicatie.services.ApiListener;
import com.timdebooij.hueapplicatie.services.VolleyService;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class fragmentTest extends AppCompatActivity implements ApiListener {

    public static Bridge bridge;
    public int bridgeNumber;
    public VolleyService service;
    public static ArrayList<LightBulb> lightBulbsAdapterSet;
    public static RecyclerViewAdapterBulbs adapter;
    public Spinner spinner;
    public static ArrayAdapter<String> spinnerAdapter;
    public static ArrayList<String> colorSchemeNames;
    public static Map<String, ColorScheme> colorSchemes;
    private static final String DATABASE_NAME = "ColorSchemes_db";
    public DatabaseColorScheme database;
    public SharedPreferences.Editor editor;
    public int position;
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences preferences = getSharedPreferences("BridgeTokens", MODE_PRIVATE);
        editor = preferences.edit();

        colorSchemeNames = new ArrayList<>();
        colorSchemes = new HashMap<>();
        Intent intent = getIntent();
        bridge = intent.getParcelableExtra("bridge");
        lightBulbsAdapterSet = bridge.lightBulbs;
        bridgeNumber = intent.getIntExtra("number", 0);
        service = new VolleyService(this.getApplicationContext(), this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_test);
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentbda);

        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //int pos = recyclerView.getLayoutManager().getPosition(recyclerView.getAdapter().);
        //Log.i("infosave", "saved with: " + pos);
        //outState.putInt("pos", pos);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onRestoreInstanceState(savedInstanceState, persistentState);
        Log.i("infosave", "restored");
        if(savedInstanceState != null){
            position = savedInstanceState.getInt("pos");
        }
        else{
            position = 0;
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.i("infosave", "restored");
        if(savedInstanceState != null){
            position = savedInstanceState.getInt("pos");
            Log.i("infosave", "restored with: " + position);
        }
        else{
            position = 0;
        }
    }

    @Override
    public void onResponse(String response) {

    }

    @Override
    public void usernameReceived(Bridge bridgeWithToken) {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onLightBulbs(Bridge bridgeWithLightbulbs) {

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

    public static class testFragment extends Fragment implements ApiListener{

        public Switch lightSwitch;
        public TextView connected;
        public fragmentTest act;
        public VolleyService service;
        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = new View(getActivity());
            view = inflater.inflate(R.layout.activity_bridge_detail, container, false);
            act = (fragmentTest) getActivity();
            service = new VolleyService(getContext(), this);


            TextView name = view.findViewById(R.id.bridgeName);
            name.setText(act.bridge.name);
            TextView ip = view.findViewById(R.id.bridgeIP);
            ip.setText(this.getString(R.string.ipAddressBridgeDetail) + act.bridge.ipAddress);
            connected = view.findViewById(R.id.bridgeConnected);
            if(act.bridge.lightBulbs.size() != 0){
                connected.setText(R.string.connectBridgeDetail);
                connected.setTextColor(Color.GREEN);
            }
            else{
                connected.setText(R.string.notConnectedBridgeDetail);
                connected.setTextColor(Color.RED);
            }

            act.spinner = view.findViewById(R.id.colorSchemeSpinner);
            spinnerAdapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, colorSchemeNames);
            act.spinner.setAdapter(spinnerAdapter);
            spinnerAdapter.notifyDataSetChanged();
            act.database = Room.databaseBuilder(getContext(), DatabaseColorScheme.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
            setUpReturnThread();
            Button choose = view.findViewById(R.id.buttonChoose2);
            choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ColorScheme color = colorSchemes.get(act.spinner.getSelectedItem().toString());
                    for(LightBulb bulb : lightBulbsAdapterSet){
                        try {
                            Log.i("info", "new bulb set in light");
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
            });

            Button add = view.findViewById(R.id.button2);
            add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), SchemeAdder.class);
                    startActivity(intent);
                }
            });

            Button delete= view.findViewById(R.id.button3);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ColorScheme color = colorSchemes.get(act.spinner.getSelectedItem().toString());
                    if(colorSchemeNames.indexOf(color.getSchemeName())>2){
                        colorSchemeNames.remove(color.getSchemeName());
                        colorSchemes.remove(color.getSchemeName());
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                act.database.daoAccess().deleteColorScheme(color);
                            }
                        }) .start();
                        spinnerAdapter.notifyDataSetChanged();
                    }
                    else{
                        Toast.makeText(getContext(), "Can't delete standard schemes", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            lightSwitch = (Switch) view.findViewById(R.id.LightSwitchAllOn);
            for(LightBulb bulb : act.bridge.lightBulbs){
                if(bulb.on){
                    lightSwitch.setChecked(true);
                }
            }
            lightSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    boolean switchState = lightSwitch.isChecked();

                    if (switchState){
                        for(LightBulb bulb : bridge.lightBulbs) {
                            bridge.lightBulbs.get(bridge.lightBulbs.indexOf(bulb)).on = true;
                            try {
                                service.switchLightOnOff(bridge, bulb.id, true);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                        }
                    } else if (switchState == false){
                        for(LightBulb bulb : bridge.lightBulbs) {
                            bridge.lightBulbs.get(bridge.lightBulbs.indexOf(bulb)).on = false;
                            try {
                                service.switchLightOnOff(bridge, bulb.id, false);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
            Button button = view.findViewById(R.id.bridgeConnectDetailButton);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (act.bridge.token != null && act.bridge.token == "fds") {
                        service.getLightsInBridge(act.bridge);
                        Log.i("info", "trying to get lights");
                    } else
                    {
                        try {
                            service.logIn(act.bridge);
                            Log.i("info", "trying to log in");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            return view;
        }

        public void setUpReturnThread(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    List<ColorScheme> schemes = new ArrayList<>();
                    schemes = act.database.daoAccess().getAllSchemes();
                    Log.i("infodata", "database size: " + schemes.size());
                    for(ColorScheme scheme : schemes){
                        colorSchemes.put(scheme.getSchemeName(), scheme);
                        colorSchemeNames.add(scheme.getSchemeName());
                        Log.i("infodata", "name: " + scheme.getSchemeName()+ " hue: " + scheme.getHue() + " sat: " + scheme.getSat() + " bri: " + scheme.getBri());
                    }
                    spinnerAdapter.notifyDataSetChanged();
                }
            }) .start();

        }

        @Override
        public void onResponse(String response) {

        }

        @Override
        public void usernameReceived(Bridge bridgeWithToken) {
            Log.i("info", "username received");

            act.editor.putString(bridgeWithToken.name, bridgeWithToken.token);
            act.editor.apply();
            service.getLightsInBridge(act.bridge);
        }

        @Override
        public void onError(String error) {

        }

        @Override
        public void onLightBulbs(Bridge bridgeWithLightbulbs) {
            lightBulbsAdapterSet.clear();
            lightBulbsAdapterSet.addAll(bridgeWithLightbulbs.lightBulbs);
            Log.i("info", "bulbs in adapter: " + lightBulbsAdapterSet.size());
            adapter.notifyDataSetChanged();
            connected.setText("is Connected");
            connected.setTextColor(Color.GREEN);
            for(LightBulb bulb : act.bridge.lightBulbs){
                if(bulb.on){
                    lightSwitch.setChecked(true);
                }
            }
        }

        @Override
        public void onNewScheme(ColorScheme scheme) {

        }
    }

    public static class fragmentBridgeAdapter extends Fragment {
        fragmentTest act;



        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            View view = new View(getActivity());
            view = inflater.inflate(R.layout.bridge_adapter, container, false);
            act = (fragmentTest) getActivity();

            act.recyclerView = view.findViewById(R.id.recyclerViewBulbs);
            act.recyclerView.setHasFixedSize(true);
            act.recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
            act.recyclerView.getLayoutManager().scrollToPosition(act.position);
            adapter = new RecyclerViewAdapterBulbs(getContext(), lightBulbsAdapterSet, act.bridge);
            act.recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            return view;
        }
    }
}
