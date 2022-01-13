package com.example.android_final.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.android_final.Notify.NotificationReceiver;
import com.example.android_final.R;
import com.example.android_final.adapter.AlarmAdapter;
import com.example.android_final.adapter.BubbleAdapter;
import com.example.android_final.addFunction.CreateSchedule;
import com.example.android_final.data.Alarm;
import com.example.android_final.data.Bubble;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AlarmFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseFirestore db;

    public AlarmFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AlarmFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmFragment newInstance(String param1, String param2) {
        AlarmFragment fragment = new AlarmFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        //create alarm
        db = FirebaseFirestore.getInstance();
        Button setalarm = view.findViewById(R.id.setalarm);
//        Button stopalarm = view.findViewById(R.id.stopalarm);
        TimePicker AlarmTimePicker = view.findViewById(R.id.AlarmTimePicker);
        AlarmTimePicker.setIs24HourView(true);

        List<Alarm> messagesList = new ArrayList<>();
        RecyclerView bubble_schedule = view.findViewById(R.id.alarm_recyclerview);
        bubble_schedule.setHasFixedSize(true);
        AlarmAdapter bubbleAdapter = new AlarmAdapter(getActivity(), messagesList);
        bubble_schedule.setAdapter(bubbleAdapter);
        bubble_schedule.setLayoutManager(new LinearLayoutManager(getActivity()));

        db = FirebaseFirestore.getInstance();
        db.collection("alarms")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        messagesList.clear();
                        bubbleAdapter.notifyDataSetChanged();
                        if (e != null) {
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("time")!=null && doc.getString("randomID")!=null) {
                                messagesList.add(new Alarm(doc.getString("time"), doc.getString("randomID"), doc.getId(), doc.getString("rung"), doc.getString("checked")));
                                bubbleAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                });

        setalarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int minute = AlarmTimePicker.getMinute();
                int hour = AlarmTimePicker.getHour();
                int randomID = (int)Math.floor(Math.random()*(100-10+1)+10);
                Map<String, Object> alarm = new HashMap<>();
                alarm.put("time", hour+":"+(minute<10? "0"+minute : minute));
                alarm.put("randomID", randomID+"");
                alarm.put("rung", "no");
                alarm.put("checked", "no");
//                alarm.put("rung", "no");
                db.collection("alarms")
                        .add(alarm)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(@NonNull DocumentReference documentReference) {
                                Toast.makeText(getContext(), "Done", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Error, try again", Toast.LENGTH_SHORT).show();
                            }
                        });
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