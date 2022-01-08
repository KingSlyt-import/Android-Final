package com.example.android_final.adapter;

import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.R;
import com.example.android_final.addFunction.CreateTask;
import com.example.android_final.custom.CustomRadioButton;
import com.example.android_final.data.Note;
import com.example.android_final.data.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.ViewHolder>{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Task> tasks;
    Context context;
    List<NoteAdapter.ViewHolder> viewHolderList = new ArrayList<>();
    int i = 0;
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
        //holder.task_item_completion.setChecked(false);
        holder.task_item_radiogroup.clearCheck();

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                PopupMenu popup = new PopupMenu(holder, view);
//                MenuInflater inflater = popup.getMenuInflater();
//                inflater.inflate(R.menu.edit_delete_menu, popup.getMenu());
//                popup.show();
                Toast.makeText(context, holder.task_item_name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

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

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView task_item_name;
        TextView task_item_body;
        CustomRadioButton task_item_completion;
        RadioGroup task_item_radiogroup;
//        CheckBox task_item_completion;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            task_item_name = itemView.findViewById(R.id.task_item_name);
            task_item_body = itemView.findViewById(R.id.task_item_body);
            task_item_completion = itemView.findViewById(R.id.task_item_completion);
            task_item_radiogroup = itemView.findViewById(R.id.task_item_radiogroup);
        }
    }
}
