package com.example.android_final.adapter;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.android_final.Notify.AlarmReceiver;
import com.example.android_final.R;
import com.example.android_final.captcha.EnglishNumberToWords;
import com.example.android_final.captcha.RandomMath;
import com.example.android_final.custom.CustomRadioButton2;
import com.example.android_final.data.Alarm;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.ViewHolder>{

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Alarm> messages;
    Context context;
    List<AlarmAdapter.ViewHolder> viewHolderList = new ArrayList<>();
    AlarmManager manager;
    PendingIntent pending;
    Intent intent;

    //for the captcha
    public static final int MAX_NUMBER = 100;

    public static final Random RANDOM = new Random();

    interface CaptchaResult {
        void validate(boolean result);
    }

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
        if (position%2==1) {
            holder.alarm_item_bg.setBackgroundColor(Color.parseColor("#d2f7dc"));
        } else {
            holder.alarm_item_bg.setBackgroundColor(Color.parseColor("#ffffff"));
        }
        if (m.getChecked().equals("yes")) {
            if (holder.alarm_onoroff.isChecked()) {

            } else {
                holder.alarm_onoroff.setChecked(true);
            }
        }
        else {
            if (holder.alarm_onoroff.isChecked()) {
                holder.alarm_onoroff_parent.clearCheck();
            } else {

            }
        }
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
                            holder.alarm_onoroff_parent.clearCheck();
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

        holder.alarm_onoroff.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                if (holder.alarm_onoroff.isChecked()) {

                    //captcha

                    ////// captcha ok //////
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
                    PendingIntent.getBroadcast(context, Integer.parseInt(m.getRandomID()), intent,
                            PendingIntent.FLAG_UPDATE_CURRENT).cancel();
                    intent.putExtra("signal", "off");
                    context.sendBroadcast(intent);
                    ////// captcha ok //////

                } else {
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
                    pending = PendingIntent.getBroadcast(context, Integer.parseInt(m.getRandomID()), intent, PendingIntent.FLAG_UPDATE_CURRENT);
                    // Schdedule notification
                    manager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
                    Calendar calendar = Calendar.getInstance();
                    String temp[] = m.getTime().split(":");
                    int hour = Integer.parseInt(temp[0]);
                    int minute = Integer.parseInt(temp[1]);
                    calendar.set(Calendar.HOUR_OF_DAY,hour);
                    calendar.set(Calendar.MINUTE,minute);
                    long milli = calendar.getTimeInMillis() - 25000;
                    long timenow = System.currentTimeMillis();
                    if (timenow > milli) {
                        milli += 86400000;
                    }
                    manager.set(AlarmManager.RTC_WAKEUP, milli, pending);

//                    LocalDateTime now = LocalDateTime.now();
//
//                    Log.d("milli", "milli: "+milli +"\n");
//                    if (now.getHour()>hour) {
//                        milli += 86400000;
//                    } else if (now.getHour()==hour) {
//                        if (now.getMinute()>minute) {
//                            milli += 86400000;
//                        }
//                    }

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
        CustomRadioButton2 alarm_onoroff;
        LinearLayout alarm_item_bg;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            alarm_time = itemView.findViewById(R.id.alarm_time);
            alarm_onoroff_parent = itemView.findViewById(R.id.alarm_onoroff_parent);
            alarm_onoroff = itemView.findViewById(R.id.alarm_onoroff);
            alarm_item_bg = itemView.findViewById(R.id.alarm_item_bg);
        }
    }

    public void processValidation(CaptchaResult captchaResult) {
        int randomCaptcha = RANDOM.nextInt(2) + 1;

        switch (randomCaptcha) {
            case 1:
                final int num1 = RANDOM.nextInt(MAX_NUMBER);
                final int num2 = RANDOM.nextInt(MAX_NUMBER);
                String calculate = RandomMath.ShowEquation(num1, num2);
                final int result = RandomMath.ShowResult(num1, num2, calculate);
                new MaterialDialog.Builder(context.getApplicationContext()).title("Enter the correct number").
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

                new MaterialDialog.Builder(context.getApplicationContext()).title("Enter the correct number").
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
