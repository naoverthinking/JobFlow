package com.example.jobflow;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.adapter.WorkspacesAdapter;
import com.example.jobflow.data.Util;
import com.example.jobflow.model.Workspaces;
import com.example.jobflow.notification.AccessTokenManager;
import com.example.jobflow.notification.MyBroadCastReceiver;
import com.example.jobflow.notification.MyForegroundService;
import com.example.jobflow.notification.SendNotification;
import com.example.jobflow.views.login.LoginActivity;
import com.example.jobflow.views.mainfragment.ChatFragment;
import com.example.jobflow.views.mainfragment.HomeFragment;
import com.example.jobflow.views.mainfragment.NotificationFragment;
import com.example.jobflow.views.mainfragment.WorkFragment;
import com.example.jobflow.views.profile.EditProfileActivity;
import com.example.jobflow.views.search.SearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.navigation.NavigationView;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private static final String PREFERENCES_NAME = "app_preferences";
    private static final String KEY_LANGUAGE = "language";

    private static final int NOTIFICATION_PERMISSION_REQUEST_CODE = 1;
    public static String textQuery="";

    private ImageView btn_toggle,btn_change_theme,openProfile;
//    private BottomNavigationView bottomNavigationView;
    RelativeLayout mainLayout;
    Animation slideInLeft,slideOutRight;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView addWorkspaces,logout, tvDuAn, tvCongViecCuaToi, tvTinNhan, tvThongBao;
    public static TextView nameWS;
    SearchView searchView;


    List<Workspaces> workspacesList = new ArrayList<>();
    WorkspacesAdapter workspacesAdapter = new WorkspacesAdapter(workspacesList, MainActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);
        String languageCode = preferences.getString(KEY_LANGUAGE, "vi");
        switchLanguage(languageCode, false);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
//        Util.createNotificationChannel("hjjhj","hjkhjkh","hihihi",this);
//        setMultipleAlarms();
        InitUI();
        RequestPermission();
        replaceFragment(new HomeFragment());
        InitNavigationView();
        InitFunction();
        InitWorkspaces(MainActivity.this);
        setupFirebaseListener();

        getFCMToken();
    }



    private void InitFunction() {
//        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
//            @SuppressLint("NonConstantResourceId")
//            @Override
//            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                int itemId= menuItem.getItemId();
//                if (itemId==R.id.navhome){
//                    replaceFragment(new HomeFragment());
//                }else if (itemId==R.id.navwork){
//                    replaceFragment(new WorkFragment());
//                }else if (itemId==R.id.navchat){
//                    replaceFragment(new ChatFragment());
//                }else{
//                    replaceFragment(new NotificationFragment());
//                }
//                return true;
//            }
//        });

        tvDuAn.setOnClickListener(v -> {
            tvDuAn.setSelected(false);
            tvCongViecCuaToi.setSelected(false);
            tvTinNhan.setSelected(false);
            tvThongBao.setSelected(false);
            v.setSelected(true);
            replaceFragment(new HomeFragment());
        });

        tvCongViecCuaToi.setOnClickListener(v -> {
            tvDuAn.setSelected(false);
            tvCongViecCuaToi.setSelected(false);
            tvTinNhan.setSelected(false);
            tvThongBao.setSelected(false);
            v.setSelected(true);
            replaceFragment(new WorkFragment());
        });

        tvTinNhan.setOnClickListener(v -> {
            tvDuAn.setSelected(false);
            tvCongViecCuaToi.setSelected(false);
            tvTinNhan.setSelected(false);
            tvThongBao.setSelected(false);
            v.setSelected(true);
            replaceFragment(new ChatFragment());
        });

        tvThongBao.setOnClickListener(v -> {
            tvDuAn.setSelected(false);
            tvCongViecCuaToi.setSelected(false);
            tvTinNhan.setSelected(false);
            tvThongBao.setSelected(false);
            v.setSelected(true);
            replaceFragment(new NotificationFragment());
        });




        btn_change_theme.setOnClickListener(v -> {
            String currentLanguage = getResources().getConfiguration().locale.getLanguage();
            String newLanguage = currentLanguage.equals("en") ? "vi" : "en";
            switchLanguage(newLanguage, true);
        });
        openProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showPasswordDialog();
                startEditProfileActivity("abc123");
            }
        });
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                return false;
            }
        });
