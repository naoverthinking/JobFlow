package com.example.jobflow.views.search;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.MainActivity;
import com.example.jobflow.R;
import com.example.jobflow.adapter.SearchAdapter;
import com.example.jobflow.model.Project;
import com.example.jobflow.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    String txtSearch;
    TextView result;
    RecyclerView recyclerView;
    SearchAdapter searchAdapter;
    List<User> userList;
    List<Project> projectList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        InitUI();
    }

    private void InitUI() {
//        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SearchFragment()).commit();
        ImageView btn_pfback = findViewById(R.id.btn_pfback);
        btn_pfback.setOnClickListener(v -> {
            startActivity(new Intent(SearchActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            finish();
        });
        result = findViewById(R.id.result_search);
        recyclerView = findViewById(R.id.recyclerview_search);
        txtSearch = getIntent().getStringExtra("query");
        List<User> userList = new ArrayList<>();
        List<Project> projectList = new ArrayList<>();
        searchAdapter = new SearchAdapter(projectList, userList, this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(searchAdapter);
        result.setText(getString(R.string.text_search_result) +" "+txtSearch);
        setUpAdapter();
    }

    private void setUpAdapter() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user.getName().trim().toLowerCase().contains(txtSearch.toLowerCase().trim())) {
                        userList.add(user);
                    }
                }
                searchAdapter.updateDataUser(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        databaseReference.child("project").child("0").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Project> projectList = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Project project = dataSnapshot.getValue(Project.class);
                    if (project.getName().toLowerCase().contains(txtSearch.toLowerCase())) {
                        projectList.add(project);
                    }
                }
                searchAdapter.updateDataProject(projectList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}