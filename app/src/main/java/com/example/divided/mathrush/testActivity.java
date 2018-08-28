package com.example.divided.mathrush;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class testActivity extends AppCompatActivity {
    IndicatorView myIndicatorView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        String labels[] = new String[5];
        labels[0] = "Pierwszy";
        labels[1] = "Drugi";
        labels[2] = "trzeci";
        labels[3] = "czwarty";
        labels[4] = "piaty";

        myIndicatorView = findViewById(R.id.mTestView);
        myIndicatorView.setParameters(-1,3,labels);
        myIndicatorView.setCurrentPosition(-1);
    }
}
