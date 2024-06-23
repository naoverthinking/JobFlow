package com.example.jobflow.views.mainfragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jobflow.R;
import com.example.jobflow.adapter.NotificationsAdapter;
import com.example.jobflow.model.Notifications;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.checkerframework.checker.units.qual.N;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link NotificationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NotificationFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    List<Notifications> notificationsList = new ArrayList<>();
    NotificationsAdapter notificationsAdapter;
    SharedPreferences sharedPreferences;

    public NotificationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment NotificationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static NotificationFragment newInstance(String param1, String param2) {
        NotificationFragment fragment = new NotificationFragment();
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
        View view= inflater.inflate(R.layout.fragment_notification, container, false);
        sharedPreferences = getContext().getSharedPreferences("MyProfile",getContext().MODE_PRIVATE);
        RecyclerView recyclerView = view.findViewById(R.id.notification_recycler);
        LoadDataNotifications();
        notificationsAdapter = new NotificationsAdapter(notificationsList,getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(notificationsAdapter);
        return view;
    }
    private void LoadDataNotifications(){
        String UID = sharedPreferences.getString("UID","");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("notifications").child(UID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                notificationsList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Notifications notifications = new Notifications();
                    notifications.setId(dataSnapshot.child("id").getValue(String.class));
                    notifications.setNotification(dataSnapshot.child("notification").getValue(String.class));
                    notifications.setTitle(dataSnapshot.child("title").getValue(String.class));
                    notificationsList.add(notifications);
                }
                Collections.sort(notificationsList, new Comparator<Notifications>() {
                    @Override
                    public int compare(Notifications n1, Notifications n2) {
                        return n2.getId().compareTo(n1.getId());
                    }
                });
                notificationsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}