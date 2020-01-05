package com.sharpsan.linked;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class ShareActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LinkedService.startActionFetchAndCopy(getApplicationContext(), getIntent());
        finish();
    }
}
