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

import com.skydoves.colorpickerpreference.ColorEnvelope;
import com.skydoves.colorpickerpreference.ColorListener;
import com.skydoves.colorpickerpreference.ColorPickerView;
import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.models.LightBulb;
import com.timdebooij.hueapplicatie.services.ApiListener;
import com.timdebooij.hueapplicatie.services.VolleyService;

import org.json.JSONException;

public class LightbulbDetailActivity extends AppCompatActivity implements ApiListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lightbulb_detail);
        service = new VolleyService(this.getApplicationContext(), this);

        Intent intent = getIntent();
        bulb = intent.getParcelableExtra("bulb");
        Log.i("infobulb", bulb.name);
        initializeBulb();
        bridge = intent.getParcelableExtra("bridge");
        colorPickerView = findViewById(R.id.colorPickerView);
        colorPickerView.setColorListener(new ColorListener() {
            @Override
            public void onColorSelected(ColorEnvelope colorEnvelope) {
                int[] rgb = colorEnvelope.getColorRGB();
                float[] hsv = new float[3];
                Color.RGBToHSV(rgb[0], rgb[1], rgb[2], hsv);
                int hue = ((int)(hsv[0]/360*65535));
                int sat = ((int)(hsv[1]*254));
                int bri = ((int)(hsv[2]*254));
                try {
                    service.setLight(bridge, bulb.id, hue, sat, bri);
                    setInfoText(hue, sat, bri);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void setInfoText(int hue, int sat, int bri){
        this.hue.setText("HUE: " + hue);
        this.sat.setText("SAT: " + sat);
        this.bri.setText("BRI: " + bri);
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
        } else if (switchState == false){
            service.switchLightOnOff(bridge,bulb.id,false);
            on.setText("OFF");
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
}
