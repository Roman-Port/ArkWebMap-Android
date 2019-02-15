package com.romanport.arkwebmap;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.romanport.arkwebmap.NetEntities.AuthReply;
import com.romanport.arkwebmap.NetEntities.OkReply;
import com.romanport.arkwebmap.NetEntities.PostNotificationTokenPayload;
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeReply;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public UsersMeReply user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Configure WebView
        WebView mapWebview = GetMapWebview();
        mapWebview.getSettings().setJavaScriptEnabled(true);
        mapWebview.getSettings().setLoadWithOverviewMode(true);
        mapWebview.getSettings().setUseWideViewPort(true);
        mapWebview.getSettings();
        mapWebview.setBackground(getDrawable(R.color.colorPrimary));

        //Authenticate user
        WebUser.SendAuthenticatedGetRequest(this, "https://ark.romanport.com/api/users/@me/?hideInvalid=true", new Response.Listener<Object>() {
            @Override
            public void onResponse(Object response) {
                UsersMeReply reply = (UsersMeReply)response;
                user = reply;

                //Update cloud messaging (notifications) token
                SubmitNewCloudMessagingToken();

                //Add servers
                OnGotUpdatedServers(reply);
            }
        }, UsersMeReply.class);
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        /*if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void OnGotUpdatedServers(UsersMeReply reply) {
        //Add servers to list.
        ArkServerListEntryAdapter adapter=new ArkServerListEntryAdapter(this, user.servers);
        ListView list =(ListView)findViewById(R.id.server_list);
        list.setAdapter(adapter);
    }

    public void SubmitNewCloudMessagingToken() {
        //Request
        final AppCompatActivity c = this;
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            //Todo: Log
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();

                        //Create payload
                        PostNotificationTokenPayload payload = new PostNotificationTokenPayload();
                        payload.token = token;

                        //Submit to master server
                        WebUser.SendAuthenticatedPostRequest(c, "https://ark.romanport.com/api/users/@me/notification_token", payload, new Response.Listener<Object>() {
                            @Override
                            public void onResponse(Object response) {
                                //Submitted.
                            }
                        }, OkReply.class);
                    }
                });
    }

    //Functions for getting things
    public WebView GetMapWebview() {
        return (WebView)findViewById(R.id.map_webview);
    }
}
