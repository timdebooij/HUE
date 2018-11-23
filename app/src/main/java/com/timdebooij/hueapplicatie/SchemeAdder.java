package com.timdebooij.hueapplicatie;

import android.arch.persistence.room.Room;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.timdebooij.hueapplicatie.database.DatabaseColorScheme;
import com.timdebooij.hueapplicatie.models.ColorScheme;

import java.util.ArrayList;
import java.util.List;

public class SchemeAdder extends AppCompatActivity {
    public TextView name;
    public TextView hue;
    public TextView sat;
    public TextView bri;
    private static final String DATABASE_NAME = "ColorSchemes_db";
    private DatabaseColorScheme database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scheme_adder);
        name =findViewById(R.id.nameValue);
        hue=findViewById(R.id.hueValue);
        sat=findViewById(R.id.satValue);
        bri=findViewById(R.id.briValue);
        database = Room.databaseBuilder(getApplicationContext(), DatabaseColorScheme.class, DATABASE_NAME).fallbackToDestructiveMigration().build();
    }

    public void addScheme(View view){
        final ColorScheme scheme = new ColorScheme();
        scheme.setSchemeName(name.getText().toString());
        scheme.setHue(Integer.parseInt(hue.getText().toString()));
        scheme.setSat(Integer.parseInt(sat.getText().toString()));
        scheme.setBri(Integer.parseInt(bri.getText().toString()));
        BridgeDetailActivity.colorSchemeNames.add(scheme.getSchemeName());
        BridgeDetailActivity.colorSchemes.put(scheme.getSchemeName(), scheme);
        BridgeDetailActivity.spinnerAdapter.notifyDataSetChanged();
        new Thread(new Runnable() {
            @Override
            public void run() {
                database.daoAccess().insertColorScheme(scheme);
            }
        }) .start();
    }
}
