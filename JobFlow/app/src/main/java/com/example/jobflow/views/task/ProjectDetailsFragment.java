package com.example.jobflow.views.task;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Intent.getIntent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jobflow.R;
import com.example.jobflow.adapter.MemberInTaskAdapter;
import com.example.jobflow.adapter.TaskAdapter;
import com.example.jobflow.controller.UserController.UserController;
import com.example.jobflow.data.Util;
import com.example.jobflow.model.Project;
import com.example.jobflow.model.Task;
import com.example.jobflow.model.TaskSub;
import com.example.jobflow.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class ProjectDetailsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public static TaskAdapter taskAdapter;
    public static List<Task> taskList;
    public static List<Task> filteredList = new ArrayList<>();
    CardView btn_addTask;
    RecyclerView recyclerView;
    Project project;
    private int position;

    Button btn_addNewTask;
    TextView tvStartDateTime,tv_addMember,tvEndDateTime;
    EditText nameProject,desProject;
    LinearLayout btnStartDateTime,btnEndDateTime;
    Calendar startCalendar, endCalendar;
    private SimpleDateFormat dateTimeFormat;
    MemberInTaskAdapter memberInProjectAdapter;
    UserController userController;
    List<User> memberList = new ArrayList<>();

    public ProjectDetailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProjectDetailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProjectDetailsFragment newInstance(String param1, String param2) {
        ProjectDetailsFragment fragment = new ProjectDetailsFragment();
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
        View view = inflater.inflate(R.layout.fragment_project_details, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        memberInProjectAdapter = new MemberInTaskAdapter(getContext(),memberList);
        userController = new UserController();
        btn_addTask =view.findViewById(R.id.btn_addTask);
        btn_addTask.setOnClickListener(v -> {
            showDialogAddNewTask();
        });

        project = (Project) getActivity().getIntent().getSerializableExtra("project");
        btn_addTask =view.findViewById(R.id.btn_addTask);
        btn_addTask.setOnClickListener(v -> {
            showDialogAddNewTask();
        });
        project= (Project) getActivity().getIntent().getSerializableExtra("project");
        setUpAdapter();
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        return view;
    }
    private void setUpAdapter(){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tasks").child(project.getId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Task task = dataSnapshot.getValue(Task.class);
                    dataSnapshot.child("users").getRef().addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            List<String> userID = new ArrayList<>();
                            for (DataSnapshot dataSnapshot1 : snapshot.getChildren()) {
                                userID.add(dataSnapshot1.getKey().toString());
                            }
                            task.setUserID(userID);
                            taskList.add(task);
                            taskAdapter.notifyDataSetChanged();
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
    }
    public static void OnVP2ChangeS(int tabPosition) {
        // Filter data based on the selected tab position
        filteredList.clear();
        for (Task data : taskList) {
            if (shouldShowDataForTab(data, tabPosition)) {
                filteredList.add(data);
            }
        }
        taskAdapter.notifyDataSetChanged();
    }
    private static boolean  shouldShowDataForTab(Task data, int tabPosition) {
        // Implement your filtering logic based on tab position
        if (tabPosition == 0) {
            return true;
        }
        return taskList.size()%tabPosition==0; // Example
    }
    private void showDialogAddNewTask(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_new_task);

        btn_addNewTask = dialog.findViewById(R.id.btn_addTask);
        tvStartDateTime = dialog.findViewById(R.id.tvStartDateTime);
        tvEndDateTime = dialog.findViewById(R.id.tvEndDateTime);
        nameProject = dialog.findViewById(R.id.editText);
        desProject = dialog.findViewById(R.id.editText2);
        btnStartDateTime = dialog.findViewById(R.id.btnStartDateTime);
        btnEndDateTime = dialog.findViewById(R.id.btnEndDateTime);
        tv_addMember = dialog.findViewById(R.id.tv_addMember);

        recyclerView = dialog.findViewById(R.id.recyclerView);
        int numberOfColumns = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(memberInProjectAdapter);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());


        btn_addNewTask.setOnClickListener(v -> saveDataTask(dialog));
        btnStartDateTime.setOnClickListener(v -> showDateTimePicker(true));
        btnEndDateTime.setOnClickListener(v -> showDateTimePicker(false));

        tv_addMember.setOnClickListener(v -> {
            userController.getAllUserByProject("0",project.getId(),new UserController.UserFetchCallback() {
                @Override
                public void onUserFetch(List<User> users) {
                    showUserSelectionDialog(users,memberInProjectAdapter.getMemberLists());
                }
            });
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }

    private void showDateTimePicker(boolean isStart) {
        Calendar calendar = isStart ? startCalendar : endCalendar;
        Calendar now = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(), (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                if (calendar.before(now)) {
                    Toast.makeText(getContext(), R.string.toast_date_time_err, Toast.LENGTH_SHORT).show();
                } else {
                    if (isStart) {
                        updateLabel(true);
                    } else {
                        if (startCalendar == null || startCalendar.getTimeInMillis() == 0) {
                            startCalendar = (Calendar) now.clone();
                            updateLabel(true);
                        }
                        if (endCalendar.before(startCalendar)) {
                            Toast.makeText(getContext(), R.string.toast_date_time_err1, Toast.LENGTH_SHORT).show();
                        } else {
                            updateLabel(false);
                        }
                    }
                }
            }, 0, 0, true);

            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.getDatePicker().setMinDate(Long.parseLong(project.getTimeS()));
        datePickerDialog.getDatePicker().setMaxDate(Long.parseLong(project.getTimeE()));
        datePickerDialog.show();
    }

    private void updateLabel(boolean isStart) {
        if (isStart) {
            tvStartDateTime.setText(dateTimeFormat.format(startCalendar.getTime()));
        } else {
            tvEndDateTime.setText(dateTimeFormat.format(endCalendar.getTime()));
        }
    }

    public void showUserSelectionDialog(final List<User> users, final List<User> users1) {
        DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
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
                    if (snapshot.child("id").getValue(String.class).equals(getActivity().getSharedPreferences("MyProfile", MODE_PRIVATE).getString("UID", "").trim())) {
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
                if (userComp != null && user.getId() != null && userComp.getId() != null && user.getId().trim().equals(userComp.getId().trim())) {
                    isChecked = true;
                    break;
                }
            }
            checkedItems[i] = isChecked;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View dialogView = inflater.inflate(R.layout.dialog_with_search, null);

        EditText searchEditText = dialogView.findViewById(R.id.search_edit_text);
        ListView usersListView = dialogView.findViewById(R.id.users_list_view);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getContext(),
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

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    private void saveDataTask(Dialog dialog) {
        String name = nameProject.getText().toString().trim();
        String des = desProject.getText().toString().trim();
        if (name.isEmpty() || des.isEmpty() || startCalendar == null || endCalendar == null) {
            Toast.makeText(getContext(), R.string.toast_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if (startCalendar.after(endCalendar)) {
            Toast.makeText(getContext(), R.string.toast_date_time_err1, Toast.LENGTH_SHORT).show();
            return;
        }
        List<User> users = memberInProjectAdapter.getMemberLists();
        if (users.isEmpty()) {
            Toast.makeText(getContext(), R.string.toast_select_member, Toast.LENGTH_SHORT).show();
            return;
        }
        String timeS = Util.convertToTimestamp(tvStartDateTime.getText().toString()) + "";
        String timeE = Util.convertToTimestamp(tvEndDateTime.getText().toString()) + "";
        if (Long.parseLong(timeS) < Long.parseLong(project.getTimeS())){
            Toast.makeText(getContext(), R.string.toast_date_time_err2, Toast.LENGTH_SHORT).show();
            return;
        }
        if (Long.parseLong(timeE) > Long.parseLong(project.getTimeE())){
            Toast.makeText(getContext(), R.string.toast_date_time_err2, Toast.LENGTH_SHORT).show();
            return;
        }
        if (timeS.isEmpty() || timeE.isEmpty()) {
            Toast.makeText(getContext(), R.string.toast_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        String id = FirebaseDatabase.getInstance().getReference("tasks").child(project.getId()).push().getKey();
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(project.getId()).child(id);
        Task task = new Task();
        task.setId(id);
        task.setName(name);
        task.setProjectID(project.getId());
        task.setDes(des);
        task.setTimeS(timeS);
        task.setTimeE(timeE);
        taskRef.setValue(task);
        for (User user : users) {
            taskRef.child("users").child(user.getId()).setValue(user.getWork());
        }
        taskList.add(task);
        taskAdapter.notifyDataSetChanged();
        dialog.dismiss();
        Toast.makeText(getContext(), R.string.toast_add_task_success, Toast.LENGTH_SHORT).show();
    }
}