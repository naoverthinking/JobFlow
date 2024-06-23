package com.example.jobflow.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.R;
import com.example.jobflow.data.Util;
import com.example.jobflow.model.Task;
import com.example.jobflow.model.TaskSub;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TaskSubAdapter extends RecyclerView.Adapter<TaskSubAdapter.ViewHolder>{

    Button btn_addNewTask;
    TextView tvStartDateTime,tv_addMember,tvEndDateTime;
    EditText nameProject,desProject;
    LinearLayout btnStartDateTime,btnEndDateTime;

    Calendar startCalendar, endCalendar;
    private SimpleDateFormat dateTimeFormat;
    DatabaseReference usersRef;
    List<TaskSub> taskSubList = new ArrayList<>();
    List<TaskSub> mainList = new ArrayList<>();
    Context context;

    public TaskSubAdapter(List<TaskSub> taskSubList, Context context) {
        this.taskSubList = taskSubList;
        this.context = context;
    }

    @NonNull
    @Override
    public TaskSubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_tasksub, parent, false);
        return new TaskSubAdapter.ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskSubAdapter.ViewHolder holder, int position) {
        TaskSub tasksub = taskSubList.get(position);
        if (Long.parseLong(tasksub.getTimeS()) < System.currentTimeMillis()){
            tasksub.setStatus(3);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tasksubs").child(tasksub.getTaskID()).child(tasksub.getId()).child("status");
            databaseReference.setValue(tasksub.getStatus());
        }
        if (Long.parseLong(tasksub.getTimeS()) > System.currentTimeMillis() && tasksub.getStatus() == 3){
            tasksub.setStatus(0);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tasksubs").child(tasksub.getTaskID()).child(tasksub.getId()).child("status");
            databaseReference.setValue(tasksub.getStatus());
        }
        if (Long.parseLong(tasksub.getTimeE()) < System.currentTimeMillis() && tasksub.getStatus() == 0){
            tasksub.setStatus(2);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tasksubs").child(tasksub.getTaskID()).child(tasksub.getId()).child("status");
            databaseReference.setValue(tasksub.getStatus());
        }
        switch (tasksub.getStatus()){
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
        holder.taskName.setText(tasksub.getName());
        holder.taskTime.setText(Util.formatTime(Long.parseLong(tasksub.getTimeS().toString()))+" - "+Util.formatTime(Long.parseLong(tasksub.getTimeE().toString())));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(tasksub.getUserID()).get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()){
                holder.taskUsers.setText(task1.getResult().child("name").getValue().toString());
            }
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
            showDialogEditTask(tasksub);
        });
    }

    @Override
    public int getItemCount() {
        if (taskSubList != null){
            return taskSubList.size();
        }else{
            return 0;
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView taskName, taskTime,taskUsers,statusTask;
        ImageView btn_EditTask;
        View line_bgr;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            taskName = itemView.findViewById(R.id.tv_nameTask);
            taskTime = itemView.findViewById(R.id.tv_timeTask);
            taskUsers = itemView.findViewById(R.id.tv_nameUser);
            btn_EditTask= itemView.findViewById(R.id.btn_EditTask);
            line_bgr = itemView.findViewById(R.id.line_bgr);
            statusTask = itemView.findViewById(R.id.status_task);
        }
    }
    private void showDialogEditTask(TaskSub tasksub) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_edt_status_task);
        dialog.setCanceledOnTouchOutside(true);

        TextView nameTask = dialog.findViewById(R.id.textView);
        TextView timeStart = dialog.findViewById(R.id.tvStartDateTime);
        TextView timeEnd = dialog.findViewById(R.id.tvEndDateTime);
        TextView tvMember = dialog.findViewById(R.id.tv_member);
        TextView btnEdit = dialog.findViewById(R.id.btn_EditTask);

        if (Long.parseLong(tasksub.getTimeS()) < System.currentTimeMillis()){
            tasksub.setStatus(3);
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("tasksubs").child(tasksub.getTaskID()).child(tasksub.getId()).child("status");
            databaseReference.setValue(tasksub.getStatus());
        }
        if (tasksub.getStatus() == 0) {
            btnEdit.setSelected(true);
            btnEdit.setText("Hoàn thành");
            btnEdit.setBackgroundColor(context.getResources().getColor(R.color.colorBlue));
            if (Long.parseLong(tasksub.getTimeE()) < System.currentTimeMillis()){
                btnEdit.setText("Hoàn thành trễ");
                btnEdit.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
            }
        } else if (tasksub.getStatus() == 1) {
            btnEdit.setText("Đã hoàn thành");
            btnEdit.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
            btnEdit.setSelected(false);
        } else if (tasksub.getStatus() == 2) {
            btnEdit.setText("Hoàn thành trễ");
            btnEdit.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
            btnEdit.setSelected(false);
        } else if (tasksub.getStatus()==3){
            btnEdit.setText("Chưa bắt đầu");
            btnEdit.setBackgroundColor(context.getResources().getColor(R.color.colorGrey));
            btnEdit.setSelected(false);
        }

        nameTask.setText(tasksub.getName());
        timeStart.setText(Util.formatTime(Long.parseLong(tasksub.getTimeS().toString())));
        timeEnd.setText(Util.formatTime(Long.parseLong(tasksub.getTimeE().toString())));
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.child(tasksub.getUserID()).get().addOnCompleteListener(task1 -> {
            if (task1.isSuccessful()){
                tvMember.setText(task1.getResult().child("name").getValue().toString());
            }
        });
        btnEdit.setOnClickListener(v -> {
            if (tasksub.getStatus() == 0) {
                if (Long.parseLong(tasksub.getTimeE()) < System.currentTimeMillis()){
                    tasksub.setStatus(2);
                    btnEdit.setBackgroundColor(context.getResources().getColor(R.color.colorGreen));
                }else{
                    tasksub.setStatus(1);
                    btnEdit.setBackgroundColor(context.getResources().getColor(R.color.colorRed));
                }
            }
            DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference().child("tasksubs").child(tasksub.getTaskID()).child(tasksub.getId()).child("status");
            databaseReference1.setValue(tasksub.getStatus());
            this.notifyDataSetChanged();
            dialog.dismiss();
        });
        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
}
