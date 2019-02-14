package com.romanport.arkwebmap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.Xml;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class WebUser {

    public static void SendAuthenticatedRequest(final AppCompatActivity c, String url, final Response.Listener<Object> callback, final Type returnType) {
        Log("Entering SendAuthenticatedRequest with GET to "+url);

        //Send request
        RequestQueue queue = Volley.newRequestQueue(c);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Decode this as JSON

                        try {
                            GsonBuilder builder = new GsonBuilder();
                            Gson gson = builder.create();
                            Object o = gson.fromJson(response, returnType);
                            callback.onResponse(o);
                        } catch (Exception ex) {
                            Log("Failed to deserialize JSON. Returning null.");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //There was an error. If this is a standard server error, (500) we should parse the error.
                if(error.networkResponse.statusCode == 500) {
                    //Convert data to string
                    String response = new String(error.networkResponse.data);
                    try {
                        //Parse incoming JSON
                        JSONObject jObject = new JSONObject(response);
                        Integer errorCode = jObject.getInt("error_code");
                        if(errorCode == 5) {
                            //Not authenticated. Take them to the signin page.
                            Log("User was not authenticated. Pushing to signin page.");
                            OpenLoginPage(c);
                        } else {
                            Log("Unknown error code '"+errorCode.toString()+"'. Returning null.");
                        }
                    } catch(Exception ex) {
                        //More serious problem.
                        Log("Got 500 error from server; failed to parse as JSON.");
                    }
                } else {
                    Log("Got unexpected error from server; Returning null...");
                }
            }


        }) {
        @Override
        public Map<String, String> getHeaders() throws AuthFailureError {
            //Set auth headers. Grab from persist storage.
            String token = c.getSharedPreferences("com.romanport.arkwebmap.WEB", Context.MODE_PRIVATE).getString("com.romanport.arkwebmap.ACCESS_TOKEN", "");

            //Run
            Map<String, String>  params = new HashMap<String, String>();
            params.put("Authorization", "Bearer "+token);

            return params;
        }
        };

        queue.add(stringRequest);
    }

    private static void Log(String message) {
        Log.println(Log.DEBUG, "ARK-NET",message);
    }

    private static void OpenLoginPage(AppCompatActivity c) {
        Intent intent = new Intent(c, SteamLoginActivity.class);
        c.startActivity(intent);
        c.finish();
    }
}


