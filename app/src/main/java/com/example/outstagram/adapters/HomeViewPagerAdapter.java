package com.example.outstagram.adapters;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.outstagram.FeedFragment;
import com.example.outstagram.MessageFragment;
import com.example.outstagram.models.Message;

public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    private static final String TAG = "HomeViewPagerAdapter";
    
    
    Context context;
    int totalTabs;

    public HomeViewPagerAdapter(@NonNull FragmentManager fm, int behavior, Context context, int totalTabs) {
        super(fm, behavior);
        this.context = context;
        this.totalTabs = totalTabs;

        Log.d(TAG, "HomeViewPagerAdapter: Called");

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                FeedFragment feedFragment = new FeedFragment();
                return feedFragment;
            case 1:
                MessageFragment messageFragment = new MessageFragment();
                return messageFragment;
            default:
                return new FeedFragment();
        }
    }

    @Override
    public int getCount() {
        return totalTabs;
    }
}
