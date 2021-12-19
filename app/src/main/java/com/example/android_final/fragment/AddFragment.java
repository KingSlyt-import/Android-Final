package com.example.android_final.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.example.android_final.R;
import com.example.android_final.addFunction.CreateCategories;
import com.example.android_final.addFunction.CreateNote;
import com.example.android_final.addFunction.CreateSchedule;
import com.example.android_final.addFunction.CreateTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private FloatingActionButton btn_add;



    public AddFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddFragment newInstance(String param1, String param2) {
        AddFragment fragment = new AddFragment();
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
        View view = inflater.inflate(R.layout.fragment_add, container, false);
        btn_add = view.findViewById(R.id.fragment_add_note_addBtn);
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(getActivity(), btn_add);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.menu_popup, popup.getMenu());

                //registering popup with OnMenuItemClickListener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {

                        if (item.getTitle().equals("Categories")) {
                            Intent i = new Intent(getActivity(), CreateCategories.class);
                            startActivity(i);
                        }

                        if (item.getTitle().equals("Notes")) {
                            Intent i = new Intent(getActivity(), CreateNote.class);
                            startActivity(i);
                        }

                        if (item.getTitle().equals("Schedules")) {
                            Intent i = new Intent(getActivity(), CreateSchedule.class);
                            startActivity(i);
                        }
//
                        if (item.getTitle().equals("Tasks")) {
                            Intent i = new Intent(getActivity(), CreateTask.class);
                            startActivity(i);
                        }
                        return true;
                    }
                });

                popup.show();
            }
        });
        return view;
    }
}