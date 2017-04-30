package app.hacareem.com.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import app.hacareem.com.R;
import app.hacareem.com.adapters.PagerAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Numair Qadir on 4/30/2017.
 */

public class DashboardFragment extends android.support.v4.app.Fragment {

    @BindView(R.id.tab_layout)
    TabLayout tabLayout;
    @BindView(R.id.pager)
    ViewPager viewPager;

    PagerAdapter adapter;

    public DashboardFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        //Bind views
        ButterKnife.bind(this, view);

        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_pickup)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.tab_drop_off)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new PagerAdapter
                (getFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Added to disable on Swipe Tab Switch
        viewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

        return view;
    }
}