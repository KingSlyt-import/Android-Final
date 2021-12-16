package com.example.android_final;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TimePicker;

public class CreateSchedule extends AppCompatActivity {

    TimePicker timePicker;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(false);
    }
}