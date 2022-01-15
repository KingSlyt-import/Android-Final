package com.example.android_final.addFunction;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android_final.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateSubTask extends AppCompatActivity {

    private EditText tsk_name;
    private Spinner task_cate_spinner;
    private DatePicker datePicker2;
    private RadioGroup radioGroup;
    private RadioButton firstRadio;
    private RadioButton importance;
    private ProgressBar progressBar;
    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);

        tsk_name = findViewById(R.id.tsk_name);
        datePicker2 = findViewById(R.id.datePicker2);
        radioGroup = findViewById(R.id.tsk_imp);
        task_cate_spinner= findViewById(R.id.task_cate_spinner);
        firstRadio = findViewById(R.id.radioButton3);
        firstRadio.setChecked(true);

        //get data from SubTaskActivity
        Intent intent = getIntent();
        String task_title = intent.getStringExtra("task_title");

        //set support action bar
        Toolbar toolbar = findViewById(R.id.create_task_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        //get current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //category select, still need to wait to show all cate
        ArrayList<String> cate_array = new ArrayList<>();
        cate_array.add("Root");
        db = FirebaseFirestore.getInstance();
        db.collection("categories")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                            cate_array.add(Objects.requireNonNull(document.get("name")).toString());
                        }
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> Log.d(TAG, "onFailure: UNSUCCESSFUL OPERATION"));
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateSubTask.this, android.R.layout.simple_list_item_1, cate_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        task_cate_spinner.setAdapter(adapter);

        progressBar = findViewById(R.id.create_task_progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        Button done_task = findViewById(R.id.task_done);
        done_task.setOnClickListener(view -> {
            progressBar.setVisibility(View.VISIBLE);

            if (tsk_name.getText().toString().isEmpty()) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(CreateSubTask.this, "Cannot create task with empty name", Toast.LENGTH_SHORT).show();
                return;
            }

            LocalDate localDate = LocalDate.now();
            int currDay = localDate.getDayOfMonth();
            int currMonth = localDate.getMonthValue();
            int currYear = localDate.getYear();
            int dayPicked = datePicker2.getDayOfMonth();
            int monthPicked = datePicker2.getMonth()+1;
            int yearPicked = datePicker2.getYear();
            String month;

            if (monthPicked<10) {
                month = "0" + monthPicked;
            } else {
                month = "" + monthPicked;
            }

            if (yearPicked < currYear) {
                progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(CreateSubTask.this, "Selected year must be past or same as current year", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (monthPicked < currMonth) {
                    progressBar.setVisibility(View.INVISIBLE);
                    Toast.makeText(CreateSubTask.this, "Selected month must be past or same as current month", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (dayPicked < currDay) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CreateSubTask.this, "Selected day must be past or same as current day", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            int selectedId = radioGroup.getCheckedRadioButtonId();
            importance = findViewById(selectedId);

            Map<String, Object> task = new HashMap<>();
            task.put("userId", firebaseUser.getUid());
            task.put("Father_task", task_title);
            task.put("Name", tsk_name.getText().toString());
            task.put("Category", task_cate_spinner.getSelectedItem().toString());
            task.put("Day", dayPicked+"/"+month+"/"+yearPicked);
            task.put("Importance", importance.getText().toString());

            db.collection("subtasks")
                    .add(task)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(CreateSubTask.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(CreateSubTask.this, "Error, try again", Toast.LENGTH_SHORT).show();
                    });
        });

        Button cancel_task = findViewById(R.id.task_cancel);
        cancel_task.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Canceled operation", Toast.LENGTH_SHORT).show();
            onBackPressed();
        });
    }
}
