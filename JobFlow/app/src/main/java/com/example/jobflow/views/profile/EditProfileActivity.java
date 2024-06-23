package com.example.jobflow.views.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.jobflow.MainActivity;
import com.example.jobflow.R;
import com.example.jobflow.data.Util;
import com.example.jobflow.model.User;
import com.example.jobflow.views.login.LoginActivity;
import com.example.jobflow.views.mainfragment.HomeFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.UUID;

public class EditProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_GALLERY = 1222;
    User user;
    TextView btnSave;
    ImageView btnBack,avt;
    boolean isChangeAvt = false;
    Uri filePath;
    String linkToIMGFirebase,password;
    TextView btnLogout, tvThayDoi;
    EditText edtName,edtDisplayName,edtEmail,edtPasss;
    SharedPreferences sharedPreferences;
    CardView btnAddMember;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
    Dialog dialogEdtPass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitUI();
    }

    private void InitUI() {
        btnAddMember = findViewById(R.id.btn_add_member);
        btnSave = findViewById(R.id.btn_pfsave);
        btnBack = findViewById(R.id.btn_pfback);
        edtName = findViewById(R.id.pf_name);
        edtDisplayName = findViewById(R.id.pf_displayname);
        edtEmail = findViewById(R.id.pf_email);
        edtPasss = findViewById(R.id.pf_password);
        btnLogout = findViewById(R.id.btn_logout);
        sharedPreferences = this.getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        String role = sharedPreferences.getString("role", "1");
        String email = sharedPreferences.getString("email", "");
        if (email.equals(Util.ADMIN_EMAIL)){
            btnAddMember.setVisibility(View.VISIBLE);
        } else {
            btnAddMember.setVisibility(View.GONE);
        }

        btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            sharedPreferences = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            String UID = sharedPreferences.getString("UID", "");
            databaseReference.child("users").child(UID).child("FCMToken").setValue("");
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(EditProfileActivity.this, LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
        btnBack.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
        btnAddMember.setOnClickListener(v -> {
            startActivity(new Intent(EditProfileActivity.this, AddMemberActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        });
        user=new User();
        getInfo();
        avt = findViewById(R.id.imageView2);
        avt.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, REQUEST_CODE_GALLERY);
        });
        btnSave.setOnClickListener(v -> {
            if (edtName.getText().toString().isEmpty() ||
                    edtEmail.getText().toString().isEmpty() || edtPasss.getText().toString().isEmpty()) {
                Toast.makeText(this, R.string.toast_complete_information, Toast.LENGTH_SHORT).show();
            }else{
                SaveInfoUser();
            }
        });
        tvThayDoi = findViewById(R.id.tvThayDoi);
        tvThayDoi.setOnClickListener(view -> {
            // Xử lý sự kiện khi nhấn vào password toggle
            showDiaLogEditPasss();
        });
    }
    private void getInfo(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                user = snapshot.getValue(User.class);
                edtName.setText(user.getName());
                edtDisplayName.setText(user.getDisplayName());
                edtEmail.setText(user.getEmail());
                edtPasss.setText(getIntent().getStringExtra("password"));
                if (user.getAvt()!=null){
                    Picasso.get().load(user.getAvt()).into(avt);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void SaveInfoUser() {
        String email = edtEmail.getText().toString();
        password = edtPasss.getText().toString();
        user.setName(edtName.getText().toString());
        user.setDisplayName(edtDisplayName.getText().toString());
        user.setEmail(edtEmail.getText().toString());
        uploadImage(user);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_GALLERY && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(EditProfileActivity.this.getContentResolver(), imageUri);
                avt.setImageBitmap(bitmap);
                filePath = imageUri;
                isChangeAvt = true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void uploadImage(User user) {
        if(isChangeAvt)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            StorageReference ref = storageReference.child("images/"+ UUID.randomUUID().toString());
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    linkToIMGFirebase = uri.toString();
                                    user.setAvt(linkToIMGFirebase);
                                    FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
                                    Toast.makeText(EditProfileActivity.this, R.string.toast_update_success, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }else {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            FirebaseDatabase.getInstance().getReference("users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user);
            if (!password.equals(getIntent().getStringExtra("password"))) {
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                firebaseUser.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        } else {
                        }
                    }
                });
            }
            progressDialog.dismiss();
            Toast.makeText(EditProfileActivity.this, R.string.toast_update_success, Toast.LENGTH_SHORT).show();
        }
    }

    private void showDiaLogEditPasss(){
        dialogEdtPass = new Dialog(this);
        dialogEdtPass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogEdtPass.setContentView(R.layout.dialog_edit_pass);

        EditText old_pass,new_pass,cf_pass;
        old_pass = dialogEdtPass.findViewById(R.id.old_pass);
        new_pass = dialogEdtPass.findViewById(R.id.new_pass);
        cf_pass = dialogEdtPass.findViewById(R.id.cf_pass);
        Button btn_save = dialogEdtPass.findViewById(R.id.savePass);

        btn_save.setOnClickListener(v -> {
            String oldPass = old_pass.getText().toString();
            String newPass = new_pass.getText().toString();
            String cfPass = cf_pass.getText().toString();

            if (oldPass.isEmpty() || newPass.isEmpty() || cfPass.isEmpty()) {
                Toast.makeText(EditProfileActivity.this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(cfPass)) {
                Toast.makeText(EditProfileActivity.this, "Mật khẩu mới không trùng khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            reauthenticateUser(oldPass, newPass, cfPass);
            dialogEdtPass.dismiss();
        });


        dialogEdtPass.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialogEdtPass.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogEdtPass.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialogEdtPass.getWindow().setGravity(Gravity.BOTTOM);
        dialogEdtPass.show();

    }

    private void reauthenticateUser(String password,String newPass,String cfPass) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null && user.getEmail() != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Verifying password...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);
            user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        savePassword(newPass);
                        dialogEdtPass.dismiss();
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(EditProfileActivity.this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }
    private void savePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EditProfileActivity.this, "Password updated", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.e("Update Password", "Error updating password", task.getException());
                        Toast.makeText(EditProfileActivity.this, "Error updating password", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }

}