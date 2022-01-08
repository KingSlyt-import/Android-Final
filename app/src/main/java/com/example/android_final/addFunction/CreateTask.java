package com.example.android_final.addFunction;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android_final.GetCategoryFromDB;
import com.example.android_final.Notify.NotificationReceiver;
import com.example.android_final.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateTask extends AppCompatActivity {

    private EditText tsk_name;
    private Spinner task_cate_spinner;
    private DatePicker datePicker2;
    private RadioGroup radioGroup;
    private RadioButton importance;
    private RadioButton tsk_complete;
    private EditText tsk_note;
    private Button done_task;
    FirebaseFirestore db;
    int a = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        tsk_name = findViewById(R.id.tsk_name);
        datePicker2 = findViewById(R.id.datePicker2);
        radioGroup = findViewById(R.id.tsk_imp);
        tsk_complete = findViewById(R.id.tsk_complete);
        tsk_note = findViewById(R.id.tsk_note);
        task_cate_spinner= findViewById(R.id.task_cate_spinner);

        //category select, still need to wait to show all cate
        ArrayList<String> cate_array = new ArrayList<String>();
        cate_array.add("Root");
        db = FirebaseFirestore.getInstance();
        db.collection("categories")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                cate_array.add(Objects.requireNonNull(document.get("name")).toString());
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
        task_cate_spinner = findViewById(R.id.task_cate_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateTask.this, android.R.layout.simple_list_item_1, cate_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        task_cate_spinner.setAdapter(adapter);

        done_task = findViewById(R.id.done_task);
        done_task.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (tsk_name.getText().toString().isEmpty()) {
                    Toast.makeText(CreateTask.this, "Cannot create task with empty name", Toast.LENGTH_SHORT).show();
                    return;
                }
                //check existed name but it doesn't work for some reason

//                db.collection("tasks")
//                        .whereEqualTo("Name", tsk_name.getText().toString())
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    a=1;
//                                    Toast.makeText(CreateTask.this, "Schedule needs a unique name", Toast.LENGTH_SHORT).show();
//                                    return;
//                                } else {
//
//                                }
//                            }
//                        });
//                Log.d(TAG, "avalue: "+a);
//                if (a!=0) {
//                    a=0;
//                    return;
//                }
                LocalDate localDate = LocalDate.now();
                int currday = localDate.getDayOfMonth();
                int currmonth = localDate.getMonthValue();
                int curryear = localDate.getYear();
                int pickday = datePicker2.getDayOfMonth();
                int pickmonth = datePicker2.getMonth()+1;
                int pickyear = datePicker2.getYear();
                String pickmonth2;
                if (pickmonth<10) {
                    pickmonth2 = "0"+pickmonth;
                } else {
                    pickmonth2 = ""+pickmonth;
                }

                if (pickyear < curryear) {
                    Toast.makeText(CreateTask.this, "Selected year must be past or same as current year", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (pickmonth < currmonth) {
                        Toast.makeText(CreateTask.this, "Selected month must be past or same as current month", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (pickday < currday) {
                            Toast.makeText(CreateTask.this, "Selected day must be past or same as current day", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                int selectedId = radioGroup.getCheckedRadioButtonId();
                importance = findViewById(selectedId);

                Map<String, Object> task = new HashMap<>();
                task.put("Name", tsk_name.getText().toString());
                task.put("Category", task_cate_spinner.getSelectedItem().toString());
                task.put("Day", pickday+"/"+pickmonth2+"/"+pickyear);
                task.put("Importance", importance.getText().toString());
                task.put("Note", tsk_note.getText().toString());
                task.put("Completed", tsk_complete.isChecked() ? "yes":"no");

                db.collection("tasks")
                        .add(task)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(@NonNull DocumentReference documentReference) {
                                Toast.makeText(CreateTask.this, "Task added successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateTask.this, "Error, try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });


    }
}