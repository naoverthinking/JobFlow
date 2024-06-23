package com.example.jobflow.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.MainActivity;
import com.example.jobflow.R;
import com.example.jobflow.model.Workspaces;
import com.example.jobflow.views.mainfragment.HomeFragment;

import java.util.ArrayList;
import java.util.List;

public class WorkspacesAdapter extends RecyclerView.Adapter<WorkspacesAdapter.ViewHolder> {

    private List<Workspaces> workspacelists = new ArrayList<>();
    private Context context;

    public WorkspacesAdapter(List<Workspaces> workspacelists, Context context) {
        this.workspacelists = workspacelists;
        this.context = context;
    }

    @NonNull
    @Override
    public WorkspacesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_workspaces, parent, false);
        return new WorkspacesAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull WorkspacesAdapter.ViewHolder holder, int position) {
        Workspaces workspaces = workspacelists.get(position);
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        String savedWorkspaceId = sharedPreferences.getString("idWorkspaces", "");

//        if (workspaces.getId().equals(savedWorkspaceId)) {
//            holder.root.setBackgroundColor(Color.DKGRAY);
//            MainActivity.nameWS.setText(workspaces.getName());
//        } else {
//            holder.root.setBackgroundColor(Color.TRANSPARENT);
//        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate()
                        .scaleX(0.95f)
                        .scaleY(0.95f)
                        .setDuration(150)
                        .withEndAction(() -> {
                            v.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(150);
                        });

//                SharedPreferences sharedPreferences = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
//                SharedPreferences.Editor editor = sharedPreferences.edit();
//                editor.putString("idWorkspaces", workspaces.getId());
//                editor.apply();
                notifyDataSetChanged();
                HomeFragment.loadWorkspaceData(context);
            }
        });

        holder.tvname.setText(this.workspacelists.get(position).getName());
        holder.tvdes.setText(this.workspacelists.get(position).getDes());
    }

    public void updateData(List<Workspaces> newData) {
        this.workspacelists.clear();
        this.workspacelists.addAll(newData);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return workspacelists != null ? workspacelists.size() : 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvname, tvdes;
        RelativeLayout root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.nameWorkspaces);
            tvdes = itemView.findViewById(R.id.desWorkspaces);
            root = itemView.findViewById(R.id.workspacesroot);
        }
    }
}
