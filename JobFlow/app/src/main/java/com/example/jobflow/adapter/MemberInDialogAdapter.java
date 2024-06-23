package com.example.jobflow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.R;

import java.util.ArrayList;
import java.util.List;

public class MemberInDialogAdapter extends RecyclerView.Adapter<MemberInDialogAdapter.ViewHolder>{
    private List<CharSequence> originalList;
    private List<CharSequence> filteredList;
    Context context;

    public MemberInDialogAdapter(Context context, List<CharSequence> objects) {
        this.context=context;
        this.originalList = new ArrayList<>(objects);
        this.filteredList = new ArrayList<>(objects);
    }

    @NonNull
    @Override
    public MemberInDialogAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dialog_with_search, parent, false);
        return new MemberInDialogAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberInDialogAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return filteredList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
    public void filter(CharSequence constraint) {
        filteredList.clear();
        if (constraint == null || constraint.length() == 0) {
            filteredList.addAll(originalList);
        } else {
            String filterPattern = constraint.toString().toLowerCase().trim();
            for (CharSequence item : originalList) {
                if (item.toString().toLowerCase().contains(filterPattern)) {
                    filteredList.add(item);
                }
            }
        }
        notifyDataSetChanged();
    }
}
