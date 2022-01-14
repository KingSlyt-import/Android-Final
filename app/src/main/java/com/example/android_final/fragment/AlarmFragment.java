package com.example.android_final.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android_final.R;
import com.example.android_final.adapter.AlarmAdapter;
import com.example.android_final.data.Alarm;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AlarmFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);

        //get current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //create alarm
        db = FirebaseFirestore.getInstance();
        Button setalarm = view.findViewById(R.id.setalarm);
        TextView no_alarm = view.findViewById(R.id.no_alarm);
//        Button stopalarm = view.findViewById(R.id.stopalarm);
        TimePicker AlarmTimePicker = view.findViewById(R.id.AlarmTimePicker);
        AlarmTimePicker.setIs24HourView(true);

        List<Alarm> messagesList = new ArrayList<>();
        RecyclerView bubble_schedule = view.findViewById(R.id.alarm_recyclerview);
        bubble_schedule.setHasFixedSize(true);
        AlarmAdapter bubbleAdapter = new AlarmAdapter(getActivity(), messagesList);
        bubble_schedule.setAdapter(bubbleAdapter);
        bubble_schedule.setLayoutManager(new LinearLayoutManager(getActivity()));

        //create line to seperate each item
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        bubble_schedule.addItemDecoration(dividerItemDecoration);

        db = FirebaseFirestore.getInstance();
        TextView youralarm = view.findViewById(R.id.youralarm);
        db.collection("alarms")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        messagesList.clear();
                        bubbleAdapter.notifyDataSetChanged();
                        if (messagesList.size()!=0) {
                            no_alarm.setText("");
                        } else {
                            no_alarm.setText("You don't have any alarm!");
                        }
                        if (messagesList.size()!=0) {
                            youralarm.setText("Your alarm:");
                        } else {
                            youralarm.setText("");
                        }
                        if (e != null) {
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("time")!=null && doc.getString("randomID")!=null) {
                                messagesList.add(new Alarm(doc.getString("time"), doc.getString("randomID"), doc.getId(), doc.getString("rung"), doc.getString("checked")));
                                Collections.sort(messagesList, new Comparator<Alarm>() {
                                    @Override
                                    public int compare(Alarm task, Alarm t1) {
                                        return task.getTime().compareTo(t1.getTime());
                                    }
                                });
                                if (messagesList.size()!=0) {
                                    no_alarm.setText("");
                                } else {
                                    no_alarm.setText("You don't have any alarm!");
                                }
                                bubbleAdapter.notifyDataSetChanged();
                                if (messagesList.size()!=0) {
                                    youralarm.setText("Your alarm:");
                                } else {
                                    youralarm.setText("");
                                }
                            }
                        }
                    }
                });

        setalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = 0;
                int minute = AlarmTimePicker.getMinute();
                int hour = AlarmTimePicker.getHour();
                int randomID = (int)Math.floor(Math.random()*(100-10+1)+10);

                //validate
                for (Alarm a: messagesList) {
                    if (a.getTime().equals((hour<10? "0"+hour : hour)+":"+(minute<10? "0"+minute : minute))) {
                        temp = 1;
                    }
                }

                if (temp == 1) {
                    Toast.makeText(getContext(), "Alarm already exits!", Toast.LENGTH_SHORT).show();
                } else {
                    Map<String, Object> alarm = new HashMap<>();
                    alarm.put("time", (hour<10? "0"+hour : hour)+":"+(minute<10? "0"+minute : minute));
                    alarm.put("randomID", randomID+"");
                    alarm.put("rung", "no");
                    alarm.put("checked", "no");
//                alarm.put("rung", "no");
                    db.collection("alarms")
                            .add(alarm)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(@NonNull DocumentReference documentReference) {
//                                    Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Error, try again", Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

//        stopalarm.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AlarmAdapter alarmA = new AlarmAdapter();
//                alarmA.StopAlarm();
//            }
//        });

        return view;
    }
}