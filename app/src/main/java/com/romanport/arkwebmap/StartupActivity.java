package com.romanport.arkwebmap;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.romanport.arkwebmap.NetEntities.OkReply;
import com.romanport.arkwebmap.NetEntities.PostNotificationTokenPayload;
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeReply;
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeServer;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        final StartupActivity c = this;

        //Authenticate user
        WebUser.SendAuthenticatedGetRequest(this, "https://ark.romanport.com/api/users/@me/?hideInvalid=false&pingAllServers=true", new Response.Listener<Object>() {
            @Override
            public void onResponse(Object response) {
                UsersMeReply reply = (UsersMeReply)response;

                //Set global user
                WebUser.me = reply;

                //Send cloud notification token
                SubmitNewCloudMessagingToken();

                //If there was a last server, jump to it rather than showing the server list.
                String lastServerId = c.getSharedPreferences("com.romanport.arkwebmap.WEB", Context.MODE_PRIVATE).getString("com.romanport.arkwebmap.LATEST_SERVER_ID", "");


                //Loop through the servers and try to find it.
                for(UsersMeServer server : reply.servers) {
                    if(server.id.equals(lastServerId)) {
                        //Found the server. Stop default. We'll need to check if it online first though.
                        FoundLastServer(server, c, reply);
                        return;
                    }
                }

                //Show default view
                ShowDefaultDisplay(reply);

            }
        }, UsersMeReply.class);
    }

    private void FoundLastServer(final UsersMeServer s, final StartupActivity context, final UsersMeReply me) {
        s.CheckIfOnline(context, new Response.Listener<Boolean>() {
            @Override
            public void onResponse(Boolean online) {
                if(online) {
                    //Load this server.
                    MainActivity.StartActivityWithServer(context, s);
                    context.finish();
                } else {
                    //Do not load this server. Show a toast and show the servers menu.
                    Toast.makeText(context, getString(R.string.error_resume_ping_offline), Toast.LENGTH_LONG).show();
                    ShowDefaultDisplay(me);
                }
            }
        });
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

    public void ShowDefaultDisplay(final UsersMeReply me) {
        //Shows the default menu if no server is selected. If there are no servers, a different prompt is shown.
        if(me.servers.length == 0) {
            //Show the no servers menu.
            Intent intent = new Intent(this, NoServersActivity.class);
            this.startActivity(intent);
            this.finish();
        } else {
            //Show hub.
            ShowHub(me);
        }
    }

    public void ShowHub(final UsersMeReply me) {
        //Show the hub activity.
        Intent intent = new Intent(this, ActivityHub.class);
        this.startActivity(intent);
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        this.finish();
    }
}
