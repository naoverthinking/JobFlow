package com.example.jobflow.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.R;
import com.example.jobflow.model.ChatRoom;
import com.example.jobflow.views.chat.ChatDetailsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatRoomAdapter extends RecyclerView.Adapter<ChatRoomAdapter.ViewHolder> {

    List<ChatRoom> chatRoomList = new ArrayList<>();
    Context context;

    public ChatRoomAdapter(List<ChatRoom> chatRoomList, Context context) {
        this.chatRoomList = chatRoomList;
        this.context = context;
    }

    @NonNull
    @Override
    public ChatRoomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.mess_list_layout, parent, false);
        return new ChatRoomAdapter.ViewHolder(itemView);
    }
    @Override
    public void onBindViewHolder(@NonNull ChatRoomAdapter.ViewHolder holder, int position) {
        ChatRoom list2=  chatRoomList.get(position);
        String UID = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE).getString("UID", "");
        if (list2.getAvt() != null){
            Picasso.get().load(list2.getAvt()).into(holder.profilePic);
        }
        holder.name.setText(list2.getChatName());
        holder.lastMessenger.setText(list2.getLastmsg());
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("chatroom").child(list2.getId()).child("users").child(UID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue(Long.class) < (list2.getLasttime()) ){
                    holder.unseenMessenger.setVisibility(View.VISIBLE);
                    holder.name.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                    holder.lastMessenger.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                } else {
                    holder.unseenMessenger.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
        holder.itemView.setOnClickListener(v -> {
            if (context != null) {
                Intent intent = new Intent(context, ChatDetailsActivity.class);
                intent.putExtra("chatRoom", (Serializable) list2);
                FirebaseDatabase.getInstance().getReference().child("chatroom").child(list2.getId()).child("users").child(UID).setValue(System.currentTimeMillis());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        if (!chatRoomList.isEmpty()){
            return chatRoomList.size();
        }
        return 0;
    }
    public void updateData(List<ChatRoom> chatRoomList){
        Collections.sort(chatRoomList, ChatRoom.lastTimeComparatorDec);
        this.chatRoomList = chatRoomList;
        notifyDataSetChanged();
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView profilePic;
        private TextView name,lastMessenger,unseenMessenger;
        private LinearLayout rootLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = (CircleImageView) itemView.findViewById(R.id.profilePicListMessenger);
            name= (TextView) itemView.findViewById(R.id.nameMessengerList);
            lastMessenger= (TextView) itemView.findViewById(R.id.lassMessengerList);
            unseenMessenger= (TextView) itemView.findViewById(R.id.unseenMessengerList);
            rootLayout = (LinearLayout) itemView.findViewById(R.id.rootLayoutMessengerList);
        }
    }
}
