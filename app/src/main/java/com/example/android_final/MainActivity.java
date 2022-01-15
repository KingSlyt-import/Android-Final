package com.example.android_final;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.android_final.adapter.ViewPagerAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
//    private TabLayout tab_bar;
    private ViewPager view_pager;
    private BottomNavigationView bottomNavigationView;
    FirebaseUser firebaseUser;
    DatabaseReference reference;
    String destination;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        Bundle extras = getIntent().getExtras();

        bottomNavigationView = findViewById(R.id.bottom_bar);
        view_pager = findViewById(R.id.view_pager);

        ViewPagerAdapter vpa = new ViewPagerAdapter(getSupportFragmentManager(), FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        view_pager.setAdapter(vpa);

        //change to destination when click notification

        if (extras!=null) {
            destination = extras.getString("des");
            if (destination.equals("schedule")) {
                bottomNavigationView.getMenu().findItem(R.id.menu_add).setChecked(true);
                view_pager.setCurrentItem(1);
            }
            if (destination.equals("alarm")) {
                bottomNavigationView.getMenu().findItem(R.id.menu_alarm).setChecked(true);
                view_pager.setCurrentItem(2);
            }
        }

        //user session
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance("https://android-final-a8b4a-default-rtdb.firebaseio.com/")
                .getReference("Users").child(firebaseUser.getUid());
        if (firebaseUser != null) {
            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
        //show which page you are in in bottom bar
        view_pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        bottomNavigationView.getMenu().findItem(R.id.menu_home).setChecked(true);
                        break;
                    case 1:
                        bottomNavigationView.getMenu().findItem(R.id.menu_add).setChecked(true);
                        break;
                    case 2:
                        bottomNavigationView.getMenu().findItem(R.id.menu_alarm).setChecked(true);
                        break;
                    case 3:
                        bottomNavigationView.getMenu().findItem(R.id.menu_option).setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        //change tab when select menu items
        bottomNavigationView.setOnItemSelectedListener(item -> {
            if (item.getTitle().equals("Home")) {
                view_pager.setCurrentItem(0);
            }
            if (item.getTitle().equals("Add")) {
                view_pager.setCurrentItem(1);
            }
            if (item.getTitle().equals("Alarm")) {
                view_pager.setCurrentItem(2);
            }
            if (item.getTitle().equals("Options")) {
                view_pager.setCurrentItem(4);
            }
            return false;
        });

        // Check default app theme

    }


}