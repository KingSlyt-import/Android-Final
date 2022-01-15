package com.example.android_final.adapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.example.android_final.fragment.AddFragment;
import com.example.android_final.fragment.AlarmFragment;
import com.example.android_final.fragment.HomeFragment;
import com.example.android_final.fragment.ListFragment;
import com.example.android_final.fragment.OptionFragment;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {


    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new HomeFragment();
            case 1:
                return new AddFragment();
            case 2:
                return new AlarmFragment();
            case 3:
                return new OptionFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        String title = "";
        switch (position) {
            case 0:
                title = "Home";
            case 1:
                title = "Add";
            case 2:
                title = "Alarm";
            case 3:
                title = "Option";
        }
        return title;
    }
}