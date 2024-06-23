package com.example.jobflow.views.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jobflow.MainActivity;
import com.example.jobflow.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private static TextView toRegis;
    private static Button toLogin;
    private EditText edtEmail,edtPassword;
    private FirebaseAuth auth = FirebaseAuth.getInstance();// xac thuc
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> { // set padding for App screen
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitUI();

        toRegis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
            }
        });
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.animate()
                        .scaleX(0.9f) // Giảm kích thước X
                        .scaleY(0.9f) // Giảm kích thước Y
                        .setDuration(150) // Thời gian animation (chậm lại)
                        .withEndAction(() -> {
                            v.animate()
                                    .scaleX(1f) // Trở về kích thước ban đầu X
                                    .scaleY(1f) // Trở về kích thước ban đầu Y
                                    .setDuration(150);
                        });
                OnClickLogin();
            }
        });
    }
    private void InitUI(){
        toRegis = findViewById(R.id.logintoregister);
        toLogin = findViewById(R.id.btnLogin);
        edtEmail = findViewById(R.id.login_email);
        edtPassword = findViewById(R.id.login_password);
    }
    private void OnClickLogin(){

        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.toast_email_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.toast_passwork_empty, Toast.LENGTH_SHORT).show();
            return;
        }
        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), R.string.toast_passwork_error, Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        auth.signInWithEmailAndPassword(email,password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (!task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this,R.string.toast_login_error, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                } else {
                    FirebaseUser currentUser = auth.getCurrentUser();
                    SharedPreferences sharedPreferences = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("email", email);
                    editor.putString("UID", currentUser.getUid());
                    DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(currentUser.getUid());
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild("role")){
                                String role = snapshot.child("role").getValue(String.class);
                                editor.putString("role",role);
                            }else{
                                editor.putString("role","1");
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                    editor.apply();
                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                    progressDialog.dismiss();
                    finish();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}