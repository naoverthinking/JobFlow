package com.example.jobflow.views.task;

import static android.content.Context.MODE_PRIVATE;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jobflow.R;
import com.example.jobflow.adapter.MemberInTaskAdapter;
import com.example.jobflow.adapter.TaskAdapter;
import com.example.jobflow.adapter.TaskSubAdapter;
import com.example.jobflow.adapter.TaskSubVPAdapter;
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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TaskSubFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TaskSubFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static TaskSubAdapter taskAdapter;
    public static List<TaskSub> taskList;
    RecyclerView recyclerView;
    public static Task task;
    CardView btn_addTask;



    Button btn_addNewTask;
    TextView tvStartDateTime,tv_addMember,tvEndDateTime;
    EditText nameProject,desProject;
    LinearLayout btnStartDateTime,btnEndDateTime;
    MemberInTaskAdapter memberInProjectAdapter;
    List<User> memberList = new ArrayList<>();
    UserController userController;

    Calendar startCalendar, endCalendar;
    private SimpleDateFormat dateTimeFormat;


    public TaskSubFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TaskSubFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TaskSubFragment newInstance(String param1, String param2) {
        TaskSubFragment fragment = new TaskSubFragment();
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
        View view =  inflater.inflate(R.layout.fragment_task_sub, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        btn_addTask = view.findViewById(R.id.btn_addTask);
        task= (Task) getActivity().getIntent().getSerializableExtra("task");
        btn_addTask.setOnClickListener(v -> showDialogAddNewTask());
        setUpAdapter();
        taskList = new ArrayList<>();
        taskAdapter = new TaskSubAdapter(taskList, getContext());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 1);
        recyclerView.setAdapter(taskAdapter);
        recyclerView.setLayoutManager(gridLayoutManager);
        return view;
    }
    private void setUpAdapter() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tasksubs").child(task.getId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TaskSub taskSub = dataSnapshot.getValue(TaskSub.class);
                    taskList.add(taskSub);
                    taskAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public static void OnVP2ChangeS(int tabPosition) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tasksubs").child(task.getId());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    TaskSub taskSub = dataSnapshot.getValue(TaskSub.class);
//                    if (taskSub.getStatus()==0 || taskSub.getStatus()==tabPosition-1){
                        taskList.add(taskSub);
//                    }
                    taskAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showDialogAddNewTask(){
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_new_task);

        dialog.findViewById(R.id.tv_addMember).setVisibility(View.GONE);
        dialog.findViewById(R.id.tv_addMember1).setVisibility(View.GONE);

        btn_addNewTask = dialog.findViewById(R.id.btn_addTask);
        tvStartDateTime = dialog.findViewById(R.id.tvStartDateTime);
        tvEndDateTime = dialog.findViewById(R.id.tvEndDateTime);
        nameProject = dialog.findViewById(R.id.editText);
        desProject = dialog.findViewById(R.id.editText2);
        btnStartDateTime = dialog.findViewById(R.id.btnStartDateTime);
        btnEndDateTime = dialog.findViewById(R.id.btnEndDateTime);


        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());


        btn_addNewTask.setOnClickListener(v -> saveDataTask(dialog));
        btnStartDateTime.setOnClickListener(v -> showDateTimePicker(true));
        btnEndDateTime.setOnClickListener(v -> showDateTimePicker(false));

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
        datePickerDialog.getDatePicker().setMinDate(Long.parseLong(task.getTimeS()));
        datePickerDialog.getDatePicker().setMaxDate(Long.parseLong(task.getTimeE()));
        datePickerDialog.show();
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
        String timeS = Util.convertToTimestamp(tvStartDateTime.getText().toString()) + "";
        String timeE = Util.convertToTimestamp(tvEndDateTime.getText().toString()) + "";
        if (Long.parseLong(timeS) < Long.parseLong(task.getTimeS())){
            Toast.makeText(getContext(), R.string.toast_date_time_err2, Toast.LENGTH_SHORT).show();
            return;
        }
        if (Long.parseLong(timeE) > Long.parseLong(task.getTimeE())){
            Toast.makeText(getContext(), R.string.toast_date_time_err2, Toast.LENGTH_SHORT).show();
            return;
        }
        if (timeS.isEmpty() || timeE.isEmpty()) {
            Toast.makeText(getContext(), R.string.toast_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        String id = FirebaseDatabase.getInstance().getReference("tasksubs").child(task.getId()).push().getKey();
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("tasksubs").child(task.getId()).child(id);
        TaskSub taskSub = new TaskSub(id, name, des, timeS, timeE,task.getId(), getContext().getSharedPreferences("MyProfile", MODE_PRIVATE).getString("UID", ""), 0 );
        taskRef.setValue(taskSub);
        dialog.dismiss();

//        Util.createNotificationChannel("hjjhj","hjkhjkh","hihihi",getContext());
//        setMultipleAlarms(timeE);
        taskList.add(taskSub);
        taskAdapter.notifyDataSetChanged();
        Toast.makeText(getContext(), R.string.toast_add_task_success, Toast.LENGTH_SHORT).show();
    }
    private void updateLabel(boolean isStart) {
        if (isStart) {
            tvStartDateTime.setText(dateTimeFormat.format(startCalendar.getTime()));
        } else {
            tvEndDateTime.setText(dateTimeFormat.format(endCalendar.getTime()));
        }
    }

}