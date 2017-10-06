package de.ameyering.wgplaner.wgplaner.section.splashScreen;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import de.ameyering.wgplaner.wgplaner.R;
import de.ameyering.wgplaner.wgplaner.section.login.LoginActivity;
import de.ameyering.wgplaner.wgplaner.utils.Configuration;

/**
 * Created by D067867 on 06.10.2017.
 */

public class SplashScreen extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        loadConfig();
        getData();


        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadConfig(){
        Configuration.initConfig(this);
    }

    private void getData(){

    }
}
