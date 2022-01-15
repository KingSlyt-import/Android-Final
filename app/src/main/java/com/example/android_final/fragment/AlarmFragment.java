package com.example.android_final.fragment;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.R;
import com.example.android_final.adapter.AlarmAdapter;
import com.example.android_final.data.Alarm;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AlarmFragment extends Fragment {
    FirebaseFirestore db;
    int tHour, tMinute;

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        //create alarm
        db = FirebaseFirestore.getInstance();
        TextView no_alarm = view.findViewById(R.id.no_alarm);
//        Button stopalarm = view.findViewById(R.id.stopalarm);

        List<Alarm> messagesList = new ArrayList<>();
        RecyclerView bubble_schedule = view.findViewById(R.id.alarm_recyclerview);
        bubble_schedule.setHasFixedSize(true);
        AlarmAdapter bubbleAdapter = new AlarmAdapter(getActivity(), messagesList);
        bubble_schedule.setAdapter(bubbleAdapter);
        bubble_schedule.setLayoutManager(new LinearLayoutManager(getActivity()));

        //create line to seperate each item
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL);
        bubble_schedule.addItemDecoration(dividerItemDecoration);

        db = FirebaseFirestore.getInstance();
        TextView youralarm = view.findViewById(R.id.youralarm);
        db.collection("alarms")
                .addSnapshotListener((value, e) -> {
                    messagesList.clear();
                    bubbleAdapter.notifyDataSetChanged();
                    if (messagesList.size()!=0) {
                        no_alarm.setText("");
                    } else {
                        no_alarm.setVisibility(View.VISIBLE);
                    }
                    if (messagesList.size()!=0) {
                        youralarm.setText("Your alarm:");
                    }
                    if (e != null) {
                        return;
                    }
                    assert value != null;
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
                });

        FloatingActionButton alarmSet = view.findViewById(R.id.fragment_alarm_addBtn);
        alarmSet.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    getContext(),
                    (view1, hourOfDay, minute) -> {
                        tHour = hourOfDay;
                        tMinute = minute;
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(0, 0, 0, hourOfDay, minute);

                        int randomID = (int)Math.floor(Math.random()*(100-10+1)+10);
                        boolean temp = false;

                        for (Alarm a: messagesList) {
                            if (a.getTime().equals((tHour < 10 ? "0" + tHour : tHour) + ":" + (minute < 10 ? "0" + minute : minute))) {
                                temp = true;
                                break;
                            }
                        }

                        if (temp) {
                            Toast.makeText(getContext(), "Alarm already exits!", Toast.LENGTH_SHORT).show();
                        } else {
                            Map<String, Object> alarm = new HashMap<>();
                            alarm.put("time", (tHour<10? "0"+tHour : tHour)+":"+(minute<10? "0"+minute : minute));
                            alarm.put("randomID", randomID+"");
                            alarm.put("rung", "no");
                            alarm.put("checked", "no");
                            db.collection("alarms")
                                .add(alarm)
                                .addOnSuccessListener(documentReference -> Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(getContext(), "Error, try again", Toast.LENGTH_SHORT).show());
                        }
                    }, 12, 0, false
            );
            timePickerDialog.show();
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