//        btn_toggle.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                drawerLayout.open();
//            }
//        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                SharedPreferences sharedPreferences = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
                String UID = sharedPreferences.getString("UID", "");
                databaseReference.child("users").child(UID).child("FCMToken").setValue("");
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();
                startActivity(new Intent(MainActivity.this,LoginActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                finish();
            }
        });
        addWorkspaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogAddWorkspaces();
            }
        });

    }

    private void InitUI() {
        SharedPreferences sharedPreferences = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("idWorkspaces", "0");
        editor.apply();
        String UID = sharedPreferences.getString("UID", "");
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(UID);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("role")) {
                    String role = snapshot.child("role").getValue(String.class);
                    SharedPreferences sharedPreferences = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("role", role);
                    editor.apply();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("role", "1");
                    editor.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        searchView = findViewById(R.id.edtSearch);
        searchView.setQuery("", false);
        textQuery="";
//        bottomNavigationView = findViewById(R.id.bottomnav);
//        btn_toggle = findViewById(R.id.btnOpenDrawer);
        openProfile = findViewById(R.id.openProfile);
        mainLayout = findViewById(R.id.main);
        btn_change_theme = findViewById(R.id.toggle_change_theme);
        addWorkspaces = findViewById(R.id.testanm);
        logout = findViewById(R.id.logout);
        nameWS = findViewById(R.id.nameWS);

        tvDuAn = findViewById(R.id.tvDuAn);
        tvDuAn.setSelected(true);
        tvCongViecCuaToi = findViewById(R.id.tvCongViecCuaToi);
        tvTinNhan = findViewById(R.id.tvTinNhan);
        tvThongBao = findViewById(R.id.tvThongBao);

        drawerLayout = findViewById(R.id.main_drawer_layout);
        navigationView = findViewById(R.id.navigationview);

        slideOutRight = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_out_right);
        slideInLeft = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_in_left);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", String.valueOf(query).trim());
                startActivity(intent);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                textQuery = newText;
                HomeFragment.loadWorkspaceData(MainActivity.this, String.valueOf(newText).trim());
                WorkFragment.loadWorkspaceData(MainActivity.this, String.valueOf(newText).trim());
                return false;
            }
        });
    }
    private void InitNavigationView(){
        actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,R.string.open_navigation_drawer,R.string.close_navigation_drawer);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        else{
            super.onBackPressed();
        }
    }
    private void toggleTheme() {
        int nightMode = AppCompatDelegate.getDefaultNightMode();
        if (nightMode == AppCompatDelegate.MODE_NIGHT_NO) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        recreate(); // Restart activity to apply theme change
    }
    private void UploadImage(){
        ImagePicker.with(MainActivity.this)
                .crop()
                .compress(1204)
                .maxResultSize(1080, 1080)
                .start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, R.string.toast_cancel_pick_image, Toast.LENGTH_SHORT).show();
        } else {

        }
    }
    private void InitWorkspaces(Context context){
        RecyclerView recyclerView = findViewById(R.id.navigation_recyclerview);
        int numberOfColumns = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(workspacesAdapter);
    }
    private void setupFirebaseListener() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("workspaces");
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Workspaces> workspaces = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String uuid = MainActivity.this.getSharedPreferences("MyProfile", Context.MODE_PRIVATE).getString("UID", "");
                    if (dataSnapshot.child("users").hasChild(uuid.trim())){
                        Workspaces workspace = dataSnapshot.getValue(Workspaces.class);
                        if (workspace.getId() != null) {
                            workspaces.add(workspace);
                        }
                    }
                }
                workspacesAdapter.updateData(workspaces);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }
    private void showDialogAddWorkspaces() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_company);

        EditText name,des;
        name = dialog.findViewById(R.id.nameWorkspaces);
        des = dialog.findViewById(R.id.fieldWorkspaces);
        Button btnNext,btnSkip;
        btnNext = dialog.findViewById(R.id.toWE);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sname = String.valueOf(name.getText());
                String sdes = String.valueOf(des.getText());

                SharedPreferences sharedPreferences = MainActivity.this.getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                String sUID = sharedPreferences.getString("UID", "");

                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("workspaces");
                String userId = databaseReference.push().getKey();
                Workspaces workspaces=new Workspaces(userId,sname,sdes,sUID,null);
                databaseReference.child(userId).setValue(workspaces);
                databaseReference.child(userId).child("users").child(sUID).setValue(1);
                dialog.dismiss();
