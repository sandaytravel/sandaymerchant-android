package com.san.app.merchant.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.joooonho.SelectableRoundedImageView;
import com.mohan.ribbonview.RibbonView;
import com.san.app.merchant.R;
import com.san.app.merchant.model.BookingModel;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.san.app.merchant.utils.Utils.chageDateFormat;
import static com.san.app.merchant.utils.Utils.getThousandsNotation;



public class BookingListAdapter extends RecyclerView.Adapter<BookingListAdapter.MyViewHolder> {

    Context mContext;
    List<BookingModel.Payload> bookingList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvActivityName, tvBookingDate, tvParticipationDate, tvOrderNo, tvCustomerName, tvQty, tvTotalPrice, tvCategoryName, tvStatus;
        CircleImageView ivActivity;
        ImageView imgRedeem;
        LinearLayout llNRedeem;
        CardView cdStatus;
        RibbonView ribbon;

        public MyViewHolder(View view) {
            super(view);
            tvActivityName = (TextView) view.findViewById(R.id.tvActivityName);
            tvBookingDate = (TextView) view.findViewById(R.id.tvBookingDate);
            tvParticipationDate = (TextView) view.findViewById(R.id.tvParticipationDate);
            // tvPackageTitle = (TextView) view.findViewById(R.id.tvPackageTitle);

            tvOrderNo = (TextView) view.findViewById(R.id.tvOrderNo);
            tvCustomerName = (TextView) view.findViewById(R.id.tvCustomerName);
            tvQty = (TextView) view.findViewById(R.id.tvQty);
            tvTotalPrice = (TextView) view.findViewById(R.id.tvTotalPrice);
            tvCategoryName = (TextView) view.findViewById(R.id.tvCategoryName);
            tvStatus = (TextView) view.findViewById(R.id.tvStatus);
            ivActivity = (CircleImageView) view.findViewById(R.id.ivActivity);
            imgRedeem = (ImageView) view.findViewById(R.id.imgRedeem);
            llNRedeem = (LinearLayout) view.findViewById(R.id.llNRedeem);
            ribbon = (RibbonView) view.findViewById(R.id.ribbon);
            cdStatus = (CardView) view.findViewById(R.id.cdStatus);
        }
    }

    public BookingListAdapter(Context mContext, ArrayList<BookingModel.Payload> bookingList) {
        this.mContext = mContext;
        this.bookingList = bookingList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_booking_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        BookingModel.Payload model = bookingList.get(position);

        holder.tvOrderNo.setText("" + model.getOrderNumber());
        holder.tvBookingDate.setText("" + chageDateFormat(model.getBookingDate()));
        holder.tvParticipationDate.setText("" + chageDateFormat(model.getParticipationDate()));
        holder.tvCategoryName.setText("" + model.getCategory());
        holder.tvCustomerName.setText("" + model.getCustomerName());

        Glide.with(mContext)
                .load(model.getActivityImage())
                .into(holder.ivActivity);

        holder.tvActivityName.setText("" + model.getActivityName());

        if (model.getPackagequantity().size() == 1) {
            holder.tvQty.setText("" + model.getPackagequantity().get(0).getQuantityName() + " - " + model.getPackagequantity().get(0).getQuantity());
        } else {
            StringBuilder commaSepValueBuilder = new StringBuilder();
            for (int i = 0; i < model.getPackagequantity().size(); i++) {
                commaSepValueBuilder.append("" + model.getPackagequantity().get(i).getQuantityName() + " - " + model.getPackagequantity().get(i).getQuantity());
                if (i != model.getPackagequantity().size() - 1) {
                    commaSepValueBuilder.append("  ");
                }
            }
            holder.tvQty.setText(commaSepValueBuilder);
        }
        //  0 = Pending ,1 = Cancel, 2 = Confirm,3 = Expired
        if (model.getStatus() == 0) {
            holder.cdStatus.setVisibility(View.VISIBLE);
            holder.llNRedeem.setVisibility(View.GONE);
            holder.tvStatus.setText("Pending");
            holder.cdStatus.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.pending));
        } else if (model.getStatus() == 1) {
            holder.cdStatus.setVisibility(View.VISIBLE);
            holder.llNRedeem.setVisibility(View.GONE);
            holder.tvStatus.setText("Canceled");
            holder.cdStatus.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cancel));
        } else if (model.getStatus() == 2) {
            if (model.getIs_redeem() == 1 || model.getIs_redeem() == 2) {
                holder.cdStatus.setVisibility(View.GONE);
                holder.llNRedeem.setVisibility(View.VISIBLE);
                holder.ribbon.setRibbonFillColor(ContextCompat.getColor(mContext, model.getIs_redeem() == 1 ? R.color.redeem : R.color.expired));
                holder.ribbon.setText(model.getIs_redeem() == 1 ? "Redeemed" : "Expired");
            } else {
                holder.cdStatus.setVisibility(View.VISIBLE);
                holder.llNRedeem.setVisibility(View.GONE);
            }
            holder.tvStatus.setText("Confirmed");
            holder.cdStatus.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.confirm));
        } else if (model.getStatus() == 3) {
            holder.cdStatus.setVisibility(View.VISIBLE);
            holder.llNRedeem.setVisibility(View.GONE);
            holder.tvStatus.setText("Expired");
            holder.cdStatus.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.expired));
        }
        String s = "RM " + getThousandsNotation(model.getTotalPrice());
        SpannableString ss1 = new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(0.7f), 0, 2, 0); // set size

        holder.tvTotalPrice.setText(ss1);
    }

    @Override
    public int getItemCount() {
        return bookingList.size();
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
