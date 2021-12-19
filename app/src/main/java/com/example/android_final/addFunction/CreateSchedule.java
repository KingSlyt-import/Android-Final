package com.example.android_final.addFunction;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.example.android_final.GetCategoryFromDB;
import com.example.android_final.R;

import java.util.ArrayList;

public class CreateSchedule extends AppCompatActivity {

    TimePicker timePicker;
    private ArrayList<String> cate_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        timePicker = findViewById(R.id.timePicker);
        timePicker.setIs24HourView(false);

        GetCategoryFromDB getC = new GetCategoryFromDB();
        cate_array = getC.getCategory();
        Spinner spinner = findViewById(R.id.schedule_cate_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateSchedule.this, android.R.layout.simple_list_item_1,cate_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}