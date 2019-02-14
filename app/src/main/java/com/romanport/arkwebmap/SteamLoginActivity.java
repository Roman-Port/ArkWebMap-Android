package com.romanport.arkwebmap;

import android.content.Intent;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class SteamLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_steam_login);

        //Set onClick for button
        ImageButton fab = (ImageButton) findViewById(R.id.loginWithSteamBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Jump to web browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://ark.romanport.com/api/auth/steam_auth/?mode=AndroidClient"));
                startActivity(browserIntent);
                finish();
            }
        });
    }
}
