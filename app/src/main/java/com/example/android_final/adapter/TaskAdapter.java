package com.example.android_final.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.R;
import com.example.android_final.data.Note;
import com.example.android_final.data.Task;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Task> tasks;
    Context context;
    List<NoteAdapter.ViewHolder> viewHolderList = new ArrayList<>();
    public TaskAdapter(Context context,List<Task> tasks) {
        this.tasks = tasks;
        this.context = context;
    }
    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item,parent,false);
        TaskAdapter.ViewHolder holder = new TaskAdapter.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        final Task m = tasks.get(position);
        if(m==null)
            return;
        holder.task_item_name.setText(m.getName());
        holder.task_item_body.setText(m.getBody());
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView task_item_name;
        TextView task_item_body;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task_item_name = itemView.findViewById(R.id.task_item_name);
            task_item_body = itemView.findViewById(R.id.task_item_body);
        }
    }
}
