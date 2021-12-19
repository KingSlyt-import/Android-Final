package com.example.android_final.adapter;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.R;
import com.example.android_final.data.Bubble;

import java.util.List;

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
        final Bubble m = messages.get(position);
//        holder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                itemClickListener.onItemClicked(holder, m, position);
//            }
//        });
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
        TextView bubble_name;
        TextView bubble_time;
        ImageView bubble_icon;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bubble_name = itemView.findViewById(R.id.bubble_name);
            bubble_time = itemView.findViewById(R.id.bubble_time);
            bubble_icon = itemView.findViewById(R.id.bubble_icon);
        }
    }
}
