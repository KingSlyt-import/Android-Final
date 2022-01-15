package com.example.android_final.fragment.order;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android_final.R;
import com.example.android_final.adapter.TaskAdapter;
import com.example.android_final.data.Task;
import com.example.android_final.fragment.SubTaskActivity;
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
import java.util.List;
import java.util.Objects;

public class TasksFragment extends Fragment implements TaskAdapter.OnTaskListener {

    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    List<Task> taskList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tasks, container, false);

        RecyclerView task_recyclerview = view.findViewById(R.id.task_recyclerview);
        task_recyclerview.setHasFixedSize(true);
        TaskAdapter taskAdapter = new TaskAdapter(getActivity(), taskList, this::OnTaskClick);
        task_recyclerview.setAdapter(taskAdapter);
        task_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        //create line to seperate each item
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        task_recyclerview.addItemDecoration(dividerItemDecoration);

        //hold and drag item
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(task_recyclerview);

        //get current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();
        db.collection("tasks")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        taskList.clear();
                        taskAdapter.notifyDataSetChanged();

                        if (e != null) {
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if (Objects.equals(doc.get("userId"), firebaseUser.getUid())) {
                                taskList.add(new Task(doc.getString("Name"), doc.getString("Importance"), doc.getString("Day"), doc.getString("Note"), doc.getId(), false));
                                Collections.sort(taskList, new Comparator<Task>() {
                                    @Override
                                    public int compare(Task task, Task t1) {
                                        return task.getImportance().compareTo(t1.getImportance());
                                    }
                                });
                                Collections.reverse(taskList);
                                taskAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
        return view;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
            ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Collections.swap(taskList, fromPosition, toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    @Override
    public void OnTaskClick(int position) {

        Intent intent = new Intent(getActivity().getApplicationContext(), SubTaskActivity.class);
        intent.putExtra("task_title", taskList.get(position).getName());
        startActivity(intent);
    }
}