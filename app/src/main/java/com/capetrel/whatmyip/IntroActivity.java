package com.capetrel.whatmyip;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.net.HttpURLConnection;
import java.net.URL;

public class IntroActivity extends AppCompatActivity {

    private Button mButtonIntroActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro);

        mButtonIntroActivity = (Button) findViewById(R.id.view_button_check_ip);
    }

    public void onClickActivityShow (View v){

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService (Context.CONNECTIVITY_SERVICE );
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            Intent intent = new Intent(IntroActivity.this, ShowActivity.class);
            startActivity(intent);

            Log.d( "lol", "--------->onclick internet" );

        } else {
            // display error
            Toast.makeText( IntroActivity.this , "Il n'y a pas de réseau", Toast.LENGTH_SHORT).show();
        }

    }

}
