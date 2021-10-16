package com.example.ppjoke.ui.publish;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.libnavannotation.ActivityDestination;
import com.example.ppjoke.R;

@ActivityDestination(pageUrl = "main/tabs/publish",asStarter = false)
public class PublishActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
    }
}