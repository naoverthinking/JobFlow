package com.example.jobflow.views.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
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
import com.example.jobflow.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
    private TextView toLogin;
    private EditText edtname,edtemail,edtpassword;
    private Button btnsignup;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitUI();
        toLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
            }
        });
        btnsignup.setOnClickListener(new View.OnClickListener() {
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
                OnClickRegister();
            }
        });
    }
    private void InitUI(){
        toLogin= findViewById(R.id.registertologin);
        edtname = findViewById(R.id.signup_edtname);
        edtemail = findViewById(R.id.signup_edtemail);
        edtpassword = findViewById(R.id.signup_edtpassword);
        btnsignup = findViewById(R.id.signup_btnsignup);
    }
    private void OnClickRegister(){
        String name = edtname.getText().toString().trim();
        String email = edtemail.getText().toString().trim();
        String password = edtpassword.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(getApplicationContext(), "Enter name!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
            return;
        }
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    Toast.makeText(RegisterActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                    if (!task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this, "Authentication failed." + task.getException(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                        String userId = firebaseUser.getUid();
                        User user = new User(userId,name,email,null,null);
                        mDatabase.child("users").child(userId).setValue(user);

                        SharedPreferences sharedPreferences = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("email", email);
                        editor.putString("UID", userId);
                        editor.apply();

                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    }
                }
            });
    }
}