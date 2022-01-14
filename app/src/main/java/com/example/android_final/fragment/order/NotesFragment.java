package com.example.android_final.fragment.order;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android_final.R;
import com.example.android_final.adapter.NoteAdapter;
import com.example.android_final.data.Note;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class NotesFragment extends Fragment {

    FirebaseFirestore db;
    FirebaseUser firebaseUser;
    List<Note> noteList = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        //get current user;
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        TextView no_note = view.findViewById(R.id.no_note);

        RecyclerView note_recyclerview = view.findViewById(R.id.note_recyclerview);
        note_recyclerview.setHasFixedSize(true);
        NoteAdapter noteAdapter = new NoteAdapter(getActivity(), noteList);
        note_recyclerview.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        note_recyclerview.setAdapter(noteAdapter);

        db = FirebaseFirestore.getInstance();
        db.collection("notes")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value,
                                        @Nullable FirebaseFirestoreException e) {
                        noteList.clear();
                        noteAdapter.notifyDataSetChanged();
                        if (noteList.size()!=0) {
                            no_note.setText("");
                        } else {
                            no_note.setText("You don't have any note!");
                        }
                        if (e != null) {
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("userId").equals(firebaseUser.getUid())) {
                                noteList.add(new Note(doc.getString("title"), doc.getString("body"), doc.getId()));
                                noteAdapter.notifyDataSetChanged();
                                if (noteList.size()!=0) {
                                    no_note.setText("");
                                } else {
                                    no_note.setText("You don't have any note!");
                                }
                            }

                        }
                    }

                });
        return view;
    }
}