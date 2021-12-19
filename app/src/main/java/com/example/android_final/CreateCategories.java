package com.example.android_final;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CreateCategories extends AppCompatActivity {

    Toolbar toolbar;
    TextView cate_icon;
    //int color_posi, icon_posin;
    Button done_button;
    Button cancel_button;
    ProgressBar progressBar;
    List<String> color = Arrays.asList("Black", "Pin!", "Blue", "Red", "Brown");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_categories);

        //set support action bar
        toolbar = findViewById(R.id.create_category_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //category select
        GetCategoryFromDB getC = new GetCategoryFromDB();
        ArrayList<String> cate_array = getC.getCategory();

        Spinner spinner = findViewById(R.id.cate_cate_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateCategories.this, android.R.layout.simple_list_item_1, cate_array);
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

        //confirm or cancel operation
        progressBar = findViewById(R.id.category_progressbar);
        final String[] text = new String[1];
        final int[] icon = new int[1];
        EditText cate_des = findViewById(R.id.cate_des);
        done_button = findViewById(R.id.cate_done);
        done_button.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);
            Toast.makeText(view.getContext(), "Category added successfully", Toast.LENGTH_SHORT).show();
            onBackPressed();
        });

        cancel_button = findViewById(R.id.cate_cancel);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(v.getContext(), "Canceled Operation", Toast.LENGTH_SHORT).show();
                onBackPressed();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}