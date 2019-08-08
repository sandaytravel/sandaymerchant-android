package com.san.app.merchant.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.san.app.merchant.R;
import com.san.app.merchant.model.BookingModel;
import com.san.app.merchant.model.NotificationModel;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class NotificationListAdapter extends RecyclerView.Adapter<NotificationListAdapter.MyViewHolder> {

    Context mContext;
    List<NotificationModel.Payload> notificationList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvCustomerName,tvMessage,tvTime;
        CircleImageView imgProfile;
        public MyViewHolder(View view) {
            super(view);
            tvCustomerName = (TextView)view.findViewById(R.id.tvCustomerName);
            tvMessage = (TextView)view.findViewById(R.id.tvMessage);
            tvTime = (TextView)view.findViewById(R.id.tvTime);
            imgProfile = (CircleImageView) view.findViewById(R.id.imgProfile);
        }
    }

    public NotificationListAdapter(Context mContext, ArrayList<NotificationModel.Payload> notificationList) {
        this.mContext = mContext;
        this.notificationList = notificationList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_notification_list,parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        NotificationModel.Payload model = notificationList.get(position);

        holder.tvCustomerName.setText(model.getCustomreName());

        holder.tvMessage.setText(model.getMessage());
        holder.tvTime.setText(model.getTimeAgo());
        if (model.getProfilePic().equalsIgnoreCase("") || model.getProfilePic().equalsIgnoreCase(null) || model.getProfilePic().equals(null)){
            holder.imgProfile.setImageDrawable(ContextCompat.getDrawable(mContext,R.mipmap.splash_bg));
        }else {
            Glide.with(mContext)
                    .load(model.getProfilePic())
                    .apply(new RequestOptions().placeholder(R.color.app_theme_dark)
                            .error(R.drawable.button_bg))
                    .into(holder.imgProfile);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
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
