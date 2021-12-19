package com.example.android_final.addFunction;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android_final.GetCategoryFromDB;
import com.example.android_final.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateNote extends AppCompatActivity {
    Toolbar toolbar;
    Spinner spinner;
    TextView note_title;
    TextView note_body;
    Button addBtn;
    Button cancelBtn;
    ProgressBar progressBar;

    FirebaseFirestore db;

    private ArrayList<String> cate_array;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        //set support action bar
        toolbar = findViewById(R.id.create_note_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //category select
        GetCategoryFromDB getC = new GetCategoryFromDB();
        cate_array = getC.getCategory();
        spinner = findViewById(R.id.note_cate_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateNote.this, android.R.layout.simple_list_item_1,cate_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        //firestore config
        db = FirebaseFirestore.getInstance();
        note_title = findViewById(R.id.note_title);
        note_body = findViewById(R.id.note_body);

        //confirm or cancel operation
        progressBar = findViewById(R.id.note_progressbar);

        addBtn = findViewById(R.id.note_done);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);

                String title = note_title.getText().toString();
                String body = note_body.getText().toString();

                if (title.isEmpty() || body.isEmpty()) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(CreateNote.this, "Cannot save category with empty name", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> note = new HashMap<>();
                note.put("title", title);
                note.put("body", body);

                db.collection("notes").document()
                        .set(note)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(CreateNote.this, "Note added successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toast.makeText(CreateNote.this, "Error, try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        cancelBtn = findViewById(R.id.note_cancel);
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(CreateNote.this, "Canceled operation", Toast.LENGTH_SHORT).show();
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