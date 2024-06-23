package com.example.jobflow.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.example.jobflow.MainActivity;
import com.example.jobflow.R;
import com.example.jobflow.data.Util;
import com.example.jobflow.model.ChatRoom;
import com.example.jobflow.views.chat.ChatDetailsActivity;
import com.example.jobflow.views.chat.VideoCallActivity;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class FCMNotificationService extends FirebaseMessagingService {
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 100;
    String CHANNEL_ID = "NotificationChannelID";
    private String target,chatKey,UID;
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("FCM", "Message Received");
        // Check for notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                Log.e("FCM", "Notification permission not granted");
                return;
            }
        }
        String messageBody = remoteMessage.getNotification().getBody();
        String[] messlist=null;
        if (messageBody.contains("--")) {
            messlist = messageBody.split("--");
            messageBody = messlist[0];
        }
        Map<String, String> data = remoteMessage.getData();
        if (data.size() > 0) {
            Log.e("FCM", "Data received: " + data.toString());
            handleNow(data);
        }
        sendNotification(remoteMessage.getNotification().getTitle(),messageBody,messlist);

        // Retrieve data from the message
//        Map<String, String> data = remoteMessage.getData();
//        Log.d("FCM", "Data received: " + data.toString());
//
//        // Extract title and body
//        String title = data.get("title");
//        String body = data.get("body");
//
//        // Log the title and body for debugging
//        Log.d("FCM", "Title: " + title);
//        Log.d("FCM", "Body: " + body);
//
//        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
//        long[] pattern = {0, 10, 100, 200};
//        if (vibrator != null) {
//            vibrator.vibrate(pattern, -1);
//        }
//
//        // Create a notification builder
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
//                .setSmallIcon(android.R.drawable.ic_dialog_info)
//                .setContentTitle(data.get("title"))
//                .setContentText(data.get("body"))
//                .setAutoCancel(true)
//                .setVibrate(pattern)
//                .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//        // Create the PendingIntent
//        Intent intent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
//        builder.setContentIntent(pendingIntent);
//
//        // Get the NotificationManager
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Create notification channel if needed (for Android 8.0+)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_HIGH);
//            channel.enableLights(true);
//            channel.enableVibration(true);
//            channel.setVibrationPattern(pattern);
//            channel.setBypassDnd(true);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                channel.setAllowBubbles(true);
//            }
//            if (notificationManager != null) {
//                notificationManager.createNotificationChannel(channel);
//            }
//        }
//
//        // Show the notification
//        if (notificationManager != null) {
//            notificationManager.notify(0, builder.build());
//        }
    }



    private void handleNow(Map<String, String> data) {
        target = data.get("target");
        chatKey = data.get("chatKey");
        UID = data.get("UID");
    }

    private void scheduleJob() {

    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
    }

    private Intent createIntent(String actionName, int notificationId, String mission) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setAction(actionName);
        intent.putExtra("NOTIFICATION_ID", notificationId);
        intent.putExtra("MISSION", mission);
        return intent;
    }
    private void sendNotification(String messageTitle,String messageBody,String[] messlist) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        if (chatKey!=null) {
            intent = new Intent(this, VideoCallActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("target", target);
            intent.putExtra("chatkey", chatKey);
            intent.putExtra("type", "notification");
            intent.putExtra("UID", UID);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE
        );
        //ss
        String channelId = CHANNEL_ID;
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_launcher_background)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_background))
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationManager.IMPORTANCE_HIGH)
                        .addAction(new NotificationCompat.Action(
                                android.R.drawable.sym_call_missed,
                                "Cancel",
                                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT | PendingIntent.FLAG_IMMUTABLE)))
                        .addAction(new NotificationCompat.Action(
                                android.R.drawable.sym_call_outgoing,
                                "OK",
                                pendingIntent));
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {0, 10, 100, 200};
        if (vibrator != null) {
            vibrator.vibrate(pattern, -1);
        }
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "FCMNotificationChannel",
                    NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setVibrationPattern(pattern);
            channel.setBypassDnd(true);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                channel.setAllowBubbles(true);
            }
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            };
            notificationManager.createNotificationChannel(channel);
        }
        notificationManager.notify(0, notificationBuilder.build());
    }
}
