package com.example.android_final.fragment;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.android_final.Notify.AlarmReceiver;
import com.example.android_final.R;
import com.example.android_final.adapter.AlarmAdapter;
import com.example.android_final.captcha.EnglishNumberToWords;
import com.example.android_final.captcha.RandomMath;
import com.example.android_final.data.Alarm;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class AlarmFragment extends Fragment {
    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    int tHour, tMinute;
    Button stopalarmasd;
    Intent intent;

    //for the captcha
    public static final int MAX_NUMBER = 100;

    public static final Random RANDOM = new Random();

    interface CaptchaResult {
        void validate(boolean result);
    }

    @SuppressLint({"SetTextI18n", "NotifyDataSetChanged"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_alarm, container, false);
        //create alarm
        db = FirebaseFirestore.getInstance();

        //get current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

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
                        if (doc.get("userId").equals(firebaseUser.getUid())) {
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
                            alarm.put("userId", firebaseUser.getUid());
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

        stopalarmasd = view.findViewById(R.id.stopalarmasd);
        stopalarmasd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                processValidation(result -> {
//                    if (result) {
//                        intent = new Intent(getContext(), AlarmReceiver.class);
//                        intent.putExtra("signal", "off");
//                        getContext().sendBroadcast(intent);
//                        Toast.makeText(getContext(), "Captcha success", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Toast.makeText(getContext(), "Captcha fail", Toast.LENGTH_SHORT).show();
//                    }
//                });

                db.collection("rings").document("rings").get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String ringing = document.getString("rings");
                                        if (ringing.equals("no")) {
                                        } else {
                                            processValidation(result -> {
                                                if (result) {
                                                    intent = new Intent(getContext(), AlarmReceiver.class);
                                                    intent.putExtra("signal", "off");
                                                    getContext().sendBroadcast(intent);
                                                    Toast.makeText(getContext(), "Captcha success", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    Toast.makeText(getContext(), "Captcha fail", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    } else {
                                        Log.d("getdata", "No such document");
                                    }
                                } else {
                                    Log.d("getdata", "get failed with ", task.getException());
                                }
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

    public void processValidation(AlarmFragment.CaptchaResult captchaResult) {
        int randomCaptcha = RANDOM.nextInt(2) + 1;

        switch (randomCaptcha) {
            case 1:
                final int num1 = RANDOM.nextInt(MAX_NUMBER);
                final int num2 = RANDOM.nextInt(num1);
                String calculate = RandomMath.ShowEquation(num1, num2);
                final int result = RandomMath.ShowResult(num1, num2, calculate);
                new MaterialDialog.Builder(getContext()).title("Enter the correct number").
                        content(calculate.toUpperCase()).
                        inputType(InputType.TYPE_CLASS_NUMBER).
                        input("", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                int number = -1;

                                try {
                                    number = Integer.parseInt(input.toString());
                                } catch (Exception e) {
                                    number = -1;
                                }

                                captchaResult.validate(number == result);
                            }
                        }).show();
                break;
            case 2:
                final int numberToFind = RANDOM.nextInt(MAX_NUMBER);
                String numberToStr = EnglishNumberToWords.convert(numberToFind);

                new MaterialDialog.Builder(getContext()).title("Enter the correct number").
                        content(numberToStr.toUpperCase()).
                        inputType(InputType.TYPE_CLASS_NUMBER).
                        input("", "", new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {
                                int number = -1;

                                try {
                                    number = Integer.parseInt(input.toString());
                                } catch (Exception e) {
                                    number = -1;
                                }

                                captchaResult.validate(number == numberToFind);
                            }
                        }).show();
                break;
        }
    }
}