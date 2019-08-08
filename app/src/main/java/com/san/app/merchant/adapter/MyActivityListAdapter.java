package com.san.app.merchant.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.san.app.merchant.R;
import com.san.app.merchant.model.MyActivityModel;
import com.san.app.merchant.model.SalesReportModel;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.san.app.merchant.utils.Utils.chageDateFormat;
import static com.san.app.merchant.utils.Utils.getRMConverter;
import static com.san.app.merchant.utils.Utils.getThousandsNotation;
import static com.san.app.merchant.utils.Utils.getThousandsNotationReview;


public class MyActivityListAdapter extends RecyclerView.Adapter<MyActivityListAdapter.MyViewHolder> {

    Context mContext;
    List<MyActivityModel.Payload> myActivityList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvActivityName, tvBookingDate, tvCategoryName, tvDisplayPrice, tvActualPrice, tvTotalRating, tvTotalReview, tvStatus;
        ImageView ivActivity;
        LinearLayout llRating;
        CardView cdStatus;

        public MyViewHolder(View view) {
            super(view);
            tvActivityName = (TextView) view.findViewById(R.id.tvActivityName);
            tvBookingDate = (TextView) view.findViewById(R.id.tvBookingDate);

            tvCategoryName = (TextView) view.findViewById(R.id.tvCategoryName);

            tvDisplayPrice = (TextView) view.findViewById(R.id.tvDisplayPrice);
            tvActualPrice = (TextView) view.findViewById(R.id.tvActualPrice);

            tvTotalRating = (TextView) view.findViewById(R.id.tvTotalRating);
            tvTotalReview = (TextView) view.findViewById(R.id.tvTotalReview);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            ivActivity = (ImageView) view.findViewById(R.id.ivActivity);

            llRating = (LinearLayout) view.findViewById(R.id.llRating);

            cdStatus = (CardView) view.findViewById(R.id.cdStatus);
        }
    }

    public MyActivityListAdapter(Context mContext, ArrayList<MyActivityModel.Payload> myActivityList) {
        this.mContext = mContext;
        this.myActivityList = myActivityList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_my_activity_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MyActivityModel.Payload model = myActivityList.get(position);

        Glide.with(mContext)
                .load(model.getImage())
                .into(holder.ivActivity);

        holder.tvActivityName.setText("" + model.getTitle());
        holder.tvBookingDate.setText("" + chageDateFormat(model.getCreatedDate()));
        holder.tvCategoryName.setText("" + model.getCategory());
        if (TextUtils.isEmpty(model.getDisplayPrice()) && TextUtils.isEmpty(model.getActualPrice())){
            holder.tvDisplayPrice.setVisibility(View.GONE);
        }
        holder.tvDisplayPrice.setText(!TextUtils.isEmpty(model.getDisplayPrice()) ? getRMConverter(0.6f,getThousandsNotation(model.getDisplayPrice())) : getRMConverter(0.6f,getThousandsNotation(model.getActualPrice())));
        holder.tvActualPrice.setText(!TextUtils.isEmpty(model.getDisplayPrice()) ? getThousandsNotation(model.getActualPrice()) : "");
        if(!TextUtils.isEmpty(model.getDisplayPrice()))holder.tvActualPrice.setPaintFlags(holder.tvActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        Log.e("AAAAAAA", "getAverageReview : " + model.getAverageReview());
        Log.e("AAAAAAA", "getTotalReview : " + model.getTotalReview());

        if (model.getAverageReview() > 0 || model.getTotalReview() > 0) {
            holder.llRating.setVisibility(View.VISIBLE);
        } else {
            holder.llRating.setVisibility(View.INVISIBLE);
        }
        holder.tvTotalRating.setText("" + new DecimalFormat("#.#").format(model.getAverageReview()));
        holder.tvTotalReview.setText("(" + getThousandsNotationReview(model.getTotalReview()) + " reviews)");

        //  0 = Pending ,1 = Approve, 2 = Decline (Older)
        // 0 = Draft , 1 = Published (new on 11/FEB )

        if (model.getAdminApprove() == 0) {
            holder.tvStatus.setText("Draft");
            holder.cdStatus.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.draft));
        } else {
            holder.tvStatus.setText("Published");
            holder.cdStatus.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.published));
        }


    }

    @Override
    public int getItemCount() {
        return myActivityList.size();
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
