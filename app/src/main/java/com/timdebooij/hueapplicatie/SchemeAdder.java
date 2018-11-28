package com.timdebooij.hueapplicatie;

import android.arch.persistence.room.Room;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.timdebooij.hueapplicatie.database.DatabaseColorScheme;
import com.timdebooij.hueapplicatie.models.ColorScheme;

import java.util.ArrayList;
import java.util.List;

import top.defaults.colorpicker.ColorObserver;

public class SchemeAdder extends AppCompatActivity implements ColorObserver {
    public TextView name;
    public TextView hue;
    public TextView sat;
    public TextView bri;
    public int huee;
    public int satt;
    public int brii;
    private static final String DATABASE_NAME = "ColorSchemes_db";
    private DatabaseColorScheme database;
    public top.defaults.colorpicker.ColorPickerView colorPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme_adder);
        name =findViewById(R.id.editText);
        hue=findViewById(R.id.hueValue);
        sat=findViewById(R.id.satValue);
        bri=findViewById(R.id.briValue);
        database = Room.databaseBuilder(getApplicationContext(), DatabaseColorScheme.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
        colorPicker = findViewById(R.id.colorPickerScheme);
        colorPicker.subscribe(this);
    }

    public void addScheme(View view){
        final ColorScheme scheme = new ColorScheme();
        scheme.setSchemeName(name.getText().toString());
        scheme.setHue(huee);
        scheme.setSat(satt);
        scheme.setBri(brii);
        fragmentTest.colorSchemeNames.add(scheme.getSchemeName());
        fragmentTest.colorSchemes.put(scheme.getSchemeName(), scheme);
        fragmentTest.spinnerAdapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.daoAccess().insertColorScheme(scheme);
            }
        }) .start();
        finish();
    }

    public com.timdebooij.hueapplicatie.models.Color hex2Rgb(int colorStr) {
        String hexColor = String.format("#%06X", (0xFFFFFF & colorStr));

        return new com.timdebooij.hueapplicatie.models.Color(
                Integer.valueOf( hexColor.substring( 1, 3 ), 16 ),
                Integer.valueOf( hexColor.substring( 3, 5 ), 16 ),
                Integer.valueOf( hexColor.substring( 5, 7 ), 16 ));
    }

    @Override
    public void onColor(int color, boolean fromUser) {
        com.timdebooij.hueapplicatie.models.Color c = hex2Rgb(color);
        int[] rgb = {c.hue, c.sat, c.bri};
        float[] hsv = new float[3];
        Color.RGBToHSV(rgb[0], rgb[1], rgb[2], hsv);

        Log.i("infocol", "hue val " + hsv[0]);
        huee = ((int)(hsv[0]/360*65535));
        satt = ((int)(hsv[1]*254));
        brii = ((int)(hsv[2]*254));
        hue.setText("HUE: " + String.valueOf(huee));
        sat.setText("SAT: " + String.valueOf(satt));
        bri.setText("BRI: " + String.valueOf(brii));

    }
}
