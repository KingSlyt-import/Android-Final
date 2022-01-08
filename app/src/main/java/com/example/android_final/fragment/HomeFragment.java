package com.example.android_final.fragment;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android_final.R;
import com.example.android_final.adapter.TaskAdapter;
import com.example.android_final.data.Task;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "test";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseFirestore db;

    private TextView home_day;
    private TextView home_month;
    private TextView home_year;
    private TextView display_tasks;
    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HomeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


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

        List<Task> taskList = new ArrayList<>();
        RecyclerView task_recyclerview = view.findViewById(R.id.home_task_recyclerview);
        task_recyclerview.setHasFixedSize(true);
        TaskAdapter taskAdapter = new TaskAdapter(getActivity(), taskList);
        task_recyclerview.setAdapter(taskAdapter);
        task_recyclerview.setLayoutManager(new LinearLayoutManager(getActivity()));
        //db
        db = FirebaseFirestore.getInstance();
        int thismonth = LocalDateTime.now().getMonthValue();
        String time = dayOfMonth+(thismonth < 10 ? "/0"+LocalDateTime.now().getMonthValue() : "/"+LocalDateTime.now().getMonth()) + "/" + LocalDateTime.now().getYear();
        Log.d(TAG, "onCreateView: " +time);
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
                            if (doc.get("Name")!=null) {
                                taskList.add(new Task(doc.getString("Name"), doc.getString("Importance"), doc.getString("Day"), doc.getString("Note"), doc.getId(), false));
                                taskAdapter.notifyDataSetChanged();
                            }
                        }
                        display_tasks.setText("You have "+((taskList.size()>1) ? taskList.size()+" tasks today": taskList.size()+" task today"));
                    }
                });
        return view;
    }
}