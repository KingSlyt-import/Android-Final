package com.example.android_final.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android_final.R;
import com.google.firebase.firestore.FirebaseFirestore;


public class ListFragment extends Fragment {

    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list, container, false);
//        List<Bubble> messagesList = new ArrayList<>();
//        RecyclerView bubble_schedule = view.findViewById(R.id.bubble_schedule);
//        bubble_schedule.setHasFixedSize(true);
//        BubbleAdapter bubbleAdapter = new BubbleAdapter(getActivity(), messagesList);
//        bubble_schedule.setAdapter(bubbleAdapter);
//        bubble_schedule.setLayoutManager(new GridLayoutManager(getActivity(), 3));
//
//        db = FirebaseFirestore.getInstance();
//        db.collection("schedules")
//                .addSnapshotListener(new EventListener<QuerySnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable QuerySnapshot value,
//                                        @Nullable FirebaseFirestoreException e) {
//                        if (e != null) {
//
//                            return;
//                        }
//                        messagesList.clear();
//                        for (QueryDocumentSnapshot doc : value) {
//                            if (doc.get("Name")!=null && doc.get("Remain")!=null) {
//                                messagesList.add(new Bubble(R.drawable.icon1, doc.getString("Name"), doc.getString("Remain"), doc.getId()));
//                                bubbleAdapter.notifyDataSetChanged();
//                            }
//
//                        }
//                    }
//
//                });
        return view;
    }
}