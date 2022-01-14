package com.example.android_final.fragment.order;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.example.android_final.R;
import com.example.android_final.adapter.BubbleAdapter;
import com.example.android_final.data.Bubble;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class SchedulesFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_schedules, container, false);
        TextView no_schedule = view.findViewById(R.id.no_schedule);
        List<Bubble> messagesList = new ArrayList<>();
        RecyclerView bubble_schedule = view.findViewById(R.id.bubble_schedule2);
        bubble_schedule.setHasFixedSize(true);
        BubbleAdapter bubbleAdapter = new BubbleAdapter(getActivity(), messagesList);
        bubble_schedule.setAdapter(bubbleAdapter);
        bubble_schedule.setLayoutManager(new GridLayoutManager(getActivity(), 3));

        //get current user
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();
        db.collection("schedules")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        messagesList.clear();
                        bubbleAdapter.notifyDataSetChanged();
                        if (messagesList.size()!=0) {
                            no_schedule.setText("");
                        } else {
                            no_schedule.setText("You don't have any alarm!");
                        }
                        if (e != null) {

                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("userId").equals(firebaseUser.getUid()) && doc.get("Remain") !=null) {
                                //Integer.parseInt(doc.getString("Icon"))
                                messagesList.add(new Bubble(0, doc.getString("Name"), doc.getString("Remain"), doc.getId()));
                                bubbleAdapter.notifyDataSetChanged();
                                if (messagesList.size()!=0) {
                                    no_schedule.setText("");
                                } else {
                                    no_schedule.setText("You don't have any alarm!");
                                }
                            }
                        }
                    }

                });
        return view;
    }
}