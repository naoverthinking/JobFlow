package com.example.jobflow.views.task;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.jobflow.R;
import com.example.jobflow.adapter.MemberInProjectAdapter;
import com.example.jobflow.adapter.MemberInTaskAdapter;
import com.example.jobflow.adapter.ProjectDetailsViewPagerAdapter;
import com.example.jobflow.controller.UserController.UserController;
import com.example.jobflow.data.Util;
import com.example.jobflow.model.Project;
import com.example.jobflow.model.Task;
import com.example.jobflow.model.User;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;


public class DetailsProjectActivity extends AppCompatActivity{

    TabLayout tabLayout ;
    ViewPager2 viewPager2 ;
    Project project;
    TextView tv_namePrj,tv_desPrj;
    ImageView btn_back;

    public static int position=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details_project);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitUI();
        setUpTabLayout();
    }

    private void InitUI() {

        tabLayout = findViewById(R.id.tabLayout);
        viewPager2= findViewById(R.id.viewpager2);
        tv_namePrj = findViewById(R.id.tv_namePrj);
        tv_desPrj = findViewById(R.id.tv_desPrj);
        btn_back = findViewById(R.id.btn_back);

        project = (Project) getIntent().getSerializableExtra("project");


        tv_namePrj.setText(project.getName());
        tv_desPrj.setText(Util.formatTime(Long.parseLong(project.getTimeS())) + " - " + Util.formatTime(Long.parseLong(project.getTimeE())));
        btn_back.setOnClickListener(v -> {
            Intent intent = new Intent(DetailsProjectActivity.this, ProjectActivity.class);
            intent.putExtra("project", project);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });

    }
    private void setUpTabLayout() {
        ProjectDetailsViewPagerAdapter adapter = new ProjectDetailsViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);
        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("Tất cả");
                            break;
                        case 1:
                            tab.setText("Đã hoàn thành");
                            break;
                        case 2:
                            tab.setText("Chưa hoàn thành");
                            break;
                        case 3:
                            tab.setText("Hoàn thành trễ");
                            break;
                        case 4:
                            tab.setText("Chưa bắt đầu");
                            break;
                    }
                }).attach();
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                ProjectDetailsFragment.OnVP2ChangeS(position);
            }
        });
    }
}