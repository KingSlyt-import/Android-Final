package com.example.android_final.addFunction;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.util.Log;
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
import com.example.android_final.data.Category;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        //set support action bar
        toolbar = findViewById(R.id.create_note_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //firestore config
        db = FirebaseFirestore.getInstance();
        note_title = findViewById(R.id.note_title);
        note_body = findViewById(R.id.note_body);

        //category select
        ArrayList<String> categories = new ArrayList<>();
        categories.add("Root");
        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                categories.add(Objects.requireNonNull(document.get("name")).toString());
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "onFailure: UNSUCCESSFUL OPERATION");
                    }
                });

        spinner = findViewById(R.id.note_cate_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateNote.this, android.R.layout.simple_list_item_1, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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
                    Toast.makeText(CreateNote.this, "Cannot create note with empty name or body", Toast.LENGTH_SHORT).show();
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