package com.romanport.arkwebmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Switch;

import com.android.volley.Response;
import com.romanport.arkwebmap.NetEntities.OkReply;
import com.romanport.arkwebmap.NetEntities.Servers.SetNotificationChannelsRequest;
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeServer;
import com.romanport.arkwebmap.R;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ArkServerNotificationsMenuActivity extends AppCompatActivity {

    String serverId;
    String[] initialChannels;

    Map<String, Switch> notificationSliders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ark_server_notifications_menu);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get data
        Intent intent = getIntent();
        initialChannels = intent.getStringArrayExtra("com.romanport.arkwebmap.SERVER_NOTIFICATIONS");
        serverId = intent.getStringExtra("com.romanport.arkwebmap.SERVER_ID");

        //Create map
        notificationSliders = new HashMap<String, Switch>();
        notificationSliders.put("BabyDino_FoodCritical", (Switch)findViewById(R.id.notificationToggleBabyDinoCritical));
        notificationSliders.put("BabyDino_FoodLow", (Switch)findViewById(R.id.notificationToggleBabyDinoLow));
        notificationSliders.put("BabyDino_FoodStarving", (Switch)findViewById(R.id.notificationToggleBabyDinoStarving));
        notificationSliders.put("Tribe_TribeDinoDeath", (Switch)findViewById(R.id.notificationToggleTribeDeath));
        notificationSliders.put("Tribe_TribeDinoTame", (Switch)findViewById(R.id.notificationToggleTribeTame));

        //Set switches
        for(int i = 0; i<initialChannels.length; i+=1) {
            //Check if we have this key
            String channel = initialChannels[i];
            if(notificationSliders.containsKey(channel)) {
                //Set switch
                notificationSliders.get(channel).setChecked(true);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Save settings. Convert settings back into a list.
        List<String> channels = new ArrayList<>();

        //Loop through all known channels.
        for(String key : notificationSliders.keySet()) {
            Switch s = notificationSliders.get(key);

            //If the switch is set to true, add it
            if(s.isChecked()) {
                channels.add(key);
            }
        }

        //Create payload
        SetNotificationChannelsRequest payload = new SetNotificationChannelsRequest();
        payload.id = serverId;
        payload.channels = channels;

        //Submit
        WebUser.SendAuthenticatedPostRequest(this, "https://ark.romanport.com/api/users/@me/servers/change_notifications", payload, new Response.Listener<Object>() {
            @Override
            public void onResponse(Object response) {
                //Submitted.
            }
        }, OkReply.class);

        //TODO: Update main activity of the change.
    }

}
