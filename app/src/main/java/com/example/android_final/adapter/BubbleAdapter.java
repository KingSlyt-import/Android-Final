package com.example.android_final.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.R;
import com.example.android_final.data.Bubble;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BubbleAdapter extends RecyclerView.Adapter<BubbleAdapter.ViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Bubble> messages;
    Context context;
    List<ViewHolder> viewHolderList = new ArrayList<>();
    int check = 0;
    public BubbleAdapter(Context context,List<Bubble> messages) {
        this.messages = messages;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.bubble_item,parent,false);
        ViewHolder holder = new ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.itemView.setOnClickListener (new View.OnClickListener() {
            CountDownTimer countDownTimer;
            @Override
            public void onClick(View v) {
                if (holder.bubble_time.getText().toString().equals("Done!")) {
                    //do nothing
                } else {
//                    if (holder.bubble_magic.getText().equals("a") && countDownTimer!=null) {
//                        countDownTimer.cancel();
//                    }
                    if (holder.bubble_magic.getText().equals("a") && check == 1) {
                        new AlertDialog.Builder(context)
                                .setTitle("Alert")
                                .setMessage("You need to stop the timer before starting a new one!")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.

                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                    if (holder.bubble_magic.getText().equals("a") && check == 0) {
                        check = 1;
                        int hour, minute, second;
                        String[] time = holder.bubble_time.getText().toString().split(":");
                        if (time.length==3) {
                            hour = Integer.parseInt(time[0]);
                            minute = Integer.parseInt(time[1]);
                            second = Integer.parseInt(time[2]);
                        } else if (time.length==2) {
                            hour = 0;
                            minute = Integer.parseInt(time[0]);
                            second = Integer.parseInt(time[1]);
                        } else {
                            hour = 0;
                            minute = 0;
                            second = Integer.parseInt(time[1]);
                        }
                        long millisInFuture = hour*3600000+minute*60000+second*1000;
                        holder.bubble.setBackgroundResource(R.drawable.circle_background2);
                        holder.bubble_magic.setText("b");
                        countDownTimer = new CountDownTimer(millisInFuture, 1000) {
                            public void onTick(long millisUntilFinished) {
                                long temp_hour = millisUntilFinished/1000/3600;
                                long temp_minute = millisUntilFinished/1000%3600/60;
                                long temp_second = millisUntilFinished/1000%3600%60;
                                if (temp_hour != 0) {
                                    holder.bubble_time.setText(temp_hour+ ":" +temp_minute+":"+temp_second);
                                } else if (temp_hour==0) {
                                    holder.bubble_time.setText(temp_minute+":"+temp_second);
                                } else if (temp_hour==0 && temp_minute==0){
                                    holder.bubble_time.setText(""+temp_second);
                                }
                            }
                            public void onFinish() {
                                holder.bubble_time.setText("Done!");
                            }
                        }.start();
                    } else if (holder.bubble_magic.getText().equals("b")) {
                        check = 0;
                        countDownTimer.cancel();
                        holder.bubble.setBackgroundResource(R.drawable.circle_background);
                        holder.bubble_magic.setText("a");
                        DocumentReference updateRemain = db.collection("schedules").document(holder.bubble_documentname.getText().toString());
                        updateRemain
                                .update("Remain", holder.bubble_time.getText().toString())
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
                    }


                }
            }
        });

        final Bubble m = messages.get(position);
        if(m==null)
            return;
        holder.bubble_icon.setImageResource(m.getIcon());
        holder.bubble_name.setText(m.getName());
        holder.bubble_time.setText(m.getTime());
        holder.bubble_documentname.setText(m.getDocument());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView bubble_magic;
        TextView bubble_name;
        TextView bubble_time;
        ImageView bubble_icon;
        TextView bubble_documentname;
        LinearLayout bubble;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bubble_magic = itemView.findViewById(R.id.bubble_magic);
            bubble_name = itemView.findViewById(R.id.bubble_name);
            bubble_time = itemView.findViewById(R.id.bubble_time);
            bubble_icon = itemView.findViewById(R.id.bubble_icon);
            bubble_documentname = itemView.findViewById(R.id.bubble_documentname);
            bubble = itemView.findViewById(R.id.bubble);
        }
    }
}
