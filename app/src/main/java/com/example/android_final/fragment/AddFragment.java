package com.example.android_final.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;

import com.example.android_final.R;
import com.example.android_final.adapter.OrderViewPagerAdapter;
import com.example.android_final.addFunction.CreateCategories;
import com.example.android_final.addFunction.CreateNote;
import com.example.android_final.addFunction.CreateSchedule;
import com.example.android_final.addFunction.CreateTask;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

public class AddFragment extends Fragment {

    private FloatingActionButton btn_add;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        // Initiate tab layout
        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.order_view);

        OrderViewPagerAdapter adapter = new OrderViewPagerAdapter(getChildFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        viewPager.setAdapter(adapter);

        tabLayout.setupWithViewPager(viewPager);

        // Button functionality
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