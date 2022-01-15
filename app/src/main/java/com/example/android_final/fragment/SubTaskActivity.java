package com.example.android_final.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.R;
import com.example.android_final.adapter.SubTaskAdapter;
import com.example.android_final.adapter.TaskAdapter;
import com.example.android_final.addFunction.CreateTask;
import com.example.android_final.data.SubTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;

public class SubTaskActivity extends AppCompatActivity {

    RecyclerView subtask_recyclerview;
    SubTaskAdapter subTaskAdapter;
    ArrayList<SubTask> subTask;
    FloatingActionButton subtask_add_task_addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_subtask);

        subtask_recyclerview = findViewById(R.id.subtask_recyclerview);
        subtask_add_task_addBtn = findViewById(R.id.subtask_add_task_addBtn);
        subTask = new ArrayList<>();

        subtask_recyclerview.setHasFixedSize(true);
        subTaskAdapter = new SubTaskAdapter(this, subTask);
        subtask_recyclerview.setAdapter(subTaskAdapter);
        subtask_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        //create line to seperate each item
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        subtask_recyclerview.addItemDecoration(dividerItemDecoration);

        //hold and drag item
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(subtask_recyclerview);

        subtask_add_task_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SubTaskActivity.this, CreateTask.class);
                startActivity(intent);
            }
        });
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(subTask, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };
}
