package com.example.jobflow.adapter;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.jobflow.R;
import com.example.jobflow.controller.UserController.UserController;
import com.example.jobflow.data.Util;
import com.example.jobflow.model.Project;
import com.example.jobflow.model.User;
import com.example.jobflow.notification.SendNotification;
import com.example.jobflow.views.chat.ChatDetailsActivity;
import com.example.jobflow.views.mainfragment.HomeFragment;
import com.example.jobflow.views.task.DetailsProjectActivity;
import com.example.jobflow.views.task.ProjectActivity;
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
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapter.ViewHolder> {

    private static final int VIEW_TYPE_FIRST = 0;
    private static final int VIEW_TYPE_NORMAL = 1;

    UserController userController = new UserController();
    String role;
    SharedPreferences sharedPreferences;
    String wsID;
    Uri filePath;
    String linkToIMGFirebase;
    EditText nameProject,desProject;
    StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");
    List<User> memberList = new ArrayList<>();
    private TextView tvStartDateTime, tvEndDateTime;
    private Calendar startCalendar, endCalendar;
    private SimpleDateFormat dateTimeFormat;

    private List<Project> projectList = new ArrayList<>();

    private Bitmap bitmap;
    Button btn_addProject;
    public void setBitmap(Bitmap bitmap,Uri uri){
        this.filePath=uri;
        this.btnChooseIMG.setImageBitmap(bitmap);
    }

    ImageView btnChooseIMG;
    MemberInProjectAdapter memberInProjectAdapter;
    DatabaseReference usersRef = FirebaseDatabase.getInstance().getReference("users");
    private Context context;
    private boolean isHome = false;
    public interface OnImageSelectListener {
        void onImageSelect();
    }
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private OnItemClickListener listener;
    public ProjectAdapter(List<Project> projectList, Context context,OnItemClickListener listener) {
        sharedPreferences = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        wsID = sharedPreferences.getString("idWorkspaces", "0");
        memberInProjectAdapter = new MemberInProjectAdapter(context,memberList);
        this.projectList = projectList;
        this.context=context;
        this.listener = listener;
        isHome=true;
    }
    public ProjectAdapter(List<Project> projectList, Context context) {
        sharedPreferences = context.getSharedPreferences("MyProfile", Context.MODE_PRIVATE);
        wsID = sharedPreferences.getString("idWorkspaces", "0");
        memberInProjectAdapter = new MemberInProjectAdapter(context,memberList);
        this.projectList = projectList;
        this.context=context;
        isHome=false;
    }
    @NonNull
    @Override
    public ProjectAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        role= sharedPreferences.getString("role", "");
        if (viewType == VIEW_TYPE_FIRST && role.equals("0") && isHome) {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_recyfraghome_first, parent, false);
        } else {
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_recyfraghome, parent, false);
        }
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProjectAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Project project = projectList.get(position);
        int viewType = getItemViewType(position);
        role= sharedPreferences.getString("role", "");
        if (viewType == VIEW_TYPE_FIRST) {
        } else {
            try {
                String timeString = Util.formatTime(Long.parseLong(project.getTimeS())) + " - " + formatTime(Long.parseLong(project.getTimeE()));
                holder.projectTime.setText(timeString);
            }catch (Exception e){

            }
            if (project.getImg() != null){
                Picasso.get().load(project.getImg()).into(holder.img_project);
            } else {
                Picasso.get().load(R.drawable.icon_ngoigoccay1).into(holder.img_project);
            }
            holder.projectName.setText(project.getName());
            holder.projectMember.setText(project.getMember() + " "+context.getString(R.string.member));
            Random random = new Random();
            Drawable[] backgrounds = new Drawable[]{
                    context.getResources().getDrawable(R.drawable.background_project),
                    context.getResources().getDrawable(R.drawable.background_project1),
                    context.getResources().getDrawable(R.drawable.background_project2),
                    context.getResources().getDrawable(R.drawable.background_project3),
            };
            Drawable background = backgrounds[random.nextInt(backgrounds.length)];
//            holder.relativeLayout.setBackground(background);
        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                if (role.equals("0") && position == 0 && isHome) {
                    showFirstItemDialog(context);
                } else {
                    if (role.equals("0")) {
                        Intent intent = new Intent(context, ProjectActivity.class);
                        intent.putExtra("project", (Serializable) project);
                        context.startActivity(intent);
                    }else{
                        final ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("project").child("0").child(project.getId()).child("users");
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.hasChild(sharedPreferences.getString("UID", ""))) {
                                    Intent intent = new Intent(context, ProjectActivity.class);
                                    intent.putExtra("project", (Serializable) project);
                                    context.startActivity(intent);
                                } else {
                                    Toast.makeText(context, R.string.toast_not_permission, Toast.LENGTH_SHORT).show();
                                }
                                progressDialog.dismiss();
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                progressDialog.dismiss();
                            }
                        });
                    }
                }
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
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
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        if (projectList!=null){
            return projectList.size();
        }
        return 0;
    }

    @Override
    public int getItemViewType(int position) {
        role= sharedPreferences.getString("role", "");
        if (role.equals("0") && position == 0 && isHome) {
            return VIEW_TYPE_FIRST;
        }else return VIEW_TYPE_NORMAL;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView projectName,projectTime,projectMember;
        CircleImageView img_project;
//        RelativeLayout relativeLayout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            projectName = itemView.findViewById(R.id.projectName);
            projectTime = itemView.findViewById(R.id.projectTime);
            projectMember = itemView.findViewById(R.id.projectMember);
            img_project = itemView.findViewById(R.id.img_project);
//            relativeLayout = itemView.findViewById(R.id.root_project);

        }
    }

    public void updateData(List<Project> newData) {
        this.projectList.clear();
        wsID = sharedPreferences.getString("idWorkspaces", "dfvalue");
        String role = sharedPreferences.getString("role", "");
        if (wsID != "dfvalue" && role.equals("0") && isHome){
            this.projectList.add(new Project());
        }
        this.projectList.addAll(newData);
        notifyDataSetChanged();
    }
    private void showFirstItemDialog(Context context){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_new_project);

        btnChooseIMG = dialog.findViewById(R.id.imageView2);
        btn_addProject = dialog.findViewById(R.id.btn_addProject);
        tvStartDateTime = dialog.findViewById(R.id.tvStartDateTime);
        tvEndDateTime = dialog.findViewById(R.id.tvEndDateTime);
        nameProject = dialog.findViewById(R.id.editText);
        desProject = dialog.findViewById(R.id.editText2);

        btnChooseIMG.setOnClickListener(v -> {
           openGallery();
        });
        btn_addProject.setOnClickListener(v -> {
            SaveDataProject(dialog);
        });
        LinearLayout btnStartDateTime = dialog.findViewById(R.id.btnStartDateTime);
        LinearLayout btnEndDateTime = dialog.findViewById(R.id.btnEndDateTime);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        btnStartDateTime.setOnClickListener(v -> showDateTimePicker(true));
        btnEndDateTime.setOnClickListener(v -> showDateTimePicker(false));

        TextView tv_addMember = dialog.findViewById(R.id.tv_addMember);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        int numberOfColumns = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(memberInProjectAdapter);


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

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
    }
    private void showItemDialog(Project project,Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_add_new_project);

        TextView tv_addMember = dialog.findViewById(R.id.tv_addMember);
        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        List<User> memberList = new ArrayList<>();
        MemberInProjectAdapter memberInProjectAdapter = new MemberInProjectAdapter(context,memberList);
        int numberOfColumns = 1;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(context, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(memberInProjectAdapter);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("project").child(wsID).child(project.getId());
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<User> userList = new ArrayList<>();
                for (DataSnapshot snapshot1:snapshot.child("users").getChildren()){
                    User user = new User();
                    user.setId(snapshot1.getKey());
                    user.setName(snapshot1.child(user.getId()).getValue(String.class));
                    userList.add(user);
                }
                memberInProjectAdapter.updateData(userList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        tv_addMember.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final List<User>[] usersCompany = new List[]{new ArrayList<>()};
                final List<User> finalUserList = usersCompany[0];
                userController.getAllUser(new UserController.UserFetchCallback() {
                    @Override
                    public void onUserFetch(List<User> users) {
                        finalUserList.addAll(users);
                    }
                });
                userController.getAllUserByProject(wsID,project.getId(),new UserController.UserFetchCallback() {
                    @Override
                    public void onUserFetch(List<User> users) {
                        showUserSelectionDialog(finalUserList,users);
                    }
                });
            }
        });

        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
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
            if (filteredIndices.contains(i) && checkedItems[i]) {
                usersListView.setItemChecked(i, checkedItems[i]);
            }
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
    private void openGallery() {
        listener.onItemClick(0);
    }
    private void uploadImage(DatabaseReference databaseReference) {
        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(context);
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
                                    databaseReference.child("timeS").setValue(String.valueOf(Util.convertToTimestamp(tvStartDateTime.getText().toString())));
                                    databaseReference.child("timeE").setValue(String.valueOf(Util.convertToTimestamp(tvEndDateTime.getText().toString())));
                                    databaseReference.child("name").setValue(nameProject.getText().toString());
                                    databaseReference.child("des").setValue(desProject.getText().toString());
                                    databaseReference.child("img").setValue(linkToIMGFirebase);
                                    for (User user:memberList){
                                        databaseReference.child("users").child(user.getId()).setValue(""+user.getWork());
                                        Util.sendNotification(user.getId(),"Bạn có thông báo mới","Bạn đã được thêm vào dự án "+nameProject.getText().toString(),context);
                                    }
                                    HomeFragment.loadWorkspaceData(context);
                                }
                            });
                            Toast.makeText(context, "Uploaded", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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
            String key = databaseReference.getKey();
            databaseReference.child("id").setValue(key);
            databaseReference.child("timeS").setValue(String.valueOf(startCalendar.getTimeInMillis()));
            databaseReference.child("timeE").setValue(String.valueOf(endCalendar.getTimeInMillis()));
            databaseReference.child("name").setValue(nameProject.getText().toString());
            databaseReference.child("des").setValue(desProject.getText().toString());
            databaseReference.child("img").setValue(linkToIMGFirebase);
            for (User user:memberList){
                databaseReference.child("users").child(user.getId()).setValue(user.getWork());
                Util.sendNotification(user.getId(),"Bạn có thông báo mới","Bạn đã được thêm vào dự án "+nameProject.getText().toString(),context);
            }
            HomeFragment.loadWorkspaceData(context);
        }
    }
    public void SaveDataProject(Dialog dialog){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("project");
        if (!tvStartDateTime.getText().equals("Start time")
                && !tvEndDateTime.getText().equals("End time") && nameProject.getText()!=null && desProject.getText()!=null){
            boolean kt= true;
            if(memberList.size()==0){
                kt=false;
            }
            if (kt){
                databaseReference=databaseReference.child(wsID).push();
                uploadImage(databaseReference);
                dialog.dismiss();
            }else{
                Toast.makeText(context,R.string.toast_complete_information,Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context,R.string.toast_complete_information,Toast.LENGTH_SHORT).show();
        }
    }
    private String formatTime(long timeInMillis) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date resultDate = new Date(timeInMillis);
        return sdf.format(resultDate);
    }
}
