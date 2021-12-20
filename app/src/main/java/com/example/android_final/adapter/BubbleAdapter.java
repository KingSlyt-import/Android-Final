package com.example.android_final.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.R;
import com.example.android_final.data.Bubble;

import java.util.List;
import java.util.Objects;

public class BubbleAdapter extends RecyclerView.Adapter<BubbleAdapter.ViewHolder> {

    List<Bubble> messages;
    Context context;
//    ItemClickListener itemClickListener;
    public BubbleAdapter(Context context,List<Bubble> messages) {
        this.messages = messages;
        this.context = context;
//        this.itemClickListener = itemClickListener;
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
                int hour, minute, second;
                String[] time = holder.bubble_time.getText().toString().split("h");
                if (time.length>1) {
                    hour = Integer.parseInt(time[0]);
                    String[] time2 = time[1].toString().split("m");
                    minute = Integer.parseInt(time2[0]);
                    second = Integer.parseInt(time2[1]);
                } else {
                    hour = 0;
                    minute = Integer.parseInt(time[0]);
                    second = 0;
                }
                long millisInFuture = hour*3600000+minute*60000+second*1000;
                if (holder.bubble_magic.getText().equals("a")) {
                    holder.bubble.setBackgroundResource(R.drawable.circle_background2);
                    holder.bubble_magic.setText("b");
                    countDownTimer = new CountDownTimer(millisInFuture, 1000) {

                        public void onTick(long millisUntilFinished) {
                            long temp_hour = millisUntilFinished/1000/3600;
                            long temp_minute = millisUntilFinished/1000%3600/60;
                            long temp_second = millisUntilFinished/1000%3600%60;

                            holder.bubble_time.setText(temp_hour+ "h" +temp_minute+"m"+temp_second);
                        }

                        public void onFinish() {
                            holder.bubble_time.setText("done!");
                        }
                    }.start();
                } else if (holder.bubble_magic.getText().equals("b")) {
                    countDownTimer.cancel();
                    holder.bubble.setBackgroundResource(R.drawable.circle_background);
                    holder.bubble_magic.setText("a");
                }
            }
        });

        final Bubble m = messages.get(position);
        if(m==null)
            return;
        holder.bubble_icon.setImageResource(m.getIcon());
        holder.bubble_name.setText(m.getName());
        holder.bubble_time.setText(m.getTime());
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
        LinearLayout bubble;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bubble_magic = itemView.findViewById(R.id.bubble_magic);
            bubble_name = itemView.findViewById(R.id.bubble_name);
            bubble_time = itemView.findViewById(R.id.bubble_time);
            bubble_icon = itemView.findViewById(R.id.bubble_icon);
            bubble = itemView.findViewById(R.id.bubble);
        }
    }
}