//                showDialogInviteWorkspaces(userId);
            }
        });

        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        dialog.show();
    }
    private void showDialogInviteWorkspaces(String wid){
//        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.dialog_invite_workspaces);
//
//        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        dialog.show();
    }
    private void switchLanguage(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }
    private void switchLanguage(String languageCode, boolean restartActivity) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);
        Resources resources = getResources();
        Configuration config = resources.getConfiguration();
        config.setLocale(locale);
        resources.updateConfiguration(config, resources.getDisplayMetrics());

        SharedPreferences.Editor editor = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE).edit();
        editor.putString(KEY_LANGUAGE, languageCode);
        editor.apply();

        if (restartActivity) {
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    }
    private void getFCMToken() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()){
                        String token = task.getResult();
                        String uuid = MainActivity.this.getSharedPreferences("MyProfile", Context.MODE_PRIVATE).getString("UID", "");
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uuid).child("FCMToken");
                        databaseReference.setValue(token);
                    }
                    if (!task.isSuccessful()) {
                        Log.w("FCM", "Fetching FCM registration token failed", task.getException());
                    }
                });
    }
    private void RequestPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE}, 299);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_PERMISSION_REQUEST_CODE);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 299){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            }
        }
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("MainActivity", "Notification permission granted");
            } else {
                Log.e("MainActivity", "Notification permission denied");
            }
        }
    }
    private void runForegroundService() {
        Intent servicceIntent = new Intent(this, MyForegroundService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(servicceIntent);
        }
        foregroundServiceIsRunning();
    }
    public  boolean foregroundServiceIsRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MyForegroundService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "foxandroidReminderChannel";
            String description = "Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("hihihi", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
    private void setAlarm() {
        createNotificationChannel();
        AlarmManager alarmManager;

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, MyBroadCastReceiver.class);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
        Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();
    }
    private void setMultipleAlarms() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, MyBroadCastReceiver.class);

        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.SECOND, 5);
        PendingIntent pendingIntent30 = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent30);

//        calendar = Calendar.getInstance();
//        calendar.add(Calendar.SECOND, 10);
//        PendingIntent pendingIntent40 = PendingIntent.getBroadcast(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent40);
//
//        calendar = Calendar.getInstance();
//        calendar.add(Calendar.SECOND, 15);
//        PendingIntent pendingIntent60 = PendingIntent.getBroadcast(this, 2, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent60);

        Toast.makeText(this, "Multiple Alarms set Successfully", Toast.LENGTH_SHORT).show();
    }



    private void showPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Password");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = input.getText().toString();
                reauthenticateUser(password);
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void reauthenticateUser(String password) {
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
                        startEditProfileActivity(password);
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "Incorrect password. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "No user is logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void startEditProfileActivity(String password) {
        Intent intent = new Intent(MainActivity.this, EditProfileActivity.class);
        intent.putExtra("password", password);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

}
