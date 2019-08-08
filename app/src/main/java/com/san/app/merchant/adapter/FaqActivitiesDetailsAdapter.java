package com.san.app.merchant.adapter;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.san.app.merchant.R;
import com.san.app.merchant.databinding.RowFaqListBinding;
import com.san.app.merchant.model.ActivityDetailsModel;

import java.util.List;

public class FaqActivitiesDetailsAdapter extends RecyclerView.Adapter<FaqActivitiesDetailsAdapter.MyViewHolder> {

    private Context mContext;
    private RowFaqListBinding mBinding;
    private List<ActivityDetailsModel.Faqdetail> payloadList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvQuestion, tvAnswer;
        ImageView imgUpDown;

        public MyViewHolder(View view) {
            super(view);
            tvQuestion = (TextView) itemView.findViewById(R.id.tvQuestion);
            tvAnswer = (TextView) itemView.findViewById(R.id.tvAnswer);
            imgUpDown = (ImageView) itemView.findViewById(R.id.imgUpDown);
        }
    }


    public FaqActivitiesDetailsAdapter(Context mContext, List<ActivityDetailsModel.Faqdetail> restaurantListModelArrayList) {
        this.mContext = mContext;
        payloadList = restaurantListModelArrayList;
    }

    public FaqActivitiesDetailsAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_faq_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ActivityDetailsModel.Faqdetail payloadModel = payloadList.get(position);

        holder.tvQuestion.setText("Q: " + payloadModel.getQuestion());
        holder.tvAnswer.setText("A: " + payloadModel.getAnswer());

        if (payloadModel.isOpen()) {
            holder.tvAnswer.setVisibility(View.VISIBLE);
            holder.imgUpDown.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_up_gray));
        } else {
            holder.tvAnswer.setVisibility(View.GONE);
            holder.imgUpDown.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_down_gray));
        }

    }

    @Override
    public int getItemCount() {
        return payloadList.size();
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