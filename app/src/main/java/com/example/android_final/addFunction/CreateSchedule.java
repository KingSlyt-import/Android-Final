package com.example.android_final.addFunction;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.android_final.Notify.NotificationReceiver;
import com.example.android_final.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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

public class CreateSchedule extends AppCompatActivity {

    private EditText sch_name;
    private Spinner schedule_cate_spinner;
    private TimePicker timePicker;
    private TimePicker timePicker2;
    private DatePicker datePicker;
    private RadioButton sch_repeat_radio;
    FirebaseFirestore db;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        //set support action bar
        Toolbar toolbar = findViewById(R.id.create_schedule_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        createNotificationChannel();

        timePicker = findViewById(R.id.timePicker);
        timePicker2 = findViewById(R.id.timePicker2);
        timePicker.setIs24HourView(true);
        timePicker2.setIs24HourView(true);

        db = FirebaseFirestore.getInstance();
        sch_name = findViewById(R.id.sch_name);
        datePicker = findViewById(R.id.datePicker);
        sch_repeat_radio = findViewById(R.id.sch_repeat_radio);

        //category select
        ArrayList<String> cate_array  = new ArrayList<>();
        cate_array.add("Root");
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
        schedule_cate_spinner = findViewById(R.id.schedule_cate_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateSchedule.this, android.R.layout.simple_list_item_1, cate_array);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        schedule_cate_spinner.setAdapter(adapter);

        //submit
        Button done = findViewById(R.id.schedule_done);
        done.setOnClickListener(view -> {
            //validate
            if (sch_name.getText().toString().isEmpty()) {
                Toast.makeText(CreateSchedule.this, "Cannot create schedule with empty name", Toast.LENGTH_SHORT).show();
                return;
            }

            LocalDate localDate = LocalDate.now();
            int currDay = localDate.getDayOfMonth();
            int currMonth = localDate.getMonthValue();
            int currYear = localDate.getYear();

            int dayPicked = datePicker.getDayOfMonth();
            int monthPicked = datePicker.getMonth()+1;
            int pickyear = datePicker.getYear();
            String pickmonth2;
            if (monthPicked<10) {
                pickmonth2 = "0"+monthPicked;
            } else {
                pickmonth2 = ""+monthPicked;
            }

            if (pickyear < currYear) {
                Toast.makeText(CreateSchedule.this, "Selected year must be past or same as current year", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (monthPicked < currMonth) {
                    Toast.makeText(CreateSchedule.this, "Selected month must be past or same as current month", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (dayPicked < currDay) {
                        Toast.makeText(CreateSchedule.this, "Selected day must be past or same as current day", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            }

            //icon but doest work
//            ArrayList<String> icon  = new ArrayList<>();
//            if (schedule_cate_spinner.getSelectedItem().toString().equals("Root")) {
//                icon.add("0");
//            } else {
//                db.collection("categories")
//                        .whereEqualTo("name", schedule_cate_spinner.getSelectedItem().toString())
//                        .get()
//                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                                if (task.isSuccessful()) {
//                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
//                                        icon.add(document.getString("icon"));
//                                    }
//                                } else {
//                                }
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//
//                            }
//                        });
//            }

            Map<String, Object> schedule = new HashMap<>();
            schedule.put("Name", sch_name.getText().toString());
            schedule.put("Category", schedule_cate_spinner.getSelectedItem().toString());
            schedule.put("TimeStart", timePicker.getHour()+":"+timePicker.getMinute());
            schedule.put("TimeEnd", timePicker2.getHour()+":"+timePicker2.getMinute());
            schedule.put("Day", dayPicked+"/"+pickmonth2+"/"+pickyear);
//            schedule.put("Icon", icon.get(0)+"");
//            icon.clear();
            if (sch_repeat_radio.isChecked()) {
                schedule.put("Repeat", "Yes");
            } else {
                schedule.put("Repeat", "No");
            }
            int minute;
            int hour;

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
                    hour = 24-Math.abs(b);
                }
            } else {
                minute = 60-Math.abs(a)-1;
                if (b>=0) {
                    hour = b-1;
                } else {
                    hour = 24-Math.abs(b)-1;
                }
            }
            schedule.put("Remain", hour+":"+minute+":0");

            db.collection("schedules")
                    .add(schedule)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(CreateSchedule.this, "Schedule added successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(CreateSchedule.this, NotificationReceiver.class);
                        intent.putExtra("title", schedule.get("Name")+"");
                        intent.putExtra("text", "Your schedule "+schedule.get("Name")+" has started!!");
                        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent pending = PendingIntent.getBroadcast(CreateSchedule.this, (int)Math.floor(Math.random()*(1000-10+1)+10), intent, 0);
                        // Schdedule notification
                        AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                        //long timenow = System.currentTimeMillis();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                        String dateString = dayPicked+"-"+pickmonth2+"-"+pickyear+" "+timePicker.getHour()+":"+timePicker.getMinute()+":00";
                        try{
                            //formatting the dateString to convert it into a Date
                            Date date = sdf.parse(dateString);
                            manager.set(AlarmManager.RTC_WAKEUP, date != null ? date.getTime() : 0, pending);
                        }catch(ParseException e){
                            e.printStackTrace();
                        }
//                                manager.set(AlarmManager.RTC_WAKEUP, timenow+4000, pending);
                        onBackPressed();
                    })
                    .addOnFailureListener(e -> Toast.makeText(CreateSchedule.this, "Error, try again", Toast.LENGTH_SHORT).show());
        });

        Button cancel = findViewById(R.id.schedule_cancel);
        cancel.setOnClickListener(v -> {
            Toast.makeText(v.getContext(), "Canceled operation", Toast.LENGTH_SHORT).show();
            onBackPressed();
        });
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
//        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + getPackageName() + "/" + R.raw.jojo);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "R.string.channel_name";
            String description = "R.string.channel_description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ec", name, importance);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
//            channel.enableLights(true);
//            channel.enableVibration(true);
            channel.setDescription(description);
            channel.setSound(uri,attributes);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}