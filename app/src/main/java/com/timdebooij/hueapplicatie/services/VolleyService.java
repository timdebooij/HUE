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
import com.timdebooij.hueapplicatie.models.Bridge;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class VolleyService {
    public RequestQueue queue;
    public ApiListener listener;
    JSONParser parser;

    public VolleyService(Context context, ApiListener listener){
        this.queue = Volley.newRequestQueue(context);
        this.listener = listener;
        this.parser = new JSONParser();
    }

    public void logIn(Bridge bridge) throws JSONException {
        String url = "http://" + bridge.ipAddress + ":" + bridge.port + "/api/";
        final Bridge usedBridge = bridge;
        JSONObject json = new JSONObject();
        json.put("on", true);

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
}