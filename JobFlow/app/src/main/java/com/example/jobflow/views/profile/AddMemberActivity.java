package com.example.jobflow.views.profile;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.service.autofill.SaveInfo;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.MainActivity;
import com.example.jobflow.R;
import com.example.jobflow.adapter.AuthAdapter;
import com.example.jobflow.model.User;
import com.example.jobflow.views.login.RegisterActivity;
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

import java.util.ArrayList;
import java.util.List;

public class AddMemberActivity extends AppCompatActivity {
    TextView btnSave;
    ImageView btnBack;
    EditText edtName,edtDisplayName,edtEmail,edtPasss,edtCfPass,edtSearch;
    List<User> userList;
    AuthAdapter authAdapter;
    RecyclerView recyclerView;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_member);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitUI();
    }

    private void InitUI() {
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_pfback);
        edtName = findViewById(R.id.pf_name);
        edtDisplayName = findViewById(R.id.pf_displayname);
        edtEmail = findViewById(R.id.pf_email);
        edtPasss = findViewById(R.id.pf_password);
        edtCfPass = findViewById(R.id.pf_cfpassword);
        edtSearch = findViewById(R.id.edt_search);
        recyclerView = findViewById(R.id.recycler_view);


        userList = new ArrayList<>();
        authAdapter = new AuthAdapter(userList, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(authAdapter);

        setUpData(null);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                setUpData(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnSave.setOnClickListener(v -> {
            saveInfo();
        });
        btnBack.setOnClickListener(v -> {
            finish();
        });
        edtCfPass.setText("abc123");
        edtPasss.setText("abc123");
        edtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s!=null && s.length()>0){
                    String[] nameParts = s.toString().trim().split(" ");
                    String displayName = nameParts[nameParts.length - 1].substring(0,1).toUpperCase()+ nameParts[nameParts.length - 1].substring(1) + "_"; // Last name in uppercase

                    for (int i = 0; i < nameParts.length - 1; i++) { // Loop through first and middle names
                        displayName += nameParts[i].substring(0, 1).toUpperCase(); // Add first letter of each name part
                    }
                    edtDisplayName.setText(displayName);
                }else{
                    edtDisplayName.setText("");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setUpData(String s) {
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (s == null || s.isEmpty()) {
                    userList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        userList.add(user);
                    }
                    authAdapter.notifyDataSetChanged();
                    return;
                } else {
                    userList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user.getName().toLowerCase().contains(s.toLowerCase())
                                || (user.getEmail()!=null && user.getEmail().toLowerCase().contains(s.toLowerCase()))
                                || (user.getDisplayName()!=null && user.getDisplayName().toLowerCase().contains(s.toLowerCase())))
                            userList.add(user);
                    }
                    authAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void saveInfo() {
        String name = edtName.getText().toString().trim();
        String displayName = edtDisplayName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String pass = edtPasss.getText().toString().trim();
        String cfPass = edtCfPass.getText().toString().trim();
        if (name.isEmpty()){
            edtName.setError("Name is required");
            edtName.requestFocus();
            return;
        }
        if (displayName.isEmpty()){
            edtDisplayName.setError("Display name is required");
            edtDisplayName.requestFocus();
            return;
        }
        if (email.isEmpty()){
            edtEmail.setError("Email is required");
            edtEmail.requestFocus();
            return;
        }
        if (pass.isEmpty()){
            edtPasss.setError("Password is required");
            edtPasss.requestFocus();
            return;
        }
        if (cfPass.isEmpty()){
            edtCfPass.setError("Confirm password is required");
            edtCfPass.requestFocus();
            return;
        }
        if (!pass.equals(cfPass)){
            edtCfPass.setError("Password not match");
            edtCfPass.requestFocus();
            return;
        }
        // Save info to database
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email,pass)
                .addOnCompleteListener(AddMemberActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Toast.makeText(AddMemberActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                        if (!task.isSuccessful()) {
                            Toast.makeText(AddMemberActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        } else {
                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                            String userId = firebaseUser.getUid();
                            User user = new User(userId,name,email,null,null);
                            user.setRole("1");
                            user.setDisplayName(displayName);
                            mDatabase.child("users").child(userId).setValue(user);
                            progressDialog.dismiss();
                            Toast.makeText(AddMemberActivity.this, "Add member successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}