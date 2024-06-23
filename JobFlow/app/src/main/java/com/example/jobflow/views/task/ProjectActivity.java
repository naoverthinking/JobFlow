package com.example.jobflow.views.task;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.MainActivity;
import com.example.jobflow.R;
import com.example.jobflow.adapter.MemberInDialogAdapter;
import com.example.jobflow.adapter.MemberInProjectAdapter;
import com.example.jobflow.controller.UserController.UserController;
import com.example.jobflow.data.Util;
import com.example.jobflow.model.Notifications;
import com.example.jobflow.model.Project;
import com.example.jobflow.model.User;
import com.example.jobflow.notification.SendNotification;
import com.example.jobflow.views.mainfragment.HomeFragment;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectActivity extends AppCompatActivity {

    MemberInProjectAdapter memberInProjectAdapter;
    String UID;
    Project project;
    String role;
    SharedPreferences sharedPreferences;
    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    String wsID;
    Uri filePath;
    String linkToIMGFirebase;
    EditText nameProject,desProject;
    private TextView tvStartDateTime, tvEndDateTime;
    private SimpleDateFormat dateTimeFormat;
    UserController userController = new UserController();
    TextView btn_addProject;
    TextView tv_addMember,tv_namePrj,tv_desPrj;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
    List<User> memberList = new ArrayList<>();
    private Calendar startCalendar, endCalendar;

    ImageView btnChooseIMG,btn_back_main,btn_project_grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_project);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitUI();
    }

    private void InitUI() {
        sharedPreferences = getSharedPreferences("MyProfile",MODE_PRIVATE);
        memberInProjectAdapter = new MemberInProjectAdapter(this,memberList);
        tv_addMember = findViewById(R.id.tv_addMember);
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        int numberOfColumns = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(memberInProjectAdapter);

        btnChooseIMG = findViewById(R.id.imageView2);
        btn_addProject = findViewById(R.id.btn_addProject);
        tvStartDateTime = findViewById(R.id.tvStartDateTime);
        tvEndDateTime = findViewById(R.id.tvEndDateTime);
        nameProject = findViewById(R.id.editText);
        desProject = findViewById(R.id.editText2);
        tv_namePrj = findViewById(R.id.tv_namePrj);
        tv_desPrj = findViewById(R.id.tv_desPrj);
        btn_back_main = findViewById(R.id.btn_back_main);
        btn_project_grid = findViewById(R.id.btn_project_grid);
        LinearLayout btnStartDateTime = findViewById(R.id.btnStartDateTime);
        LinearLayout btnEndDateTime = findViewById(R.id.btnEndDateTime);


        project = (Project) getIntent().getSerializableExtra("project");

        UID = sharedPreferences.getString("UID","");
        role = sharedPreferences.getString("role","1");

        if (!role.equals("0")){
            nameProject.setEnabled(false);
            desProject.setEnabled(false);
            btnChooseIMG.setEnabled(false);
            btnStartDateTime.setEnabled(false);
            btnEndDateTime.setEnabled(false);
        }

        if (!role.equals("0") && FirebaseDatabase.getInstance().getReference().child("project").child("0").child(project.getId()).child("users").child(UID)!=null){
            FirebaseDatabase.getInstance().getReference().child("project").child("0").child(project.getId()).child("users").child(UID).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String role = snapshot.getValue(String.class);
                    if (role.equals("Leader")){
                        tv_addMember.setVisibility(View.VISIBLE);
                    }else{
                        tv_addMember.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        nameProject.setText(project.getName());
        desProject.setText(project.getDes());
        tv_namePrj.setText(project.getName());
        tv_desPrj.setText(project.getDes());
        tvStartDateTime.setText(Util.formatTime(Long.parseLong(project.getTimeS())));
        tvEndDateTime.setText(Util.formatTime(Long.parseLong(project.getTimeE())));
        if (project.getImg()!=null){
            Picasso.get().load(project.getImg()).into(btnChooseIMG);
        }
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("project").child("0").child(project.getId()).child("users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> memberList1 = new ArrayList<>();
                for (DataSnapshot snapshot1:snapshot.getChildren()){
                    User user = new User();
                    user.setId(snapshot1.getKey());
                    user.setWork(snapshot1.getValue(String.class));
                    memberList1.add(user);
                }
                int numUsers = memberList1.size();
                AtomicInteger counter = new AtomicInteger(0);
                for (int i=0;i<=numUsers-1;i++) {
                    String uid = memberList1.get(i).getId().trim();
                    int j=i;
                    usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            String name = snapshot.child("name").getValue(String.class);
                            String email = snapshot.child("email").getValue(String.class);
                            memberList1.get(j).setName(name);
                            memberList1.get(j).setEmail(email);
                            if (counter.incrementAndGet() == numUsers) {
                                memberInProjectAdapter.updateData(memberList1);
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        btnChooseIMG.setOnClickListener(v -> {
            openGallery();
        });
        btn_addProject.setOnClickListener(v -> {
            SaveDataProject();
        });
        btn_project_grid.setOnClickListener(v -> {
            Intent intent= new Intent(this,DetailsProjectActivity.class);
            intent.putExtra("project",project);
            Util.project=project;
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });
        btn_back_main.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
//            finish();
        });
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        btnStartDateTime.setOnClickListener(v -> showDateTimePicker(true));
        btnEndDateTime.setOnClickListener(v -> showDateTimePicker(false));
        tv_addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userController.getAllUser(new UserController.UserFetchCallback() {
                    @Override
                    public void onUserFetch(List<User> users) {
                        showUserSelectionDialog(users,memberInProjectAdapter.getMemberLists());
                    }
                });
            }
        });
    }
    public void showUserSelectionDialog(final List<User> users, final List<User> users1) {
        List<String> userArray1 = new ArrayList<>();
        final boolean[] checkedItems = new boolean[users.size()];
        int numUsers = users.size();
        AtomicInteger counter = new AtomicInteger(0);
        for (User user : users) {
            String uid = user.getId().trim();
            usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String name = snapshot.child("name").getValue(String.class);
                    String email = snapshot.child("email").getValue(String.class);
                    String txt_dname = snapshot.child("displayName").getValue(String.class) == null ? "" : snapshot.child("displayName").getValue(String.class);
                    if (!txt_dname.isEmpty()) {
                        email = txt_dname;
                    }
                    String displayName = name + " (" + email + ")";
                    if (snapshot.child("id").getValue(String.class).equals(getSharedPreferences("MyProfile", MODE_PRIVATE).getString("UID", "").trim())) {
                        displayName = name + " (You)";
                    }
                    user.setName(name);
                    user.setEmail(email);
                    userArray1.add(displayName);
                    if (counter.incrementAndGet() == numUsers) {
                        showDialog(userArray1, users, users1, checkedItems);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });
        }
    }

    private void showDialog1(List<String> userArray1, List<User> users, List<User> users1, boolean[] checkedItems) {
        final CharSequence[] userArray = userArray1.toArray(new CharSequence[userArray1.size()]);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            boolean isChecked = false;
            for (User userComp : users1) {
                if (userComp.getWork()!=null){
                    user.setWork(userComp.getWork());
                }
                if (userComp != null && user.getId() != null && userComp.getId() != null && user.getId().trim().equals(userComp.getId().trim())) {
                    isChecked = true;
                    break;
                }
            }
            checkedItems[i] = isChecked;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_select_users)
                .setMultiChoiceItems(userArray, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    }
                })
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<User> users2 = new ArrayList<>();
                        for (int i = 0; i < checkedItems.length; i++) {
                            boolean isChecked = checkedItems[i];
                            if (isChecked) {
                                users2.add(users.get(i));
                            }
                        }
                        memberInProjectAdapter.updateData(users2);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.create().show();
    }
    public void showDialog(List<String> userArray1, List<User> users, List<User> users1, boolean[] checkedItems) {
        final List<CharSequence> userList = new ArrayList<>(userArray1);
        final List<CharSequence> filteredUserList = new ArrayList<>(userList);
        final List<Integer> filteredIndices = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            filteredIndices.add(i);
            User user = users.get(i);
            boolean isChecked = false;
            for (User userComp : users1) {
                if (userComp.getWork()!=null){
                    user.setWork(userComp.getWork());
                }
                if (userComp != null && user.getId() != null && userComp.getId() != null && user.getId().trim().equals(userComp.getId().trim())) {
                    isChecked = true;
                    break;
                }
            }
            checkedItems[i] = isChecked;
        }
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_with_search, null);

        EditText searchEditText = dialogView.findViewById(R.id.search_edit_text);
        ListView usersListView = dialogView.findViewById(R.id.users_list_view);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_multiple_choice, filteredUserList);
        usersListView.setAdapter(adapter);
        usersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        for (int i = 0; i < userList.size(); i++) {
            usersListView.setItemChecked(i, checkedItems[i]);
        }
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filteredUserList.clear();
                filteredIndices.clear();
                for (int i = 0; i < userList.size(); i++) {
                    CharSequence userString = userList.get(i);
                    if (userString.toString().toLowerCase().contains(s.toString().toLowerCase())) {
                        filteredUserList.add(userString);
                        filteredIndices.add(i);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.text_select_users)
                .setView(dialogView)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<User> selectedUsers = new ArrayList<>();
                        for (int i = 0; i < checkedItems.length; i++) {
                            boolean isChecked = checkedItems[i];
                            if (isChecked) {
                                selectedUsers.add(users.get(i));
                            }
                        }
                        for (int i = 0; i < filteredUserList.size(); i++) {
                            if (usersListView.isItemChecked(i)) {
                                int originalIndex = filteredIndices.get(i);
                                if (!selectedUsers.contains(users.get(originalIndex))) {
                                    selectedUsers.add(users.get(originalIndex));
                                }
                            }
                            if (!usersListView.isItemChecked(i)) {
                                int originalIndex = filteredIndices.get(i);
                                if (selectedUsers.contains(users.get(originalIndex))) {
                                    selectedUsers.remove(users.get(originalIndex));
                                }
                            }
                        }
                        memberInProjectAdapter.updateData(selectedUsers);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void showDateTimePicker(boolean isStart) {
        Calendar calendar = isStart ? startCalendar : endCalendar;
        Calendar now = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                if (calendar.before(now)) {
                    Toast.makeText(this, R.string.toast_date_time_err, Toast.LENGTH_SHORT).show();
                } else {
                    if (isStart) {
                        updateLabel(true);
                    } else {
                        if (startCalendar == null || startCalendar.getTimeInMillis() == 0) {
                            startCalendar = (Calendar) now.clone();
                            updateLabel(true);
                        }
                        if (endCalendar.before(startCalendar)) {
                            Toast.makeText(this, R.string.toast_date_time_err1, Toast.LENGTH_SHORT).show();
                        } else {
                            updateLabel(false);
                        }
                    }
                }
            }, 0, 0, true);

            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(now.getTimeInMillis());
        datePickerDialog.show();
    }

    private void updateLabel(boolean isStart) {
        if (isStart) {
            tvStartDateTime.setText(dateTimeFormat.format(startCalendar.getTime()));
        } else {
            tvEndDateTime.setText(dateTimeFormat.format(endCalendar.getTime()));
        }
    }
    private void uploadImage(DatabaseReference databaseReference) {
        if(filePath != null)
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
                                    String key = databaseReference.getKey();
                                    databaseReference.child("id").setValue(key);
                                    databaseReference.child("timeS").setValue(Util.convertToTimestamp(tvStartDateTime.getText().toString())+"");
                                    databaseReference.child("timeE").setValue(Util.convertToTimestamp(tvEndDateTime.getText().toString())+"");
                                    databaseReference.child("name").setValue(nameProject.getText().toString());
                                    databaseReference.child("des").setValue(desProject.getText().toString());
                                    databaseReference.child("img").setValue(linkToIMGFirebase);
                                    databaseReference.child("users").removeValue();
                                    for (User user:memberList){
                                        databaseReference.child("users").child(user.getId()).setValue(""+user.getWork());
                                        sendNotification(user.getId(),"Dự án "+nameProject.getText().toString()+" đã có một số chỉnh sửa");
                                    }
                                    HomeFragment.loadWorkspaceData(ProjectActivity.this);
                                }
                            });
                            Toast.makeText(ProjectActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(ProjectActivity.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();
            String key = databaseReference.getKey();
            databaseReference.child("id").setValue(key);
            databaseReference.child("timeS").setValue(String.valueOf(Util.convertToTimestamp(tvStartDateTime.getText().toString())));
            databaseReference.child("timeE").setValue(String.valueOf(Util.convertToTimestamp(tvEndDateTime.getText().toString())));
            databaseReference.child("name").setValue(nameProject.getText().toString());
            databaseReference.child("des").setValue(desProject.getText().toString());
            databaseReference.child("img").setValue(linkToIMGFirebase);
            databaseReference.child("users").removeValue();
            for (User user:memberList){
                databaseReference.child("users").child(user.getId()).setValue(user.getWork());
                sendNotification(user.getId(),"Dự án "+nameProject.getText().toString()+" đã có một số chỉnh sửa");
            }
            HomeFragment.loadWorkspaceData(ProjectActivity.this);
            Toast.makeText(ProjectActivity.this, "Success", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }
    }
    public void SaveDataProject(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("project");
        if (!tvStartDateTime.getText().equals("Start time")
                && !tvEndDateTime.getText().equals("End time") && nameProject.getText()!=null && desProject.getText()!=null){
            boolean kt= true;
            if(memberList.size()==0){
                kt=false;
            }
            if (kt){
                databaseReference=databaseReference.child("0").child(project.getId());
                uploadImage(databaseReference);
            }else{
                Toast.makeText(this,R.string.toast_complete_information,Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,R.string.toast_complete_information,Toast.LENGTH_SHORT).show();
        }
    }

    public void sendNotification(String suid,String msg){
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                DatabaseReference databaseReference= FirebaseDatabase.getInstance().getReference();
                databaseReference.child("users").child(suid).child("FCMToken").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String registrationToken= snapshot.getValue(String.class);
                        String messageTitle = ProjectActivity.this.getString(R.string.new_notification);
                        String messageBody = "Nội dung thông báo";
                        SendNotification sendNotification = new SendNotification(registrationToken,messageTitle,msg, ProjectActivity.this);
                        Notifications notifications = new Notifications();
                        final String currentTimeStamp= String.valueOf(System.currentTimeMillis());
                        notifications.setId(currentTimeStamp.toString());
                        notifications.setTitle(messageTitle);
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
    private void openGallery(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Image"),199);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==199 && resultCode==RESULT_OK && data!=null){
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),filePath);
                btnChooseIMG.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}