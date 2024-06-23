package com.example.jobflow.views.mainfragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jobflow.R;
import com.example.jobflow.adapter.ChatRoomAdapter;
import com.example.jobflow.model.ChatRoom;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ChatFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChatFragment extends Fragment {

    DatabaseReference mDataMessageUser = FirebaseDatabase.getInstance().getReference().child("chatroom");
    List<ChatRoom> chatRoomList = new ArrayList<>();
    ChatRoomAdapter chatRoomAdapter;
    String UID;
    String email;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ChatFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChatFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChatFragment newInstance(String param1, String param2) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_chat, container, false);

        chatRoomAdapter = new ChatRoomAdapter(chatRoomList,this.getContext());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext());
        RecyclerView recyclerView = view.findViewById(R.id.recycleChat);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(chatRoomAdapter);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        UID = sharedPreferences.getString("UID", "");
        email = sharedPreferences.getString("email", "");


        mDataMessageUser.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChatRoom> chatRoomList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    if (dataSnapshot.child("users").hasChild(UID)) {
                        ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                        if (chatRoom != null) {
                            DataSnapshot usersSnapshot = dataSnapshot.child("users");
                            List<String> userList = new ArrayList<>();
                            for (DataSnapshot userSnapshot : usersSnapshot.getChildren()) {
                                String userId = userSnapshot.getKey();
                                if (userId != null && !userId.equals(UID)) {
                                    userList.add(userId);
                                }
                            }
                            if (!userList.isEmpty()) {
                                chatRoom.setIdUser(userList);
                                DatabaseReference userRef = FirebaseDatabase.getInstance()
                                        .getReference()
                                        .child("users")
                                        .child(userList.get(0));
                                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        String chatName = snapshot.child("name").getValue(String.class);
                                        if (chatName != null) {
                                            chatRoom.setChatName(chatName);
                                            chatRoom.setAvt(snapshot.child("avt").getValue(String.class));
                                            chatRoomAdapter.updateData(chatRoomList);
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                                chatRoomList.add(chatRoom);
                            }
                        }
                    }
                }
                chatRoomAdapter.updateData(chatRoomList);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle error
            }
        });
        return view;
    }
}