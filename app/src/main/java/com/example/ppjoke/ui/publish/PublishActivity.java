package com.example.ppjoke.ui.publish;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.libnavannotation.ActivityDestination;
import com.example.ppjoke.R;
import com.example.ppjoke.util.DataBus;


@ActivityDestination(pageUrl = "main/tabs/publish",asStarter = false)
public class PublishActivity extends AppCompatActivity {

    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);

        DataBus.INSTANCE.<String>with("stickyData").observerSticky(this,true, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                Toast.makeText(getApplicationContext(),"data from "+s,Toast.LENGTH_SHORT).show();
            }
        });

        btn = findViewById(R.id.btn);
        btn.setOnClickListener(v -> DataBus.INSTANCE.<String>with("stickyData").setStickyData("PublishActivity"));
    }
}