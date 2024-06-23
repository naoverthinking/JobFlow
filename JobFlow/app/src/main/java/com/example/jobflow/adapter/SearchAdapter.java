package com.example.jobflow.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.R;
import com.example.jobflow.model.ChatRoom;
import com.example.jobflow.model.Project;
import com.example.jobflow.model.User;
import com.example.jobflow.views.chat.ChatDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.List;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder>{
    private static final int VIEW_TYPE_PROJECTS = 0;
    private static final int VIEW_TYPE_USERS = 1;
    List<Project> projectList;
    List<User> userList;
    Context context;
    String UID;
    boolean ktra = false;

    public SearchAdapter(List<Project> projectList, List<User> userList, Context context) {
        this.projectList = projectList;
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public SearchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_details_project_search, parent, false);
            return new SearchAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchAdapter.ViewHolder holder, int position) {
        if (projectList!=null && userList!=null){
            if (position<projectList.size()){
                holder.name.setText(projectList.get(position).getName());
                holder.des.setText(projectList.get(position).getDes());
                holder.btn_Chat.setVisibility(View.GONE);
            }else{
                holder.name.setText(userList.get(position-projectList.size()).getName());
                holder.des.setText(userList.get(position-projectList.size()).getEmail());

                holder.btn_Chat.setVisibility(View.VISIBLE);
                holder.btn_Chat.setOnClickListener(v -> {
                    intentChatDetails(userList.get(position-projectList.size()));
                });
            }
        }else if (projectList!=null){
            holder.name.setText(projectList.get(position).getName());
            holder.des.setText(projectList.get(position).getDes());
            holder.btn_Chat.setVisibility(View.GONE);
        }else if (userList!=null){
            holder.name.setText(userList.get(position).getName());
            holder.des.setText(userList.get(position).getEmail());
            holder.btn_Chat.setVisibility(View.VISIBLE);
            holder.btn_Chat.setOnClickListener(v -> {
               intentChatDetails(userList.get(position));
            });
        }

    }

    @Override
    public int getItemCount() {
        if (projectList!=null && userList!=null){
            return projectList.size() + userList.size();
        }else if (projectList!=null){
            return projectList.size();
        }else if (userList!=null){
            return userList.size();
        }else{
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView name,des;
        RelativeLayout btn_Chat;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.namedts);
            des = itemView.findViewById(R.id.desdts);
            btn_Chat = itemView.findViewById(R.id.btn_Chat);
        }
    }
    public void intentChatDetails(User user){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("chatroom").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    UID = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE).getString("UID", "");
                    if (dataSnapshot.child("users").hasChild(user.getId())
                            && dataSnapshot.child("users").hasChild(UID)
                            && dataSnapshot.child("users").getChildrenCount()==2) {
                        ktra=true;
                        ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                        chatRoom.setChatName(user.getName());
                        chatRoom.setAvt(user.getAvt());
                        chatRoom.getIdUser().add(user.getId());
                        Intent intent = new Intent(context, ChatDetailsActivity.class);
                        intent.putExtra("chatRoom", (Serializable) chatRoom);
                        FirebaseDatabase.getInstance().getReference().child("chatroom").child(chatRoom.getId()).child("users").child(UID).setValue(System.currentTimeMillis());
                        context.startActivity(intent);
                        break;
                    }
                }
                if (!ktra){
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                    String key = databaseReference.child("chatroom").push().getKey();
                    ChatRoom chatRoom = new ChatRoom(key, null, "1", null, null);
                    databaseReference.child("chatroom").child(key).setValue(chatRoom);
                    databaseReference.child("chatroom").child(key).child("users").child(UID).setValue(System.currentTimeMillis());
                    databaseReference.child("chatroom").child(key).child("users").child(user.getId()).setValue(System.currentTimeMillis());
                    chatRoom.setChatName(user.getName());
                    chatRoom.setAvt(user.getAvt());
                    chatRoom.getIdUser().add(user.getId());
                    Intent intent = new Intent(context, ChatDetailsActivity.class);
                    intent.putExtra("chatRoom", (Serializable) chatRoom);
                    context.startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public void updateData(List<Project> projectList, List<User> userList){
        this.projectList = projectList;
        this.userList = userList;
        notifyDataSetChanged();
    }
    public void updateDataProject(List<Project> projectList){
        this.projectList = projectList;
        notifyDataSetChanged();
    }
    public void updateDataUser(List<User> userList){
        this.userList = userList;
        notifyDataSetChanged();
    }
}
