package com.example.android_final.addFunction;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android_final.GetCategoryFromDB;
import com.example.android_final.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateSchedule extends AppCompatActivity {

    private EditText sch_name;
    private Spinner schedule_cate_spinner;
    private TimePicker timePicker;
    private TimePicker timePicker2;
    private DatePicker datePicker;
    private RadioButton sch_repeat_radio;
    private Button done;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        timePicker = findViewById(R.id.timePicker);
        timePicker2 = findViewById(R.id.timePicker2);
        timePicker.setIs24HourView(true);
        timePicker2.setIs24HourView(true);

        db = FirebaseFirestore.getInstance();
        sch_name = findViewById(R.id.sch_name);
        datePicker = findViewById(R.id.datePicker);
        sch_repeat_radio = findViewById(R.id.sch_repeat_radio);

        //category select, still need to wait to show all cate
        GetCategoryFromDB getC = new GetCategoryFromDB();
        ArrayList<String> cate_array = getC.getCategory();
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
        schedule_cate_spinner = findViewById(R.id.schedule_cate_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(CreateSchedule.this, android.R.layout.simple_list_item_1, cate_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schedule_cate_spinner.setAdapter(adapter);

        //submit
        done = findViewById(R.id.done);
        done.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (sch_name.getText().toString().isEmpty()) {
                    Toast.makeText(CreateSchedule.this, "Cannot create schedule with empty name", Toast.LENGTH_SHORT).show();
                    return;
                }

                Map<String, Object> schedule = new HashMap<>();
                schedule.put("Name", sch_name.getText().toString());
                schedule.put("Category", schedule_cate_spinner.getSelectedItem().toString());
                schedule.put("TimeStart", timePicker.getHour()+":"+timePicker.getMinute());
                schedule.put("TimeEnd", timePicker2.getHour()+":"+timePicker2.getMinute());
                schedule.put("Day", datePicker.getDayOfMonth()+"/"+datePicker.getMonth()+"/"+datePicker.getYear());
                if (sch_repeat_radio.isChecked()) {
                    schedule.put("Repeat", "Yes");
                } else {
                    schedule.put("Repeat", "No");
                }
                int minute = 0;
                int hour = 0;

                int m_Start = timePicker.getMinute();
                int h_Start = timePicker.getHour();
                int m_End = timePicker2.getMinute();
                int h_End = timePicker2.getHour();
                int a = m_End - m_Start;
                int b = h_End - h_Start;
                if (a>=0) {
                    minute = a;
                    if (b>=0) {
                        hour = b;
                    } else {
                        hour = 24-b;
                    }
                    schedule.put("Remain", hour+":"+minute);
                } else {
                    minute = 60-a*-1;
                    if (b>=0) {
                        hour = b-1;
                    } else {
                        hour = 24-b-1;
                    }
                    schedule.put("Remain", hour+":"+minute);
                }



                db.collection("schedules")
                        .add(schedule)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(@NonNull DocumentReference documentReference) {
                                Toast.makeText(CreateSchedule.this, "Schedule added successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(CreateSchedule.this, "Error, try again", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
    }
}