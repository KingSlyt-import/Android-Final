package com.example.android_final.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.R;
import com.example.android_final.custom.CustomRadioButton;
import com.example.android_final.data.SubTask;
import com.example.android_final.data.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Task> tasks;
    ArrayList<SubTask> subTask;
    Context context;

    OnTaskListener onTaskListener;

    public TaskAdapter(Context context, List<Task> tasks, OnTaskListener onTaskListener) {
        this.context = context;
        this.tasks = tasks;
        this.onTaskListener = onTaskListener;
    }
    @NonNull
    @Override
    public TaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.task_item,parent,false);
        TaskAdapter.ViewHolder holder = new TaskAdapter.ViewHolder(v, onTaskListener);
        return holder;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.ViewHolder holder, int position) {
        final Task m = tasks.get(position);
        if(m==null)
            return;
        holder.task_item_name.setText(m.getName());

        int day = LocalDateTime.now().getDayOfMonth();
        int month = LocalDateTime.now().getMonthValue();
        int year = LocalDateTime.now().getYear();
        String temp_time[] = m.getDay().split("/");
        if (day == Integer.parseInt(temp_time[0]) && month == Integer.parseInt(temp_time[1]) && year == Integer.parseInt(temp_time[2])) {
            holder.task_item_body.setText("Today");
        } else {
            holder.task_item_body.setText(m.getDay());
        }
        holder.task_item_radiogroup.clearCheck();
        if (m.getImportance().equals("!")) {
            holder.task_importance.setText("Level: !");
            holder.task_importance.setTextColor(Color.parseColor("#55de1b"));
        } else if (m.getImportance().equals("!!")) {
            holder.task_importance.setText("Level: !!");
            holder.task_importance.setTextColor(Color.parseColor("#deb41b"));
        } else if (m.getImportance().equals("!!!")) {
            holder.task_importance.setText("Level: !!!");
            holder.task_importance.setTextColor(Color.parseColor("#de351b"));
        }
        if (position%2==1) {
            holder.task_item.setBackgroundColor(Color.parseColor("#d2f7dc"));
        } else {
            holder.task_item.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu popup = new PopupMenu(holder.itemView.getContext(), v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_delete_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edititem:
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Editing task");
                                LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
                                View dialogView = inflater.inflate(R.layout.edit_task, null);
                                builder.setView(dialogView);
                                EditText edit_task_name = dialogView.findViewById(R.id.edit_task_name);
                                EditText edit_task_note = dialogView.findViewById(R.id.edit_task_note);
                                edit_task_name.setText(holder.task_item_name.getText().toString());
                                edit_task_note.setText(holder.task_item_body.getText().toString());
                                builder
                                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("Name", edit_task_name.getText().toString());
                                                data.put("Note", edit_task_note.getText().toString());

                                                db.collection("tasks").document(m.getDocument())
                                                        .set(data, SetOptions.merge());
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                return true;
                            case R.id.deleteitem:
                                db.collection("tasks")
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
                return false;
            }
        });

//        holder.subtask_count.setText(subTask.size());

        holder.task_item_completion.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (holder.task_item_completion.isChecked()) {
                    //Toast.makeText(context, "Completed", Toast.LENGTH_SHORT).show();
                    new CountDownTimer(3000, 500) {
                        public void onTick(long millisUntilFinished) {
                            if(!holder.task_item_completion.isChecked()) {
                                cancel();
                            }
                        }
                        public void onFinish() {
                            db.collection("tasks")
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
                        }
                    }.start();
                } else {
                    //Toast.makeText(context, "Canceled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView task_item_name, task_item_body, subtask_count, task_importance;
        CustomRadioButton task_item_completion;
        RadioGroup task_item_radiogroup;
        LinearLayout task_item;

        OnTaskListener onTaskListener;
//        CheckBox task_item_completion;
        public ViewHolder(@NonNull View itemView, OnTaskListener onTaskListener) {
            super(itemView);
            task_item_name = itemView.findViewById(R.id.task_item_name);
            task_item_body = itemView.findViewById(R.id.task_item_body);
            subtask_count = itemView.findViewById(R.id.subtask_count);
            task_item_completion = itemView.findViewById(R.id.task_item_completion);
            task_item_radiogroup = itemView.findViewById(R.id.task_item_radiogroup);
            task_item = itemView.findViewById(R.id.task_item);
            task_importance = itemView.findViewById(R.id.task_importance);

            this.onTaskListener = onTaskListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onTaskListener.OnTaskClick(getAdapterPosition());
        }
    }

    public interface OnTaskListener {
        void OnTaskClick (int position);
    }
}
