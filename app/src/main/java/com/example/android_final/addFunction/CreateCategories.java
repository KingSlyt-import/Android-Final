package com.example.android_final.addFunction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android_final.GetCategoryFromDB;
import com.example.android_final.adapter.ImageSpinnerAdapter;
import com.example.android_final.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateCategories extends AppCompatActivity {

    FirebaseFirestore db;
    Toolbar toolbar;
    TextView cate_name;
    TextView cate_des;
    Button done_button;
    Button cancel_button;
    ProgressBar progressBar;

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

        //firestore config
        db = FirebaseFirestore.getInstance();
        cate_name = findViewById(R.id.cate_name);
        cate_des = findViewById(R.id.cate_des);

        //confirm or cancel operation
        progressBar = findViewById(R.id.category_progressbar);
        done_button = findViewById(R.id.cate_done);
        done_button.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            String name = cate_name.getText().toString();
            String description = cate_des.getText().toString();

            if (name.isEmpty()) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(view.getContext(), "Cannot save category with empty name", Toast.LENGTH_SHORT).show();
                return;
            }

            if (description.isEmpty()) {
                description = "null";
            }

            Map<String, Object> category = new HashMap<>();
            category.put("name", name);
            category.put("description", description);

            db.collection("categories").document()
                    .set(category)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(CreateCategories.this, "Category added successfully", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toast.makeText(CreateCategories.this, "Error, try again", Toast.LENGTH_SHORT).show();
                        }
                    }
            );
        });

        cancel_button = findViewById(R.id.cate_cancel);
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                Toast.makeText(v.getContext(), "Canceled operation", Toast.LENGTH_SHORT).show();
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