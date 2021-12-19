package com.example.android_final.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.android_final.fragment.AddFragment;
import com.example.android_final.fragment.HomeFragment;
import com.example.android_final.fragment.ListFragment;
import com.example.android_final.fragment.OptionFragment;
import com.example.android_final.fragment.order.NotesFragment;
import com.example.android_final.fragment.order.SchedulesFragment;
import com.example.android_final.fragment.order.TasksFragment;

public class OrderViewPagerAdapter extends FragmentStatePagerAdapter {


    public OrderViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new NotesFragment();
            case 1:
                return new SchedulesFragment();
            case 2:
                return new TasksFragment();
        }
        return new SchedulesFragment();
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return  "Notes";
            case 1:
                return "Schedules";
            case 2:
                return "Tasks";
        }
        return null;
    }
}