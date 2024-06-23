package com.example.jobflow.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.R;
import com.example.jobflow.data.Util;
import com.example.jobflow.model.Notifications;
import com.example.jobflow.model.Workspaces;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder>{
    private List<Notifications> notifications = new ArrayList<>();
    private Context context;

    public NotificationsAdapter(List<Notifications> notifications, Context context) {
        this.notifications = notifications;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_notifications, parent, false);
        return new NotificationsAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notifications notification = notifications.get(position);
        String UID = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE).getString("UID","");
        holder.title.setText(notification.getTitle());
        holder.notification.setText(notification.getNotification());
        holder.timestamp.setText(Util.formatTime(Long.parseLong(notification.getId())));
    }

    @Override
    public int getItemCount() {
        if (notifications != null)
            return notifications.size();
        else
            return 0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title,notification,timestamp;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.titleNotifications);
            notification = itemView.findViewById(R.id.nameNotifications);
            timestamp = itemView.findViewById(R.id.timeNotifications);
        }
    }
}
