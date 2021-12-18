package com.example.android_final;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CreateCategories extends AppCompatActivity {

    TextView cate_icon;
    //int color_posi, icon_posi;
    Button cate_done;
    private ArrayList<String> cate_array;
    List<String> color = Arrays.asList("Black", "Pink!", "Blue", "Red", "Brown");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_categories);

        //category select
        GetCategoryFromDB getC = new GetCategoryFromDB();
        cate_array = getC.getCategory();

        Spinner spinner = findViewById(R.id.cate_cate_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateCategories.this, android.R.layout.simple_list_item_1,cate_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //color select
        Spinner cate_cate_spinner = findViewById(R.id.color_spinner);
        ArrayAdapter<CharSequence> c_adapter = ArrayAdapter.createFromResource(CreateCategories.this, R.array.color_array, android.R.layout.simple_list_item_1);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        cate_cate_spinner.setAdapter(c_adapter);

        //icon select
        Spinner icon_spinner = findViewById(R.id.icon_spinner);
        ImageSpinnerAdapter icon_adapter = new ImageSpinnerAdapter(this, new Integer[]{R.drawable.icon1, R.drawable.icon2, R.drawable.icon3});
        icon_spinner.setAdapter(icon_adapter);

        final String[] text = new String[1];
        final int[] icon = new int[1];
        EditText cate_des = findViewById(R.id.cate_des);
        cate_done = findViewById(R.id.cate_done);
        cate_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text[0] = spinner.getSelectedItem().toString();     //cate
                icon[0] = icon_spinner.getSelectedItemPosition();   //icon
                cate_des.setText(text[0]+"   "+icon[0]);
            }
        });
    }
}