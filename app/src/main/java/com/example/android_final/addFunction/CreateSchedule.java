package com.example.android_final.addFunction;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
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
    private Button done;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_schedule);

        createNotificationChannel();

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

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                //validate
                if (sch_name.getText().toString().isEmpty()) {
                    Toast.makeText(CreateSchedule.this, "Cannot create schedule with empty name", Toast.LENGTH_SHORT).show();
                    return;
                }

                LocalDate localDate = LocalDate.now();
                int currday = localDate.getDayOfMonth();
                int currmonth = localDate.getMonthValue();
                int curryear = localDate.getYear();

                int pickday = datePicker.getDayOfMonth();
                int pickmonth = datePicker.getMonth()+1;
                int pickyear = datePicker.getYear();
                String pickmonth2;
                if (pickmonth<10) {
                    pickmonth2 = "0"+pickmonth;
                } else {
                    pickmonth2 = ""+pickmonth;
                }




                if (pickyear < curryear) {
                    Toast.makeText(CreateSchedule.this, "Selected year must be past or same as current year", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    if (pickmonth < currmonth) {
                        Toast.makeText(CreateSchedule.this, "Selected month must be past or same as current year", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        if (pickday < currday) {
                            Toast.makeText(CreateSchedule.this, "Selected day must be past or same as current year", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                }

                Map<String, Object> schedule = new HashMap<>();
                schedule.put("Name", sch_name.getText().toString());
                schedule.put("Category", schedule_cate_spinner.getSelectedItem().toString());
                schedule.put("TimeStart", timePicker.getHour()+":"+timePicker.getMinute());
                schedule.put("TimeEnd", timePicker2.getHour()+":"+timePicker2.getMinute());
                schedule.put("Day", pickday+"/"+pickmonth+"/"+pickyear);
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
                        hour = 24-Math.abs(b);
                    }
                    schedule.put("Remain", hour+":"+minute+":0");
                } else {
                    minute = 60-Math.abs(a)-1;
                    if (b>=0) {
                        hour = b-1;
                    } else {
                        hour = 24-Math.abs(b)-1;
                    }
                    schedule.put("Remain", hour+":"+minute+":0");
                }

                db.collection("schedules")
                        .add(schedule)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(@NonNull DocumentReference documentReference) {
                                Toast.makeText(CreateSchedule.this, "Schedule added successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(CreateSchedule.this, NotificationReceiver.class);
                                intent.putExtra("title", schedule.get("Name")+"");
                                intent.putExtra("text", "Your schedule "+schedule.get("Name")+" has started!!");
                                PendingIntent pending = PendingIntent.getBroadcast(CreateSchedule.this, (int)Math.floor(Math.random()*(1000-10+1)+10), intent, 0);
                                // Schdedule notification
                                AlarmManager manager = (AlarmManager) getSystemService(ALARM_SERVICE);
                                //long timenow = System.currentTimeMillis();
                                SimpleDateFormat sdf = new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
                                String dateString = pickday+"-"+pickmonth2+"-"+pickyear+" "+timePicker.getHour()+":"+timePicker.getMinute()+":00";
                                try{
                                    //formatting the dateString to convert it into a Date
                                    Date date = sdf.parse(dateString);
                                    manager.set(AlarmManager.RTC_WAKEUP, date.getTime(), pending);
                                }catch(ParseException e){
                                    e.printStackTrace();
                                }
//                                manager.set(AlarmManager.RTC_WAKEUP, timenow+4000, pending);
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
}