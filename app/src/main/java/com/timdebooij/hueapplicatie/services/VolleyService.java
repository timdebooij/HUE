package com.timdebooij.hueapplicatie.services;
import android.app.DownloadManager;
import android.app.VoiceInteractor;
import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.timdebooij.hueapplicatie.MainActivity;
import com.timdebooij.hueapplicatie.models.Bridge;
import com.timdebooij.hueapplicatie.models.LightBulb;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class VolleyService {
    public RequestQueue queue;
    public ApiListener listener;
    private JSONParser parser;

    public VolleyService(Context context, ApiListener listener){
        this.queue = Volley.newRequestQueue(context);
        this.listener = listener;
        this.parser = new JSONParser();
    }

    public void logIn(Bridge bridge) throws JSONException {
        String url = "http://" + bridge.ipAddress + ":" + bridge.port + "/api/";
        final Bridge usedBridge = bridge;
        JSONObject json = new JSONObject();
        json.put("devicetype", "HUE app");

        CustomJsonArrayRequest request = new CustomJsonArrayRequest(Request.Method.POST, url, json, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject jsonObject = response.getJSONObject(0);
                    if(jsonObject.has("success")) {
                        JSONObject success = jsonObject.getJSONObject("success");
                        String token = success.getString("username");
                        usedBridge.token = token;
                        listener.usernameReceived(usedBridge);
                    }
                    else{
                        listener.onError("No connection possible with bridge, be sure to press the button");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("info", error.toString());
            }
        });

        queue.add(request);

    }

    public void getLightsInBridge(Bridge bridge){
        String url = "http://" + bridge.ipAddress + ":" + bridge.port + "/api/" + bridge.token;
        Log.i("info", url);
        final Bridge usedBridge = bridge;
        usedBridge.lightBulbs = new ArrayList<>();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject lights = response.getJSONObject("lights");
                    Log.i("info", lights.toString());
                    List<String> lightKeys = new ArrayList<>();
                    Iterator<String> keys = lights.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        lightKeys.add(key);
                    }
                    for(String s : lightKeys){
                        JSONObject bulb = lights.getJSONObject(s);
                        String id = s;
                        String name = bulb.getString("name");
                        JSONObject state = bulb.getJSONObject("state");
                        boolean on = state.getBoolean("on");
                        int hue = state.getInt("hue");
                        int sat = state.getInt("sat");
                        int bri = state.getInt("bri");
                        Log.i("info", "bulbs in detail: " + usedBridge.lightBulbs.size());
                        usedBridge.lightBulbs.add(new LightBulb(id, name, on, hue, sat, bri));
                    }
                    listener.onLightBulbs(usedBridge);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });


        queue.add(request);
    }

    public void setLight(Bridge bridge, String lightId, int hueValue, int satValue, int briValue) throws JSONException {
        String url = "http://" + bridge.ipAddress + ":" + bridge.port + "/api/" + bridge.token + "/lights/" + lightId + "/state";

        JSONObject order = new JSONObject();
        order.put("hue", hueValue);
        order.put("sat", satValue);
        order.put("bri", briValue);
        CustomJsonArrayRequest request = new CustomJsonArrayRequest(Request.Method.PUT, url, order, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject first = response.getJSONObject(0);
                    JSONObject success = first.getJSONObject("success");
                    Log.i("info", success.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }

    public void switchLightOnOff(Bridge bridge, String lightID, boolean switchstate) throws JSONException {
        String url = "http://" + bridge.ipAddress + ":" + bridge.port + "/api/" + bridge.token + "/lights/" + lightID + "/state/";
        JSONObject object = new JSONObject();

        object.put("on",switchstate);

        CustomJsonArrayRequest request = new CustomJsonArrayRequest(Request.Method.PUT, url, object, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    JSONObject first = response.getJSONObject(0);
                    JSONObject success = first.getJSONObject("success");
                    Log.i("info", success.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        queue.add(request);
    }
}