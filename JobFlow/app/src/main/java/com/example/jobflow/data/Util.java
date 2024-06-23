package com.example.jobflow.data;

import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.jobflow.MainActivity;
import com.example.jobflow.R;
import com.example.jobflow.model.Notifications;
import com.example.jobflow.model.Project;
import com.example.jobflow.notification.MyBroadCastReceiver;
import com.example.jobflow.notification.SendNotification;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Util {
    public static final String ADMIN_EMAIL = "vulena@gmail.com";
    public static String  formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date resultDate = new Date(timeInMillis);
        return sdf.format(resultDate);
    }
    public static long convertToTimestamp(String datetime) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date date = dateFormat.parse(datetime);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
    }
    public static void createNotificationChannel(CharSequence name,String description,String channelID,Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
    public static void createNotificationBuilder(String title,String mes,String channelID,Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelID)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(title)
                .setContentText(mes)
                .setAutoCancel(true)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManagerCompat.notify(123, builder.build());
        }
    }
    public static Project project;
    private void setMultipleAlarms(Context context,String timeE) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(context, MyBroadCastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);

        // Parse the timeE string to a Date object
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        try {
            Date date = dateFormat.parse(timeE);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            // Subtract 10 minutes
            calendar.add(Calendar.DATE, -1);
            // Set the alarm
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    public static void sendNotification(String suid,String title,String msg,Context context){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
                databaseReference.child("users").child(suid).child("FCMToken").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String registrationToken= snapshot.getValue(String.class);
                        String messageTitle = context.getString(R.string.new_notification);
                        String messageBody = "Nội dung thông báo";
                        SendNotification sendNotification = new SendNotification(registrationToken,title,msg, context);
                        Notifications notifications = new Notifications();
                        final String currentTimeStamp= String.valueOf(System.currentTimeMillis());
                        notifications.setId(currentTimeStamp.toString());
                        notifications.setTitle(title);
                        notifications.setNotification(msg);
                        FirebaseDatabase.getInstance().getReference().child("notifications").child(suid).child(currentTimeStamp).setValue(notifications);
                        sendNotification.SendNotificationHi();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        },3000);
    }
    public static void sendNotification(String suid,String title,String msg,Context context,String target,String chatKey,String UID){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
                databaseReference.child("users").child(suid).child("FCMToken").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String registrationToken= snapshot.getValue(String.class);
                        String messageTitle = context.getString(R.string.new_notification);
                        String messageBody = "Nội dung thông báo";
                        SendNotification sendNotification = new SendNotification(registrationToken,title,msg, context);
                        sendNotification.SendNotificationHi(target,chatKey,UID);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        },3000);
    }
}
