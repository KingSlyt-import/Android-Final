package com.example.android_final.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android_final.R;
import com.example.android_final.data.Note;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final Note m = notes.get(position);
        if(m==null)
            return;
        holder.note_item_name.setText(m.getName());
        holder.note_item_text.setText(m.getBody());
        //click item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(holder.itemView.getContext(), view);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.edit_delete_menu, popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.edititem:
                                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                                builder.setTitle("Editing note");
                                LayoutInflater inflater = LayoutInflater.from(holder.itemView.getContext());
                                View dialogView = inflater.inflate(R.layout.edit_task, null);
                                builder.setView(dialogView);
                                TextView item_name_ = dialogView.findViewById(R.id.item_name_);
                                TextView item_body_ = dialogView.findViewById(R.id.item_body_);
                                item_name_.setText("Note's name:");
                                item_body_.setText("Note's body:");
                                EditText edit_task_name = dialogView.findViewById(R.id.edit_task_name);
                                EditText edit_task_note = dialogView.findViewById(R.id.edit_task_note);
                                edit_task_name.setText(holder.note_item_name.getText().toString());
                                edit_task_note.setText(holder.note_item_text.getText().toString());
                                builder
                                        .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("title", edit_task_name.getText().toString());
                                                data.put("body", edit_task_note.getText().toString());
                                                db.collection("notes").document(m.getDocument())
                                                        .set(data, SetOptions.merge());
                                            }
                                        })
                                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                            }
                                        });
                                AlertDialog dialog = builder.create();
                                dialog.show();
                                return true;
                            case R.id.deleteitem:
                                db.collection("notes")
                                        .document(m.getDocument())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {

                                            }
                                        });
                                return true;
                        }
                        return false;
                    }
                });
            }
        });


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
