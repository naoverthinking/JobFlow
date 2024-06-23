package com.example.jobflow.controller.UserController;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.jobflow.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UserController {
    public UserController() {
    }

    public void getUserInfo(String UID, final UserControllerCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users").child(UID);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = new User();
                user.setId(UID);
                if (snapshot.hasChild("name")) {
                    user.setName(snapshot.child("name").getValue(String.class));
                } else {
                    user.setName("N/A");
                }
                if (snapshot.hasChild("avt")) {
                    user.setAvt(snapshot.child("avt").getValue(String.class));
                } else {
                    user.setAvt("N/A");
                }
                if (snapshot.hasChild("background")) {
                    user.setBackground(snapshot.child("background").getValue(String.class));
                } else {
                    user.setBackground("N/A");
                }
                callback.onUserInfoReceived(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.getMessage());
            }
        });
    }

    public interface UserFetchCallback {
        void onUserFetch(List<User> users);
    }

    public void getAllUser(final UserFetchCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user= new User();
                    user.setId(userSnapshot.getKey());
                    users.add(user);
                }
                callback.onUserFetch(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void getAllUserByProject(String wsID,String ID, final UserFetchCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("project").child(wsID).child(ID).child("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                        User user= new User();
                        user.setId(userSnapshot.getKey());
                        user.setWork(userSnapshot.getValue(String.class));
                        users.add(user);
                }
                callback.onUserFetch(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    public void getAllUserByCompany(String ID,final UserFetchCallback callback) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("workspaces").child(ID).child("users");
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> users = new ArrayList<>();
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user= new User();
                    user.setId(userSnapshot.getKey());
                    users.add(user);
                }
                callback.onUserFetch(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
}