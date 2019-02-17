package com.romanport.arkwebmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.romanport.arkwebmap.Entities.MapStartupCommand;
import com.romanport.arkwebmap.NetEntities.Dinos.ArkDinosReply;
import com.romanport.arkwebmap.NetEntities.OkReply;
import com.romanport.arkwebmap.NetEntities.PostNotificationTokenPayload;
import com.romanport.arkwebmap.NetEntities.Servers.ArkServerCreateSession;
import com.romanport.arkwebmap.NetEntities.Servers.PingReply;
import com.romanport.arkwebmap.NetEntities.Servers.Tribes.ArkTribe;
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeReply;
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeServer;
import android.support.v4.app.Fragment;

import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentServerMapView.OnFragmentInteractionListener, DinoStatsDialogFragment.Listener, FragmentServerSearchInventoriesView.FragmentServerSearchInventoriesViewInterface {

    public Fragment activeTabFragment;

    public static void StartActivityWithServer(Context context, final UsersMeServer s) {
        //Start the activity
        Intent mIntent = new Intent(context, MainActivity.class);
        mIntent.putExtra("com.romanport.arkwebmap.STARTUP_SERVER", s);
        context.startActivity(mIntent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_container);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Ensure we have a user
        if(WebUser.me == null) {
            throw new RuntimeException("User was null in WebUser.me. You should not have been able to get this far. Exiting...");
        }

        //Get the requested initial server
        currentServer = (UsersMeServer)getIntent().getSerializableExtra("com.romanport.arkwebmap.STARTUP_SERVER");

        //Set up UI
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Add sidebar servers
        OnGotUpdatedServers(WebUser.me);

        //Create the Fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        FragmentNoActiveServerDefault fragment = new FragmentNoActiveServerDefault();
        activeTabFragment = fragment;
        fragmentTransaction.add(R.id.main_fragment_container, fragment);
        fragmentTransaction.commit();

        //Start server
        OnOpenServer(currentServer, true);
    }

    public void SwitchMainFragment(Fragment f) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(activeTabFragment); //Remove exiting
        fragmentTransaction.add(R.id.main_fragment_container, f); //Add new
        activeTabFragment = f; //Set the current fragment.
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig){
        super.onConfigurationChanged(newConfig);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //Handle button clicks on main menu
        int id = item.getItemId();

        if (id == R.id.server_notifications_menu_btn) {
            //Open the activity for the settings page.
            Intent intent = new Intent(this, ArkServerNotificationsMenuActivity.class);
            intent.putExtra("com.romanport.arkwebmap.SERVER_ID", currentServerId);
            intent.putExtra("com.romanport.arkwebmap.SERVER_NOTIFICATIONS", currentServer.enabled_notifications);
            startActivity(intent);
        }
        if(id == R.id.show_map) {
            //Show fragment for map
            ShowMapFragment();
        }
        if(id == R.id.search_inventories) {
            //Open the fragment for this.
            SwitchMainFragment(new FragmentServerSearchInventoriesView());
        }

        //Close drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void OnGotUpdatedServers(UsersMeReply reply) {
        //Add servers to list.
        ArkServerListEntryAdapter adapter=new ArkServerListEntryAdapter(this, WebUser.me.servers);
        ListView list =(ListView)findViewById(R.id.server_list);
        list.setAdapter(adapter);

        //Attach listener
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Set server
                OnOpenServer(WebUser.me.servers[position], false);

                //Close drawer
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
    }



    public void ShowFailure(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    //Servers
    public UsersMeServer currentServer;
    public String currentServerId;
    public ArkServerCreateSession currentSession;
    public ArkTribe currentTribe;

    public void OnOpenServer(final UsersMeServer requestServer, final Boolean force) {
        //Ping server to make sure it is ok
        final AppCompatActivity c = this;
        WebUser.SendAuthenticatedGetRequest(c, requestServer.endpoint_ping, new Response.Listener<Object>() {
            @Override
            public void onResponse(Object objPingReply) {
                //If the server is offline, stop
                PingReply pingReply = (PingReply)objPingReply;
                if(!pingReply.online && !force) {
                    //Show error toast
                    Toast.makeText(c, c.getString(R.string.error_ping_offline), Toast.LENGTH_LONG).show();
                } else {
                    //Continue to load server
                    currentServer = requestServer;
                    //Create a session to get new URLs
                    WebUser.SendAuthenticatedGetRequest(c, requestServer.endpoint_createsession, new Response.Listener<Object>() {
                        @Override
                        public void onResponse(Object response) {
                            currentSession = (ArkServerCreateSession)response;

                            //Now, request the Ark tribe.
                            WebUser.SendAuthenticatedGetRequest(c, currentSession.endpoint_tribes, new Response.Listener<Object>() {
                                @Override
                                public void onResponse(Object tribe_response) {
                                    currentTribe = (ArkTribe)tribe_response;

                                    OnGotNewMapData();
                                }
                            }, ArkTribe.class);
                        }
                    }, ArkServerCreateSession.class);
                }
            }
        }, PingReply.class);


    }

    public void ShowMapFragment() {
        //Create a command to send to the map.
        MapStartupCommand mapStartup = new MapStartupCommand();
        mapStartup.mapUrl = currentSession.endpoint_game_map;
        mapStartup.dinos = currentTribe.dinos;
        UpdateMap(mapStartup);
    }

    public void OnGotNewMapData() {
        //Update UI elements
        ((TextView)findViewById(R.id.serverMenuName)).setText(currentServer.display_name);

        //Show map
        ShowMapFragment();

    }

    public void UpdateMap(MapStartupCommand mapStartup) {
        //Serialize JSON and send to the map.
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        String payloadString = gson.toJson(mapStartup);

        //Grab map and load
        try {
            FragmentServerMapView f = new FragmentServerMapView();
            f.mWebViewUrl = "https://ark.romanport.com/standalone_map/#"+ URLEncoder.encode(payloadString, "UTF-8");
            SwitchMainFragment(f);
        } catch (Exception ex) {
            ShowFailure("Failed to load map.");
        }
    }

    @Override
    public void onDinoClick(String url) {
        //Called when a dinosaur is clicked on the map and we need to show the modal.
        //Make a request to download Dino data
        WebUser.SendAuthenticatedGetRequest(this, url, new Response.Listener<Object>() {
            @Override
            public void onResponse(Object response) {
                ArkDinosReply data = (ArkDinosReply)response;
                DinoStatsDialogFragment.newInstance(data).show(getSupportFragmentManager(), "dialog");

            }
        }, ArkDinosReply.class);

    }

    @Override
    public void onDinoModalButtonClicked(int i) {

    }


}
