package com.example.jobflow.views.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jobflow.R;
import com.example.jobflow.databinding.ActivityVideoCallBinding;
import com.example.jobflow.webRTC.repository.MainRepository;
import com.example.jobflow.webRTC.utils.DataModelType;
import com.example.jobflow.webRTC.utils.SuccessCallBack;

import org.webrtc.SurfaceViewRenderer;

public class VideoCallActivity extends AppCompatActivity implements MainRepository.Listener{
    private MainRepository mainRepository;
    private Boolean isCameraMuted = false;
    private ProgressDialog progressDialog;
    private Boolean isMicrophoneMuted = false;
    private ActivityVideoCallBinding views;
    private boolean doubleBackToExitPressedOnce = false;
    String chatKey,target,UID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        views = ActivityVideoCallBinding.inflate(getLayoutInflater());
        setContentView(views.getRoot());
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    @Override
    public void onBackPressed() {
        if (progressDialog != null && progressDialog.isShowing()) {
            // Show a toast message or handle the back press event when the dialog is showing
            Toast.makeText(this, "Please wait...", Toast.LENGTH_SHORT).show();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    private void init(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
//        progressDialog.setCancelable(false);
        progressDialog.show();
        Bundle b = getIntent().getExtras();
        String type=null;
        chatKey = b.getString("chatkey");
        target = b.getString("UID");
        UID = b.getString("target");
        type = b.getString("type");
//        chatKey = getIntent().getStringExtra("chatkey");
//        target = getIntent().getStringExtra("target");
//        UID = getIntent().getStringExtra("UID");
        mainRepository = MainRepository.getInstance(chatKey);
        if (type!=null){
            mainRepository.login(chatKey,UID, this, new SuccessCallBack() {
                @Override
                public void onSuccess() {
                    setUpCall();
                }
            });
        }else {
            setUpCall();
        }
    }
    private void setUpCall(){
        mainRepository.sendCallRequest(target,()->{
            Toast.makeText(VideoCallActivity.this, "couldnt find the target", Toast.LENGTH_SHORT).show();
        });
        mainRepository.initLocalView(views.localView);
        mainRepository.initRemoteView(views.remoteView);
        mainRepository.listener = (MainRepository.Listener) this;
        mainRepository.subscribeForLatestEvent(data->{
            if (data.getType()== DataModelType.StartCall){
                runOnUiThread(()->{
                    views.incomingNameTV.setText(data.getSender()+" is Calling you");
                    views.incomingCallLayout.setVisibility(View.VISIBLE);
                    mainRepository.startCall(data.getSender());
                    views.incomingCallLayout.setVisibility(View.GONE);
                });
            }
        });
        views.switchCameraButton.setOnClickListener(v->{
            mainRepository.switchCamera();
        });
        views.micButton.setOnClickListener(v->{
            if (isMicrophoneMuted){
                views.micButton.setImageResource(R.drawable.ic_baseline_mic_off_24);
            }else {
                views.micButton.setImageResource(R.drawable.ic_baseline_mic_24);
            }
            mainRepository.toggleAudio(isMicrophoneMuted);
            isMicrophoneMuted=!isMicrophoneMuted;
        });

        views.videoButton.setOnClickListener(v->{
            if (isCameraMuted){
                views.videoButton.setImageResource(R.drawable.ic_baseline_videocam_off_24);
            }else {
                views.videoButton.setImageResource(R.drawable.ic_baseline_videocam_24);
            }
            mainRepository.toggleVideo(isCameraMuted);
            isCameraMuted=!isCameraMuted;
        });

        views.endCallButton.setOnClickListener(v->{
            mainRepository.endCall();
            finish();
        });
    }

    @Override
    public void webrtcConnected() {
        runOnUiThread(()->{
            progressDialog.dismiss();
            views.incomingCallLayout.setVisibility(View.GONE);
            views.whoToCallLayout.setVisibility(View.GONE);
            views.callLayout.setVisibility(View.VISIBLE);
        });
    }

    @Override
    public void webrtcClosed() {
        runOnUiThread(this::finish);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainRepository.endCall();
    }
}