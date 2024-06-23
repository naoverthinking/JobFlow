package com.example.jobflow.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.R;
import com.example.jobflow.controller.UserController.UserController;
import com.example.jobflow.data.Util;
import com.example.jobflow.model.Project;
import com.example.jobflow.model.Task;
import com.example.jobflow.model.User;
import com.example.jobflow.views.task.ProjectActivity;
import com.example.jobflow.views.task.TaskActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    List<Task> taskList = new ArrayList<>();
    Context context;

    Button btn_addNewTask;
    TextView tvStartDateTime,tv_addMember,tvEndDateTime;
    EditText nameProject,desProject;
    LinearLayout btnStartDateTime,btnEndDateTime;
    RecyclerView recyclerView;
    MemberInTaskAdapter memberInProjectAdapter;
    List<User> memberList = new ArrayList<>();
    UserController userController;

    Calendar startCalendar, endCalendar;
    private SimpleDateFormat dateTimeFormat;
    DatabaseReference usersRef;

    public TaskAdapter(List<Task> taskList, Context context) {
        this.taskList = taskList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskAdapter.TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_details_project, parent, false);
        return new TaskAdapter.TaskViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {
        Task task = taskList.get(position);


        switch (task.getStatus()){
            case 0:
                holder.line_bgr.setBackgroundColor(context.getResources().getColor(R.color.colorBlue));
                holder.statusTask.setText("Chưa hoàn thành");
                break;
            case 1:
                holder.line_bgr.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
                holder.statusTask.setText("Đã hoàn thành");
                break;
            case 2:
                holder.line_bgr.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
                holder.statusTask.setText("Trễ hạn");
                break;
            default:
                holder.line_bgr.setBackgroundColor(context.getResources().getColor(R.color.colorGrey));
                holder.statusTask.setText("Chưa bắt đầu");
                break;
        }
        holder.taskName.setText(task.getName());
        holder.taskTime.setText(Util.formatTime(Long.parseLong(task.getTimeS()))+" - "+Util.formatTime(Long.parseLong(task.getTimeE())));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("tasksubs").child(task.getId());
        databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Count the number of children
                long count = dataSnapshot.getChildrenCount();
                holder.count.setText("/"+String.valueOf(count));
                databaseReference1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        int countd = 0;
                        for(DataSnapshot ds : dataSnapshot.getChildren()) {
                            if (ds.child("status").getValue().toString().equals("1")){
                                countd++;
                            }
                        }
                        if (countd == count && countd != 0){
                            task.setStatus(1);
                            holder.statusTask.setText("Đã hoàn thành");
                            holder.line_bgr.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
                        }
                        holder.countDone.setText(String.valueOf(countd));
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        // Handle possible errors.
                        Log.e("Firebase", "Error getting data", databaseError.toException());
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors.
                Log.e("Firebase", "Error getting data", databaseError.toException());
            }
        });
        StringBuilder nameBuilder = new StringBuilder();
        for (int i = 0; i < task.getUserID().size(); i++) {
            int j=i;
            databaseReference.child(task.getUserID().get(i)).get().addOnCompleteListener(task1 -> {
                if (task1.isSuccessful()){
                    if (j>0){
                        nameBuilder.append(", ");
                    }
                    nameBuilder.append(task1.getResult().child("name").getValue().toString());
                    holder.taskUsers.setText(nameBuilder.toString());
                }
            });
        }
        holder.btn_EditTask.setOnClickListener(v -> {
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
            showDialogAddNewTask(task);
        });
        holder.itemView.setOnClickListener(v -> {
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
            String UID = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE).getString("UID", "");
            Project project = (Project) ((Activity) context).getIntent().getSerializableExtra("project");
            DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(project.getId()).child(task.getId()).child("users");
            taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.hasChild(UID.trim())) {
                        Intent intent = new Intent(context, TaskActivity.class);
                        intent.putExtra("task", (Serializable) task);
                        intent.putExtra("project", (Serializable) ((Activity) context).getIntent().getSerializableExtra("project"));
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        holder.itemView.getContext().startActivity(intent);
                    } else {
                        Toast.makeText(context, "Bạn không có quyền truy cập", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Handle possible errors.
                    Log.e("Firebase", "Error getting data", error.toException());
                }
            });
        });
    }

    @Override
    public int getItemCount() {
        if (taskList!=null){
            return taskList.size();
        }
        else {
            return 0;
        }
    }
    private void updateList(List<Task> taskList){
        this.taskList = taskList;
        notifyDataSetChanged();
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskTime,taskUsers,statusTask,countDone,count;
        ImageView btn_EditTask;
        View line_bgr;
        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.tv_nameTask);
            taskTime = itemView.findViewById(R.id.tv_timeTask);
            taskUsers = itemView.findViewById(R.id.tv_nameUser);
            btn_EditTask= itemView.findViewById(R.id.btn_EditTask);
            line_bgr = itemView.findViewById(R.id.line_bgr);
            statusTask = itemView.findViewById(R.id.status_task);
            count = itemView.findViewById(R.id.countItemTask);
            countDone = itemView.findViewById(R.id.countItemTaskDone);
        }
    }

    private void showDialogAddNewTask(Task task){
        final Dialog dialog = new Dialog(context);
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numberOfColumns);
        memberInProjectAdapter = new MemberInTaskAdapter(context, memberList);
        userController = new UserController();
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(memberInProjectAdapter);


        setUpProjectMemberRecyclerView(dialog,task);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());


        btn_addNewTask.setOnClickListener(v -> saveDataTask(dialog,task));
        btnStartDateTime.setOnClickListener(v -> showDateTimePicker(true));
        btnEndDateTime.setOnClickListener(v -> showDateTimePicker(false));

        tv_addMember.setOnClickListener(v -> {
            Project project = (Project) ((Activity) context).getIntent().getSerializableExtra("project");
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

        DatePickerDialog datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            TimePickerDialog timePickerDialog = new TimePickerDialog(context, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);

                if (calendar.before(now)) {
                    Toast.makeText(context, R.string.toast_date_time_err, Toast.LENGTH_SHORT).show();
                } else {
                    if (isStart) {
                        updateLabel(true);
                    } else {
                        if (startCalendar == null || startCalendar.getTimeInMillis() == 0) {
                            startCalendar = (Calendar) now.clone();
                            updateLabel(true);
                        }
                        if (endCalendar.before(startCalendar)) {
                            Toast.makeText(context, R.string.toast_date_time_err1, Toast.LENGTH_SHORT).show();
                        } else {
                            updateLabel(false);
                        }
                    }
                }
            }, 0, 0, true);
            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        Project project = (Project) ((Activity) context).getIntent().getSerializableExtra("project");
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
                    if (snapshot.child("id").getValue(String.class).equals(((Activity) context).getSharedPreferences("MyProfile", MODE_PRIVATE).getString("UID", "").trim())) {
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

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.dialog_with_search, null);

        EditText searchEditText = dialogView.findViewById(R.id.search_edit_text);
        ListView usersListView = dialogView.findViewById(R.id.users_list_view);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context,
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

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
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

    private void saveDataTask(Dialog dialog,Task task) {
        String name = nameProject.getText().toString().trim();
        String des = desProject.getText().toString().trim();
        if (name.isEmpty() || des.isEmpty() || startCalendar == null || endCalendar == null) {
            Toast.makeText(context, R.string.toast_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        if (startCalendar.after(endCalendar)) {
            Toast.makeText(context, R.string.toast_date_time_err1, Toast.LENGTH_SHORT).show();
            return;
        }
        List<User> users = memberInProjectAdapter.getMemberLists();
        if (users.isEmpty()) {
            Toast.makeText(context, R.string.toast_select_member, Toast.LENGTH_SHORT).show();
            return;
        }
        String timeS = Util.convertToTimestamp(tvStartDateTime.getText().toString()) + "";
        String timeE = Util.convertToTimestamp(tvEndDateTime.getText().toString()) + "";
        Project project = (Project) ((Activity) context).getIntent().getSerializableExtra("project");
        if (Long.parseLong(timeS) < Long.parseLong(project.getTimeS())){
            Toast.makeText(context, R.string.toast_date_time_err2, Toast.LENGTH_SHORT).show();
            return;
        }
        if (Long.parseLong(timeE) > Long.parseLong(task.getTimeE())){
            Toast.makeText(context, R.string.toast_date_time_err2, Toast.LENGTH_SHORT).show();
            return;
        }
        if (timeS.isEmpty() || timeE.isEmpty()) {
            Toast.makeText(context, R.string.toast_fill_all_fields, Toast.LENGTH_SHORT).show();
            return;
        }
        String id = FirebaseDatabase.getInstance().getReference("tasks").child(project.getId()).child(task.getId()).getKey();
        DatabaseReference taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(project.getId()).child(id);
        Task task1 = new Task();
        task1.setId(id);
        task1.setName(name);
        task1.setProjectID(task.getId());
        task1.setDes(des);
        task1.setTimeS(timeS);
        task1.setTimeE(timeE);
        taskRef.setValue(task1);
        for (User user : users) {
            taskRef.child("users").child(user.getId()).setValue(user.getWork());
        }
        dialog.dismiss();
        this.taskList.get(this.taskList.indexOf(task)).setName(name);
        this.taskList.get(this.taskList.indexOf(task)).setDes(des);
        this.taskList.get(this.taskList.indexOf(task)).setTimeS(timeS);
        this.taskList.get(this.taskList.indexOf(task)).setTimeE(timeE);
        this.notifyDataSetChanged();
        Toast.makeText(context, R.string.toast_add_task_success, Toast.LENGTH_SHORT).show();
    }
    private void setUpProjectMemberRecyclerView(Dialog dialog,Task task) {
        nameProject.setText(task.getName());
        desProject.setText(task.getDes());
        tvStartDateTime.setText(Util.formatTime(Long.parseLong(task.getTimeS())));
        tvEndDateTime.setText(Util.formatTime(Long.parseLong(task.getTimeE())));

        usersRef= FirebaseDatabase.getInstance().getReference("users");
        Activity activity = (Activity) context;
        Project project = (Project) activity.getIntent().getSerializableExtra("project") != null ? (Project) activity.getIntent().getSerializableExtra("project") : new Project();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tasks").child(project.getId()).child(task.getId()).child("users");
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
    }

}
