package com.example.jobflow.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.jobflow.views.task.ProjectDetailsFragment;
import com.example.jobflow.views.task.TaskSubFragment;

public class TaskSubVPAdapter extends FragmentStateAdapter {
    public TaskSubVPAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            default:
                return new TaskSubFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
