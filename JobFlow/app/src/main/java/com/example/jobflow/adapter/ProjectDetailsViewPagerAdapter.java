package com.example.jobflow.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.jobflow.views.mainfragment.HomeFragment;
import com.example.jobflow.views.task.ProjectDetailsFragment;

public class ProjectDetailsViewPagerAdapter extends FragmentStateAdapter {

    public ProjectDetailsViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
            case 1:
            case 2:
            case 3:
            case 4:
                return new ProjectDetailsFragment();
            default:
                return new ProjectDetailsFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }


}
