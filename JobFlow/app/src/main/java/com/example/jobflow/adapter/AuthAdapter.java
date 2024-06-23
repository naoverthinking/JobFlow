package com.example.jobflow.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.R;
import com.example.jobflow.model.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class AuthAdapter extends RecyclerView.Adapter<AuthAdapter.ViewHolder>{
    List<User> userList;
    Context context;

    public AuthAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public AuthAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_details_user_search, parent, false);
        return new AuthAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull AuthAdapter.ViewHolder holder, int position) {
        User user = userList.get(position);
        holder.name.setText(user.getName()+ " ("+user.getDisplayName()+")");
        String[] roles = {"Manager", "Member"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(adapter);
        switch (Integer.parseInt(user.getRole())){
            case 0:
                holder.spinner.setSelection(0);
                break;
            case 1:
                holder.spinner.setSelection(1);
                break;
        }
        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
                mDatabase.child("users").child(user.getId()).child("role").setValue(String.valueOf(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do something when nothing is selected
            }
        });

    }

    @Override
    public int getItemCount() {
        if (userList!=null){
            return userList.size();
        }else {
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        Spinner spinner;
        TextView name,des;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            spinner = itemView.findViewById(R.id.spinnerRole);
            name = itemView.findViewById(R.id.namedts);
        }
    }
}
