package com.example.jobflow.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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
import com.example.jobflow.model.Workspaces;

import java.util.ArrayList;
import java.util.List;

public class MemberInProjectAdapter extends RecyclerView.Adapter<MemberInProjectAdapter.ViewHolder> {
    Context context;
    List<User> memberLists = new ArrayList<>();

    public List<User> getMemberLists() {
        return memberLists;
    }

    public void setMemberLists(List<User> memberLists) {
        this.memberLists = memberLists;
    }

    public MemberInProjectAdapter(Context context, List<User> memberLists) {
        this.context = context;
        this.memberLists = memberLists;
    }

    @NonNull
    @Override
    public MemberInProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_project_newmember, parent, false);
        return new MemberInProjectAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MemberInProjectAdapter.ViewHolder holder, int position) {
        User user = memberLists.get(position);
        int pos = position;
        if (user.getWork()==null){
            memberLists.get(position).setWork("Member");
        }
        holder.name.setText(user.getName()) ;

        holder.addTask.setVisibility(View.GONE);
        String[] items = new String[]{ "Member","Leader"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, items);

        SharedPreferences sharedPreferences = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "1");
        holder.dropdown.setAdapter(adapter);
        if (user.getWork().equals("Leader")){
            holder.dropdown.setSelection(1);
        }
        if (!role.equals("0")){
            holder.dropdown.setEnabled(false);
        }
        holder.dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                memberLists.get(pos).setWork(items[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

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
        Spinner dropdown;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.nameMemberInProject);
            dropdown=itemView.findViewById(R.id.nameWorkInProject);
            addTask = itemView.findViewById(R.id.addTaskProject);
        }
    }
}
