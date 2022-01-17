package com.example.android_final.fragment;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android_final.R;
import com.example.android_final.adapter.TaskAdapter;
import com.example.android_final.data.SubTask;
import com.example.android_final.data.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HomeFragment extends Fragment implements TaskAdapter.OnTaskListener{

    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    private TextView home_day;
    private TextView home_month;
    private TextView home_year;
    private TextView display_tasks;
    private TextView home_congrat;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        String dayOfMonth = LocalDateTime.now().getDayOfMonth()+"";
        String month = LocalDateTime.now().getMonth().name()+"";
        String year = LocalDateTime.now().getYear()+"";
        home_day = view.findViewById(R.id.home_day);
        home_month = view.findViewById(R.id.home_month);
        home_year = view.findViewById(R.id.home_year);
        home_day.setText(dayOfMonth);
        home_month.setText(month);
        home_year.setText(year);
        display_tasks = view.findViewById(R.id.display_tasks);
        home_congrat = view.findViewById(R.id.home_congrat);

        List<Task> taskList = new ArrayList<>();
        RecyclerView task_recyclerview = view.findViewById(R.id.home_task_recyclerview);
        task_recyclerview.setHasFixedSize(true);
        TaskAdapter taskAdapter = new TaskAdapter(getActivity(), taskList, this::OnTaskClick);
        task_recyclerview.setAdapter(taskAdapter);
        task_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));

        //get current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //db
        db = FirebaseFirestore.getInstance();
        int thismonth = LocalDateTime.now().getMonthValue();
        String time = dayOfMonth+(thismonth < 10 ? "/0"+LocalDateTime.now().getMonthValue() : "/"+LocalDateTime.now().getMonth()) + "/" + LocalDateTime.now().getYear();

        db.collection("tasks")
                .whereEqualTo("Day", time)
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
                            if (doc.get("userId").equals(firebaseUser.getUid())) {
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
                        if (taskList.size()>0) {
                            display_tasks.setText("You have "+taskList.size()+" tasks today!");
                            home_congrat.setText("");
                        } else {
                            home_congrat.setText("Congratulation! \n You have no task today!!");
                            display_tasks.setText("");
                        }
                    }
                });
        return view;
    }

    @Override
    public void OnTaskClick(int position) {

    }
}