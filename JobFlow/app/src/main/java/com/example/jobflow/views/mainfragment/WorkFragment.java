package com.example.jobflow.views.mainfragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jobflow.MainActivity;
import com.example.jobflow.R;
import com.example.jobflow.adapter.ProjectAdapter;
import com.example.jobflow.model.Project;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WorkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static List<Project> projectList = new ArrayList<>();
    public static ProjectAdapter projectAdapter;

    public WorkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkFragment newInstance(String param1, String param2) {
        WorkFragment fragment = new WorkFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_work, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recyFragWork);
        String role = getContext().getSharedPreferences("MyProfile", Context.MODE_PRIVATE).getString("role", "1");
        if (role.equals("0")){
            projectList.add(new Project());
        }
        projectAdapter = new ProjectAdapter(projectList,getContext());
        int numberOfColumns = 3;

        GridLayoutManager gridLayoutManager = new GridLayoutManager(view.getContext(), numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        loadWorkspaceData(getContext(), MainActivity.textQuery);
        recyclerView.setAdapter(projectAdapter);
        return view;

    }
    public static void loadWorkspaceData(Context context) {
        List<Project> projectList1 = new ArrayList<>();
        projectList1.clear();
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        String wsID = sharedPreferences.getString("idWorkspaces", "dfvalue");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("project");
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild(wsID)) {
                    for (DataSnapshot snapshot1 : snapshot.child(wsID).getChildren()) {
                        Project project = snapshot1.getValue(Project.class);
                        project.setMember(String.valueOf(snapshot1.child("users").getChildrenCount()));
                        if (project != null && snapshot1.child("users").hasChild(sharedPreferences.getString("UID", "dfvalue"))) {
                            projectList1.add(project);
                        }
                    }
                }
                projectAdapter.updateData(projectList1);
            }            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    public static void loadWorkspaceData(Context context,String query) {
        if (projectAdapter!=null && projectList!=null){
            Log.d("androidruntime", "loadWorkspaceData: "+projectList.size());
            Log.d("androidruntime", "loadWorkspaceData1: "+query);
            List<Project> projectList1 = new ArrayList<>();
            projectList1.clear();
            SharedPreferences sharedPreferences = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
            String wsID = sharedPreferences.getString("idWorkspaces", "dfvalue");
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("project");
            databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(wsID)) {
                        for (DataSnapshot snapshot1 : snapshot.child(wsID).getChildren()) {
                            Project project = snapshot1.getValue(Project.class);
                            project.setMember(String.valueOf(snapshot1.child("users").getChildrenCount()));
                            if (project != null && snapshot1.child("users").hasChild(sharedPreferences.getString("UID", "dfvalue")) && project.getName().toLowerCase().contains(query.toLowerCase())) {
                                projectList1.add(project);
                            }
                        }
                    }
                    projectAdapter.updateData(projectList1);
                }            @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}