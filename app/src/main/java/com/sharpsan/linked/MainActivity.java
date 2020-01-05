package com.sharpsan.linked;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //new OpenGraphNetworking(this).execute("https://youtu.be/-6YIP2pXtzI");
    }
}
