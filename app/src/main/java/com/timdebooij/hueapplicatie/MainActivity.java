package com.timdebooij.hueapplicatie;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.services.ApiListener;
import com.timdebooij.hueapplicatie.services.VolleyService;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity implements ApiListener {

    public Bridge bridge;
    public VolleyService service;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        bridge = new Bridge("192.168.178.18", "emulator", "80");
        service = new VolleyService(this.getApplicationContext(), this);
        service.queue.start();
    }
    public void connect(View view) throws JSONException {
        service.logIn(bridge);
    }

    @Override
    public void onResponse(String response) {

    }

    @Override
    public void usernameReceived(Bridge bridgeWithToken) {
        Log.i("info", "Token is: " + bridgeWithToken.token);
    }

    @Override
    public void onError(String error) {
        Log.i("info", error);
    }
}
