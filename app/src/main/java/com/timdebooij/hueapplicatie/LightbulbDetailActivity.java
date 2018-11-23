package com.timdebooij.hueapplicatie;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerView;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;
import com.skydoves.colorpickerview.listeners.ColorListener;
import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.models.ColorScheme;
import com.timdebooij.hueapplicatie.models.LightBulb;
import com.timdebooij.hueapplicatie.services.ApiListener;
import com.timdebooij.hueapplicatie.services.VolleyService;

import org.json.JSONException;


import top.defaults.colorpicker.ColorObserver;

public class LightbulbDetailActivity extends AppCompatActivity implements ApiListener, ColorObserver {
    public VolleyService service;
    public LightBulb bulb;
    public Bridge bridge;
    public TextView on;
    public TextView name;
    public TextView hue;
    public TextView bri;
    public TextView sat;
    public Switch lightSwitch;
    public ColorPickerView colorPickerView;
    public top.defaults.colorpicker.ColorPickerView colorPicker;
    public float huee;
    public float satt;
    public float brii;
    float[] hsv;
    public int number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightbulb_detail);
        service = new VolleyService(this.getApplicationContext(), this);

        Intent intent = getIntent();
        number = intent.getIntExtra("number", 0);
        bulb = intent.getParcelableExtra("bulb");
        Log.i("infocom", "hue: " + bulb.hue);
        initializeBulb();
        bridge = intent.getParcelableExtra("bridge");
        colorPicker = findViewById(R.id.colorPickerArsenal);

        float hue = (float)bulb.hue/65535*360;
        float sat = (float)bulb.sat/254;
        float bri = (float)bulb.bri/254;
        float[] hsb = {hue, sat, bri};
        int color = Color.HSVToColor(hsb);
        colorPicker.setInitialColor(color);
        colorPicker.subscribe(this);
    }



    public void setInfoText(int hue, int sat, int bri){
        this.hue.setText("HUE: " + hue);
        this.hue.setTextColor(Color.HSVToColor(hsv));
        this.sat.setText("SAT: " + sat);
        this.sat.setTextColor(Color.HSVToColor(hsv));
        this.bri.setText("BRI: " + bri);
        this.bri.setTextColor(Color.HSVToColor(hsv));
        this.name.setTextColor(Color.HSVToColor(hsv));
    }

    public void initializeBulb(){
        on = findViewById(R.id.bulbOn);
        name = findViewById(R.id.bulbName);
        hue = findViewById(R.id.bulbHue);
        bri = findViewById(R.id.bulbBri);
        sat = findViewById(R.id.bulbSat);
        lightSwitch = (Switch) findViewById(R.id.lightSwitchBulb);

        if(bulb.on){
            on.setText("ON");
        }
        else{
            on.setText("OFF");
        }
        name.setText(bulb.name);
        hue.setText("HUE: " + bulb.hue);
        bri.setText("BRI: " + bulb.bri);
        sat.setText("SAT: " + bulb.sat);
        lightSwitch.setChecked(bulb.on);
    }

    public void setLightbulbOnOff(View view) throws JSONException {
        boolean switchState = lightSwitch.isChecked();

        if (switchState){
            service.switchLightOnOff(bridge,bulb.id,true);
            on.setText("ON");
            bulb.on = true;
        } else if (switchState == false){
            service.switchLightOnOff(bridge,bulb.id,false);
            on.setText("OFF");
            bulb.on = false;
        }
    }

    public com.timdebooij.hueapplicatie.models.Color hex2Rgb(int colorStr) {
        String hexColor = String.format("#%06X", (0xFFFFFF & colorStr));

        return new com.timdebooij.hueapplicatie.models.Color(
                Integer.valueOf( hexColor.substring( 1, 3 ), 16 ),
                Integer.valueOf( hexColor.substring( 3, 5 ), 16 ),
                Integer.valueOf( hexColor.substring( 5, 7 ), 16 ));
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
    public void onColor(int color, boolean fromUser) {
        Log.i("infocom", "color got: " + color);
        com.timdebooij.hueapplicatie.models.Color c = hex2Rgb(color);
        //Log.i("infocom", "set r: " + c.hue);
        //Log.i("infocom", "set g to: " + c.sat);
        //Log.i("infocom", "set b to: " + c.bri);
        int[] rgb = {c.hue, c.sat, c.bri};
        hsv = new float[3];
        Color.RGBToHSV(rgb[0], rgb[1], rgb[2], hsv);

        Log.i("infocom", "set hue bef calc: " + hsv[0]);
        huee = ((int)(hsv[0]/360*65535));
        satt = ((int)(hsv[1]*254));
        brii = ((int)(hsv[2]*254));
//        if(huee >= 32767){
//            huee = huee + 43;
//        }
        Log.i("infocom", "set hue to: " + huee);
        bulb.hue = (int) huee;
        bulb.sat = (int) satt;
        bulb.bri = (int) brii;
        try {
            service.setLight(bridge, bulb.id, (int)huee, (int)satt, (int)brii);
            setInfoText((int)huee, (int)satt, (int)brii);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        bridge.lightBulbs.set(number, bulb);
        Log.i("infocom", "HUE value send: " + bridge.lightBulbs.get(number).hue);

        BridgeDetailActivity.bridge.lightBulbs.clear();
        BridgeDetailActivity.bridge.lightBulbs.addAll(bridge.lightBulbs);
        BridgeDetailActivity.lightBulbsAdapterSet.clear();
        BridgeDetailActivity.lightBulbsAdapterSet.addAll(bridge.lightBulbs);
        BridgeDetailActivity.adapter.notifyDataSetChanged();
        super.onPause();
    }
}
