package com.example.android_final.adapter;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.Notify.AlarmReceiver;
import com.example.android_final.R;
import com.example.android_final.data.Alarm;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder>{


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Alarm> messages;
    Context context;
    List<AlarmAdapter.ViewHolder> viewHolderList = new ArrayList<>();
    AlarmManager manager;
    PendingIntent pending;
    Intent intent;

    public AlarmAdapter(Context context,List<Alarm> messages) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.alarm_item,parent,false);
        AlarmAdapter.ViewHolder holder = new AlarmAdapter.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        intent = new Intent(context, AlarmReceiver.class);
        createNotificationChannel();

        final Alarm m = messages.get(position);
        if(m==null)
            return;
        holder.alarm_time.setText(m.getTime());
        if (m.getChecked().equals("yes")) {
            holder.alarm_onoroff.setChecked(true);
        } else {
            holder.alarm_onoroff.setChecked(false);
        }
//        if (m.getRung().equals("yes")) {
//            holder.alarm_item_bg.setBackgroundColor(123);
//        } else {
//            holder.alarm_item_bg.setBackgroundColor(999);
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(holder.itemView.getContext(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.delete_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (holder.alarm_onoroff.isChecked()) {
                            holder.alarm_onoroff.setChecked(false);
                        }
                        switch (menuItem.getItemId()){
                            case R.id.deleteitemonly:
                                db.collection("alarms")
                                        .document(m.getDocument())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                return true;
                        }
                        return false;
                    }
                });
            }
        });

        holder.alarm_onoroff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    db.collection("alarms").document(m.getDocument()).update("checked","yes")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                    intent.putExtra("signal", "on");
                    intent.putExtra("title", "Your alarm has started!!");
                    intent.putExtra("text", "Click to open alarm tab");
                    intent.putExtra("document", m.getDocument());
                    long timenow = System.currentTimeMillis();
                    int random = (int)Math.floor(Math.random()*(100-10+1)+10);
                    pending = PendingIntent.getBroadcast(context, Integer.parseInt(m.getRandomID()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    // Schdedule notification
                    manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                    Calendar calendar = Calendar.getInstance();
                    String temp[] = m.getTime().split(":");
                    int hour = Integer.parseInt(temp[0]);
                    int minute = Integer.parseInt(temp[1]);
                    calendar.set(Calendar.HOUR_OF_DAY,hour);
                    calendar.set(Calendar.MINUTE,minute);
                    long milli = calendar.getTimeInMillis();
                    LocalDateTime now = LocalDateTime.now();
                    Log.d("TAG", "onCheckedChanged: "+milli );
                    if (now.getHour()>hour) {
                        milli += 86400000;
                        Log.d("TAG", "onCheckedChanged: "+milli );
                    } else if (now.getHour()==hour) {
                        if (now.getMinute()>minute) {
                            milli += 86400000;
                            Log.d("TAG", "onCheckedChanged: "+milli );
                        }
                    }
                    manager.set(AlarmManager.RTC_WAKEUP, milli, pending);
                } else {
                    db.collection("alarms").document(m.getDocument()).update("checked","no")
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
//                    db.collection("alarms").document(m.getDocument()).update("rung","no")
//                            .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                @Override
//                                public void onSuccess(Void aVoid) {
//
//                                }
//                            })
//                            .addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull Exception e) {
//
//                                }
//                            });
                    PendingIntent.getBroadcast(context, Integer.parseInt(m.getRandomID()), intent,
                            PendingIntent.FLAG_UPDATE_CURRENT).cancel();
                    if (manager != null && pending != null && intent != null) {

                        //////////////////captcha here////////////////////////////////////////

//                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                        LayoutInflater inflater = LayoutInflater.from(context);
//                        //file xml cua giao dien captcha muon bo vao
//                        View dialogView = inflater.inflate(R.layout.inputname, null);
//                        builder.setView(dialogView);
//                        //anh xa tu giao dien
//                        EditText editText = dialogView.findViewById(R.id.input);
//                        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                            @Override
//                            public void onClick(DialogInterface dialogInterface, int i) {
//                                //tat thong bao
//                                intent.putExtra("signal", "off");
//                                context.sendBroadcast(intent);
//                            }
//                        });
//                        builder.show();

                        //tat thong bao
//                        manager.cancel(pending);
                        intent.putExtra("signal", "off");
                        context.sendBroadcast(intent);
                    }
                }
            }
        });
    }

    private void createNotificationChannel() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ecec", name, importance);
            AudioAttributes attributes = new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                    .build();
            channel.setDescription(description);
            channel.setSound(uri, attributes);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView alarm_time;
        RadioGroup alarm_onoroff_parent;
        RadioButton alarm_onoroff;
        LinearLayout alarm_item_bg;
//        Button stopalarm;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alarm_time = itemView.findViewById(R.id.alarm_time);
            alarm_onoroff_parent = itemView.findViewById(R.id.alarm_onoroff_parent);
            alarm_onoroff = itemView.findViewById(R.id.alarm_onoroff);
            alarm_item_bg = itemView.findViewById(R.id.alarm_item_bg);
//            stopalarm = itemView.findViewById(R.id.stopalarm);
        }
    }

//    public void StopAlarm() {
//        if (manager != null && pending != null && intent != null) {
//
//            //////////////////captcha here////////////////////////////////////////
////            Log.d("ecec", "StopAlarm: ");
////            manager.cancel(pending);
//            intent.putExtra("signal", "off");
//            context.sendBroadcast(intent);
//        }
//    }
}
