package com.example.android_final.fragment.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android_final.R;
import com.example.android_final.adapter.BubbleAdapter;
import com.example.android_final.data.Bubble;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SchedulesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SchedulesFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseFirestore db;

    public SchedulesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SchedulesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SchedulesFragment newInstance(String param1, String param2) {
        SchedulesFragment fragment = new SchedulesFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedules, container, false);
        List<Bubble> messagesList = new ArrayList<>();
        RecyclerView bubble_schedule = view.findViewById(R.id.bubble_schedule2);
        bubble_schedule.setHasFixedSize(true);
        BubbleAdapter bubbleAdapter = new BubbleAdapter(getActivity(), messagesList);
        bubble_schedule.setAdapter(bubbleAdapter);
        bubble_schedule.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        db = FirebaseFirestore.getInstance();
        db.collection("schedules")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        messagesList.clear();
                        bubbleAdapter.notifyDataSetChanged();
                        if (e != null) {

                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("Name")!=null && doc.get("Remain")!=null) {
                                messagesList.add(new Bubble(R.drawable.icon1, doc.getString("Name"), doc.getString("Remain"), doc.getId()));
                                bubbleAdapter.notifyDataSetChanged();
                            }

                        }
                    }

                });
        return view;
    }
}