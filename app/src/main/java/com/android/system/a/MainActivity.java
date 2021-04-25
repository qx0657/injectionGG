package com.android.system.a;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.android.system.a.ceshi.CeshiActivity;
import com.android.system.a.jiemi.JieMiActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(v -> new b().g(this));
        findViewById(R.id.button2).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, JieMiActivity.class)));
        findViewById(R.id.button3).setOnClickListener(v -> startActivity(new Intent(MainActivity.this, CeshiActivity.class)));
    }
}