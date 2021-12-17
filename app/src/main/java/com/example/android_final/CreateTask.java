package com.example.android_final;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.ArrayList;

public class CreateTask extends AppCompatActivity {

    private ArrayList<String> cate_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        GetCategoryFromDB getC = new GetCategoryFromDB();
        cate_array = getC.getCategory();
        Spinner spinner = findViewById(R.id.task_cate_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateTask.this, android.R.layout.simple_list_item_1,cate_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
}