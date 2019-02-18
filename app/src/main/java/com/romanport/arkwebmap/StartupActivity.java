package com.romanport.arkwebmap;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.Response;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.romanport.arkwebmap.NetEntities.OkReply;
import com.romanport.arkwebmap.NetEntities.PostNotificationTokenPayload;
import com.romanport.arkwebmap.NetEntities.UsersMe.UsersMeReply;

public class StartupActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_startup);

        //Authenticate user
        WebUser.SendAuthenticatedGetRequest(this, "https://ark.romanport.com/api/users/@me/?hideInvalid=false&pingAllServers=true", new Response.Listener<Object>() {
            @Override
            public void onResponse(Object response) {
                UsersMeReply reply = (UsersMeReply)response;

                //Set global user
                WebUser.me = reply;

                //Send cloud notification token
                SubmitNewCloudMessagingToken();

                //Show default view
                ShowDefaultDisplay(reply);

            }
        }, UsersMeReply.class);
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
        this.finish();
    }
}
