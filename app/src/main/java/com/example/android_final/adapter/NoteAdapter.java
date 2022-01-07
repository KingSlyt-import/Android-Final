package com.example.android_final.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.R;
import com.example.android_final.data.Note;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder>{
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    List<Note> notes;
    Context context;
    List<NoteAdapter.ViewHolder> viewHolderList = new ArrayList<>();
    public NoteAdapter(Context context,List<Note> notes) {
        this.notes = notes;
        this.context = context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.note_item,parent,false);
        NoteAdapter.ViewHolder holder = new NoteAdapter.ViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //click item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, holder.note_item_name.getText().toString(), Toast.LENGTH_SHORT).show();
            }
        });

        final Note m = notes.get(position);
        if(m==null)
            return;
        holder.note_item_name.setText(m.getName());
        holder.note_item_text.setText(m.getBody());
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView note_item_name;
        TextView note_item_text;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            note_item_name = itemView.findViewById(R.id.note_item_name);
            note_item_text = itemView.findViewById(R.id.note_item_text);
        }
    }
}
