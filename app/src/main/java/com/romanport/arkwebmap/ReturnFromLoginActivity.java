package com.romanport.arkwebmap;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.romanport.arkwebmap.NetEntities.AuthReply;

public class ReturnFromLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return_from_login);

        //Read request token
        Intent intent = getIntent();
        String action = intent.getAction();
        Uri data = intent.getData();
        String preflightToken = data.getPath().replace("/","");

        //Now, request access to the preflight token to get our real token.
        final ReturnFromLoginActivity context = this;
        WebUser.SendAuthenticatedGetRequest(this, "https://ark.romanport.com/api/auth/validate_preflight_token?id="+preflightToken, new Response.Listener<Object>() {
            @Override
            public void onResponse(Object response) {
                Log.println(Log.DEBUG, "ARK-NET", "Got user data when finishing login. Saving...");
                AuthReply r = (AuthReply)response;

                //Store access token
                SharedPreferences sharedPref = getSharedPreferences("com.romanport.arkwebmap.WEB", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("com.romanport.arkwebmap.ACCESS_TOKEN", r.token);
                editor.commit();

                //Jump to the main view.
                Intent intent = new Intent(context, MainActivity.class);
                context.startActivity(intent);

                //Show welcome message
                Toast toast = Toast.makeText(getApplicationContext(), "Hi, "+r.user.screen_name+"!", Toast.LENGTH_LONG);
                toast.show();

                //Finish this activity
                context.finish();
            }
        }, AuthReply.class);
    }
}
