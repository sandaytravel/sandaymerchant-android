package com.san.app.merchant.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.san.app.merchant.R;
import com.san.app.merchant.model.NoteModel;
import com.san.app.merchant.model.NotificationModel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.MyViewHolder> {

    Context mContext;
    List<NoteModel> noteList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvNote,tvTime;
        public MyViewHolder(View view) {
            super(view);
            tvNote = (TextView)view.findViewById(R.id.tvNote);
            tvTime = (TextView)view.findViewById(R.id.tvTime);

        }
    }

    public NoteListAdapter(Context mContext, ArrayList<NoteModel> noteList) {
        this.mContext = mContext;
        this.noteList = noteList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_note_list,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NoteModel model = noteList.get(position);

        holder.tvNote.setText(model.getNote());
        holder.tvTime.setText(model.getDate());

    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    
}
