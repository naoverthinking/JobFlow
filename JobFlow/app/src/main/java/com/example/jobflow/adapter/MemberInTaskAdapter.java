package com.example.jobflow.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.R;
import com.example.jobflow.model.User;

import java.util.ArrayList;
import java.util.List;

public class MemberInTaskAdapter extends RecyclerView.Adapter<MemberInTaskAdapter.ViewHolder> {
    Context context;
    List<User> memberLists = new ArrayList<>();

    public List<User> getMemberLists() {
        return memberLists;
    }

    public void setMemberLists(List<User> memberLists) {
        this.memberLists = memberLists;
    }

    public MemberInTaskAdapter(Context context, List<User> memberLists) {
        this.context = context;
        this.memberLists = memberLists;
    }

    @NonNull
    @Override
    public MemberInTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_task_newmember, parent, false);
        return new MemberInTaskAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberInTaskAdapter.ViewHolder holder, int position) {
        User user = memberLists.get(position);
        int pos = position;
        if (user.getWork()==null){
            memberLists.get(position).setWork("");
        }
        holder.name.setText(user.getName()) ;
        String[] items = new String[]{ "Member","Leader"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "1");
        holder.nameWorkInTask.setText(user.getWork());
        holder.nameWorkInTask.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(android.text.Editable s) {
                memberLists.get(pos).setWork(holder.nameWorkInTask.getText().toString());
            }
        });
    }

    @Override
    public int getItemCount() {
        if (memberLists!=null){
            return memberLists.size();
        }
        return 0;
    }
    public void updateData(List<User> newData) {
        this.memberLists.clear();
        this.memberLists.addAll(newData);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,addTask;
        EditText nameWorkInTask;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameMemberInProject);
            nameWorkInTask=itemView.findViewById(R.id.nameWorkInProject);
            addTask = itemView.findViewById(R.id.addTaskProject);
        }
    }
}

