package com.san.app.merchant.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.joooonho.SelectableRoundedImageView;
import com.mohan.ribbonview.RibbonView;
import com.san.app.merchant.R;
import com.san.app.merchant.model.BookingModel;
import com.san.app.merchant.model.SalesReportModel;

import java.util.ArrayList;
import java.util.List;

import static com.san.app.merchant.utils.Utils.chageDateFormat;
import static com.san.app.merchant.utils.Utils.getThousandsNotation;


public class SalesReportListAdapter extends RecyclerView.Adapter<SalesReportListAdapter.MyViewHolder> {

    Context mContext;
    List<SalesReportModel.Payload> reportList;

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvActivityName,tvBookingDate,tvParticipationDate,tvOrderNo,tvCustomerName,tvTotalPrice,tvCategoryName;
        RibbonView ribbon;
        public MyViewHolder(View view) {
            super(view);
            tvActivityName = (TextView)view.findViewById(R.id.tvActivityName);
            tvBookingDate = (TextView)view.findViewById(R.id.tvBookingDate);
            tvParticipationDate = (TextView)view.findViewById(R.id.tvParticipationDate);
            ribbon = (RibbonView) view.findViewById(R.id.ribbon);
            tvOrderNo = (TextView)view.findViewById(R.id.tvOrderNo);
            tvCustomerName = (TextView)view.findViewById(R.id.tvCustomerName);
            tvTotalPrice = (TextView)view.findViewById(R.id.tvTotalPrice);
            tvCategoryName = (TextView)view.findViewById(R.id.tvCategoryName);
        }
    }

    public SalesReportListAdapter(Context mContext, ArrayList<SalesReportModel.Payload> reportList) {
        this.mContext = mContext;
        this.reportList = reportList;

    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_sales_report_list, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        SalesReportModel.Payload model = reportList.get(position);

        holder.tvOrderNo.setText(""+model.getOrderNumber());
        holder.tvBookingDate.setText(""+chageDateFormat(model.getBookingDate()));
        holder.tvParticipationDate.setText(""+chageDateFormat(model.getParticipationDate()));

        holder.tvCategoryName.setText(""+model.getCategoryName());
        holder.tvCustomerName.setText(""+model.getCustomerName());

        holder.tvActivityName.setText(""+model.getActivityName());

        String s= "RM " + getThousandsNotation(model.getTotalPrice());
        SpannableString ss1=  new SpannableString(s);
        ss1.setSpan(new RelativeSizeSpan(0.7f), 0,2, 0); // set size
        holder.tvTotalPrice.setText(ss1);

        if (model.getIs_redeem() == 0){
            holder.ribbon.setRibbonFillColor(ContextCompat.getColor(mContext,  R.color.awaiting));
            holder.ribbon.setText("Awaiting");
        }else if (model.getIs_redeem() == 1){
            holder.ribbon.setRibbonFillColor(ContextCompat.getColor(mContext,  R.color.redeem));
            holder.ribbon.setText("Redeemed");
        }else if (model.getIs_redeem() == 2){
            holder.ribbon.setRibbonFillColor(ContextCompat.getColor(mContext,  R.color.expired));
            holder.ribbon.setText("Expired");
        }

    }

    @Override
    public int getItemCount() {
        return reportList.size();
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
