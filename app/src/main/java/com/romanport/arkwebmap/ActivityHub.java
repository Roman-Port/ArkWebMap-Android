package com.romanport.arkwebmap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.romanport.arkwebmap.NetEntities.Servers.PingReply;
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeServer;
import com.romanport.arkwebmap.Parts.Hub.ArkHubServerListAdapter;

public class ActivityHub extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hub);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        UpdateServerList();
    }

    public void UpdateServerList() {
        ListView list = findViewById(R.id.hub_server_list);
        ArkHubServerListAdapter adapter = new ArkHubServerListAdapter(this, WebUser.me.servers);
        list.setAdapter(adapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                OnChooseServer(WebUser.me.servers[position]);
            }
        });
    }

    public void OnChooseServer(final UsersMeServer server) {
        //Ping check server
        final ActivityHub c = this;
        server.CheckIfOnline(this, new Response.Listener<Boolean>() {
            @Override
            public void onResponse(Boolean isOnline) {
                //If the server is offline, stop
                if(!isOnline) {
                    //Show error toast
                    Toast.makeText(c, c.getString(R.string.error_ping_offline), Toast.LENGTH_LONG).show();
                } else {
                    //Load and dismiss hub
                    MainActivity.StartActivityWithServer(c, server);
                    finish();
                }
            }
        });
    }
}
