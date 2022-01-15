package com.example.android_final.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.R;
import com.example.android_final.adapter.SubTaskAdapter;
import com.example.android_final.addFunction.CreateSubTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;

public class SubTask extends AppCompatActivity {

    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    RecyclerView subtask_recyclerview;
    SubTaskAdapter subTaskAdapter;
    ArrayList<com.example.android_final.data.SubTask> subTask;
    FloatingActionButton subtask_add_task_addBtn;
    Toolbar toolbar;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_subtask);

        //set support action bar
        toolbar = findViewById(R.id.create_subtask_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

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

        //get current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //get data from TaskFragment
        Intent intent = getIntent();
        String task_title = intent.getStringExtra("task_title");

        //set activity title
        textView = findViewById(R.id.create_subtask_title);
        textView.setText(task_title);

        db = FirebaseFirestore.getInstance();
        db.collection("subtasks")
                .whereEqualTo("Father_task", task_title)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        subTask.clear();
                        subTaskAdapter.notifyDataSetChanged();

                        if (e != null) {
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("userId").equals(firebaseUser.getUid())) {
                                subTask.add(new com.example.android_final.data.SubTask(doc.getString("Name"), doc.getString("Importance"), doc.getString("Day"), doc.getString("Note"), doc.getId(), false));
                                Collections.sort(subTask, new Comparator<com.example.android_final.data.SubTask>() {
                                    @Override
                                    public int compare(com.example.android_final.data.SubTask sub_task, com.example.android_final.data.SubTask t1) {
                                        return sub_task.getImportance().compareTo(t1.getImportance());
                                    }
                                });
                                Collections.reverse(subTask);
                                subTaskAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });

        subtask_add_task_addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //push data to CreateSubTask
                Intent intent = new Intent(SubTask.this, CreateSubTask.class);
                intent.putExtra("task_title", task_title);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
