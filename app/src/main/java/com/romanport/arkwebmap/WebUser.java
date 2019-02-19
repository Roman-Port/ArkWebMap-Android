package com.romanport.arkwebmap;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
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
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeReply;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class WebUser {

    public static UsersMeReply me;

    public static void SendAuthenticatedGetRequest(final Activity c, String url, final Response.Listener<Object> callback, final Type returnType) {
        SendAuthenticatedRequest(c, url, callback, new byte[0], Request.Method.GET, returnType);
    }

    public static void SendAuthenticatedPostRequest(final Activity c, String url, Object payload, final Response.Listener<Object> callback, final Type returnType) {
        //Serialize
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String payloadString = gson.toJson(payload);

        SendAuthenticatedRequest(c, url, callback, payloadString.getBytes(), Request.Method.POST, returnType);
    }

    public static void SendAuthenticatedRequest(final Activity c, String url, final Response.Listener<Object> callback, final byte[] body, final int method, final Type returnType) {
        Log("Entering SendAuthenticatedRequest with GET to "+url);

        //Send request
        RequestQueue queue = Volley.newRequestQueue(c);
        StringRequest stringRequest = new StringRequest(method, url,
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
                if(error.networkResponse == null) {
                    //No server connection.
                    ReturnToLauncherWithMessageId(c, R.string.error_net_connect_title, R.string.error_net_connect_sub);
                }
                else if(error.networkResponse.statusCode == 500) {
                    //Convert data to string
                    String response = new String(error.networkResponse.data);
                    try {
                        //Parse incoming JSON
                        JSONObject jObject = new JSONObject(response);
                        Integer errorCode = jObject.getInt("error_code");
                        if (errorCode == 5) {
                            //Not authenticated. Take them to the signin page.
                            Log("User was not authenticated. Pushing to signin page.");
                            OpenLoginPage(c);
                        } else {
                            Log("Unknown error code '" + errorCode.toString() + "'. Returning null.");
                            ReturnToHubWithMessageId(c, R.string.error_net_server_error_title, "Got "+jObject.getString("screen_error"));

                        }
                    } catch (Exception ex) {
                        //More serious problem.
                        ReturnToHubWithMessageId(c, R.string.error_net_unexpected_title, R.string.error_net_unexpected_sub);
                    }
                } else if (error.networkResponse.statusCode == 521) {
                    //This is a error communicating with sub servers.
                    ReturnToHubWithMessageId(c, R.string.error_net_521_title, R.string.error_net_521_sub);
                } else if (error.networkResponse.statusCode == 503) {
                    //This is a error communicating with master server.
                    ReturnToLauncherWithMessageId(c, R.string.error_net_503_title, R.string.error_net_503_sub);
                } else {
                    //Unknown
                    ReturnToHubWithMessageId(c, R.string.error_net_status_title, R.string.error_net_status_sub);
                }
            }


        }) {
        @Override
        public byte[] getBody() throws AuthFailureError {
            return body;
        }

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

    private static void OpenLoginPage(Activity c) {
        Intent intent = new Intent(c, SteamLoginActivity.class);
        c.startActivity(intent);
        c.finish();
    }

    private static void ReturnToHubWithMessageId(final Activity context, int titleMessageId, int subMessageId) {
        ReturnToHubWithMessageId(context, titleMessageId, context.getString(subMessageId));
    }

    private static void ReturnToHubWithMessageId(final Activity context, int titleMessageId, String subMessage) {
        //Show full dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.error_net_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ReturnToHub(context);
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                ReturnToHub(context);
            }
        });
        builder.setTitle(titleMessageId);
        builder.setMessage(subMessage);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private static void ReturnToLauncherWithMessageId(final Activity context, int titleMessageId, int subMessageId) {
        //Show full dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setPositiveButton(R.string.error_net_btn, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                context.finish();
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            public void onCancel(DialogInterface dialog) {
                context.finish();
            }
        });
        builder.setTitle(titleMessageId);
        builder.setMessage(subMessageId);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private static void ReturnToHub(final Activity context) {
        Intent i = new Intent(context, ActivityHub.class);
        context.startActivity(i);
        context.finish();
    }
}


