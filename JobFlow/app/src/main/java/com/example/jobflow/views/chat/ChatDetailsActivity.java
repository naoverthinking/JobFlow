package com.example.jobflow.views.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.R;
import com.example.jobflow.adapter.ChatAdapter;
import com.example.jobflow.data.Util;
import com.example.jobflow.model.ChatList;
import com.example.jobflow.model.ChatRoom;
import com.example.jobflow.notification.SendNotification;
import com.example.jobflow.webRTC.repository.MainRepository;
import com.example.jobflow.webRTC.utils.SuccessCallBack;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatDetailsActivity extends AppCompatActivity {
    ImageView btnBackChat;
    ImageButton btnSendChat;
    CircleImageView profilePicChat,videoCallBtnChat;
    MainRepository mainRepository;
    String UID;
    private static final int PERMISSION_REQUEST_CODE = 1011;
    ChatRoom chatRoom;
    TextView nameChat;
    EditText edtChat;
    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
    String chatKey;
    String getUserMobile;
    RecyclerView chattingRecyclerView;
    ChatAdapter chatAdapter;
    List<ChatList> chatLists;
    public static boolean loadingFirstTime= true;
    ImageView btn_back;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_details);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        showProgressBar();
        InitUI();
        EventSendChat();
        databaseReference.child("chat").child(chatKey).child("messenger").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatLists.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        final String messengerTimeStamps = dataSnapshot.getKey();
                        final String getUser =dataSnapshot.child("user").getValue(String.class);
                        final String getMsg = dataSnapshot.child("msg").getValue(String.class);
                        final  String getType = dataSnapshot.child("type").getValue(String.class);
                        Timestamp timestamp = new Timestamp(Long.parseLong(messengerTimeStamps)); // Corrected Timestamp
                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat simpleTimeFormat = new SimpleDateFormat("hh:mm aa");

                        chatLists.add(new ChatList(getMsg,getType,getUser,simpleDateFormat.format(timestamp),simpleTimeFormat.format(timestamp)));
                }
                chatAdapter.updateChat(chatLists);
                chattingRecyclerView.setAdapter(chatAdapter);
                chattingRecyclerView.scrollToPosition(chatLists.size()-1);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void EventSendChat() {
        btnSendChat.setOnClickListener(v -> {
            final String getTxtMessenger = edtChat.getText().toString();
            final String currentTimeStamp= String.valueOf(System.currentTimeMillis());
            ChatList chatList = new ChatList();
            chatList.setId(currentTimeStamp);
            chatList.setUser(String.valueOf(chatRoom.getIdUser().get(0)));
            chatList.setMsg(getTxtMessenger);
            chatList.setType("1");
            databaseReference.child("chat").child(chatKey).child("messenger").child(currentTimeStamp).setValue(chatList);
            databaseReference.child("chatroom").child(chatKey).child("lastmsg").setValue(getTxtMessenger);
            databaseReference.child("chatroom").child(chatKey).child("lastuser").setValue(chatRoom.getIdUser().get(0));
            databaseReference.child("chatroom").child(chatKey).child("lasttime").setValue(Long.parseLong(currentTimeStamp));
            chatAdapter.updateChat(chatLists);
            edtChat.setText("");
            for (String idus : chatRoom.getIdUser()){
                databaseReference.child("users").child(idus).child("FCMToken").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String registrationToken= snapshot.getValue(String.class);
                        String messageTitle = ChatDetailsActivity.this.getString(R.string.new_message);
                        SendNotification sendNotification = new SendNotification(registrationToken,messageTitle,getTxtMessenger, ChatDetailsActivity.this);
                        sendNotification.SendNotificationHi();
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

    }

    private void showProgressBar() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    private void InitUI() {
        videoCallBtnChat = (CircleImageView) findViewById(R.id.callBtnChat);
        chatRoom = (ChatRoom) getIntent().getSerializableExtra("chatRoom");
        btnBackChat = (ImageView) findViewById(R.id.back_buttonChat);
        btnSendChat =(ImageButton) findViewById(R.id.sendBtnChat);
        profilePicChat = (CircleImageView) findViewById(R.id.idProfilePicChat);
        nameChat = (TextView) findViewById(R.id.nameChat);
        edtChat = (EditText) findViewById(R.id.edtChat);
        chattingRecyclerView = (RecyclerView) findViewById(R.id.chatRecyclerView);
        SharedPreferences sharedPreferences = getSharedPreferences("MyProfile", MODE_PRIVATE);
        UID = sharedPreferences.getString("UID", "");
        chatLists = new ArrayList<>();
        chatAdapter = new ChatAdapter(chatLists,this,UID);
        chattingRecyclerView.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        chattingRecyclerView.setLayoutManager(gridLayoutManager);
//        chattingRecyclerView.setLayoutManager(new LinearLayoutManager(ChatDetailsActivity.this));
        chattingRecyclerView.setAdapter(chatAdapter);
        btnBackChat.setOnClickListener(v -> {
            finish();
        });
        nameChat.setText(chatRoom.getChatName() != null ? chatRoom.getChatName() : "User");
        if (chatRoom.getAvt() != null) {
            Picasso.get().load(chatRoom.getAvt()).into(profilePicChat);
        }
        chatKey = chatRoom.getId();
        mainRepository = MainRepository.getInstance(chatKey);
        videoCallBtnChat.setOnClickListener(v -> {
            checkPermissions();
        });
    }
    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    ChatDetailsActivity.this,
                    new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO},
                    PERMISSION_REQUEST_CODE
            );
        } else {
            mainRepository.login(chatKey,UID, this, new SuccessCallBack() {
                @Override
                public void onSuccess() {
                    IntentCall();
                }
            });
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                    grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                mainRepository.login(chatKey,UID, this, new SuccessCallBack() {
                    @Override
                    public void onSuccess() {
                        Intent intent = new Intent(ChatDetailsActivity.this, VideoCallActivity.class);
                        intent.putExtra("target", chatRoom.getIdUser().get(0));
                        startActivity(intent);
                    }
                });
            } else {

            }
        }
    }
    private void IntentCall(){
        Intent intent = new Intent(ChatDetailsActivity.this, VideoCallActivity.class);
        intent.putExtra("target", chatRoom.getIdUser().get(0));
        intent.putExtra("UID", UID);
        intent.putExtra("chatkey", chatKey);
        ProgressDialog progressDialog = new ProgressDialog(ChatDetailsActivity.this);
        progressDialog.setCancelable(false);
        progressDialog.show();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("users").child(UID).child("name");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.getValue(String.class);
//                Util.sendNotification(chatRoom.getIdUser().get(0), "Call", "Bạn có cuộc gọi từ "+name+" --"+chatRoom.getIdUser().get(0)+" --"+chatKey+" --"+UID,ChatDetailsActivity.this);
                Util.sendNotification(chatRoom.getIdUser().get(0), "Call", "Bạn có cuộc gọi từ "+name,ChatDetailsActivity.this,chatRoom.getIdUser().get(0),chatKey,UID);
                startActivity(intent);
                progressDialog.dismiss();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });
    }
}