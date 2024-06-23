package com.example.jobflow.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.jobflow.R;
import com.example.jobflow.model.ChatList;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatList> chatLists;
    private Context context;
    private String user;
    public ChatAdapter(List<ChatList> chatLists, Context context,String UID) {
        this.chatLists = chatLists;
        this.context = context;
        this.user = UID;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.messenger_list_layout,null));
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
        ChatList list2= chatLists.get(position);
        if (list2.getUser().equals(user)){
            holder.opoLayout.setVisibility(View.VISIBLE);
            holder.myLayout.setVisibility(View.GONE);
            holder.opoMessenger.setText(list2.getMsg());
            holder.opoTime.setText(list2.getDate()+" - "+ list2.getTime());
        }else{
            holder.opoLayout.setVisibility(View.GONE);
            holder.myLayout.setVisibility(View.VISIBLE);
            holder.myMessenger.setText(list2.getMsg());
            holder.myTime.setText(list2.getDate()+" - "+ list2.getTime());
        }
    }

    public void updateChat(List<ChatList> chatLists){
        this.chatLists=chatLists;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        if (chatLists!=null){
            return chatLists.size();
        }else {
            return 0;
        }

    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private LinearLayout myLayout,opoLayout;
        TextView myMessenger,opoMessenger,myTime,opoTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            myLayout = (LinearLayout) itemView.findViewById(R.id.chatMyLayout);
            opoLayout = (LinearLayout) itemView.findViewById(R.id.chatOpoLayout);
            myMessenger =(TextView) itemView.findViewById(R.id.chatMyMsgTxt);
            opoMessenger =(TextView) itemView.findViewById(R.id.chatOpoMsgTxt);
            myTime =(TextView) itemView.findViewById(R.id.chatMyTimeTxt);
            opoTime =(TextView) itemView.findViewById(R.id.chatOpoTimeTxt);
        }
    }
}
