package com.san.app.merchant.adapter;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.san.app.merchant.R;
import com.san.app.merchant.databinding.RowPackageOptionsDetailActivityBinding;
import com.san.app.merchant.model.ActivityDetailsModel;

import java.util.List;

import static com.san.app.merchant.utils.Utils.getRMConverter;
import static com.san.app.merchant.utils.Utils.getThousandsNotation;
import static com.san.app.merchant.utils.Utils.makeTextViewResizable;

public class PackageOptionFromActivitiesAdapter extends RecyclerView.Adapter<PackageOptionFromActivitiesAdapter.MyViewHolder> {

    private Context mContext;
    private RowPackageOptionsDetailActivityBinding mBinding;
    private List<ActivityDetailsModel.Packageoption> payloadList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tvPkgTitle,tvDisplayPrice,tvActualPrice,tvViewMoreLess,tvDesc;

        public MyViewHolder(View view) {
            super(view);
            tvPkgTitle=(TextView)view.findViewById(R.id.tvPkgTitle);
            tvDisplayPrice=(TextView)view.findViewById(R.id.tvDisplayPrice);
            tvActualPrice=(TextView)view.findViewById(R.id.tvActualPrice);
            tvViewMoreLess=(TextView)view.findViewById(R.id.tvViewMoreLess);
            tvDesc=(TextView)view.findViewById(R.id.tvDesc);
        }
    }

    public PackageOptionFromActivitiesAdapter(Context mContext, List<ActivityDetailsModel.Packageoption> restaurantListModelArrayList) {
        this.mContext = mContext;
        for(int i=0;i<restaurantListModelArrayList.size();i++){
            restaurantListModelArrayList.get(i).setExpand(false);
        }
        payloadList = restaurantListModelArrayList;
    }

    public PackageOptionFromActivitiesAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_package_options_detail_activity, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }

    @Override
    public void onBindViewHolder(final MyViewHolder mBinding, final int position) {
        final ActivityDetailsModel.Packageoption payloadModel = payloadList.get(position);
        mBinding.tvPkgTitle.setText(payloadModel.getPackageTitle());
        mBinding.tvDisplayPrice.setText(!TextUtils.isEmpty(payloadModel.getDisplayPrice()) ? getRMConverter(0.6f, getThousandsNotation(payloadModel.getDisplayPrice())) : getRMConverter(0.6f, getThousandsNotation(payloadModel.getActualPrice())));
        mBinding.tvActualPrice.setText(!TextUtils.isEmpty(payloadModel.getDisplayPrice()) ? payloadModel.getActualPrice() : "");
        if (!TextUtils.isEmpty(payloadModel.getDisplayPrice()))
            mBinding.tvActualPrice.setPaintFlags(mBinding.tvActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        if (!TextUtils.isEmpty(payloadModel.getDescription())) {
            if (payloadModel.getDescription().length() > 18) {
                mBinding.tvViewMoreLess.setVisibility(View.VISIBLE);
                if (payloadList.get(position).isExpand()) {
                    payloadList.get(position).setExpand(false);
                    mBinding.tvDesc.setText(Html.fromHtml(payloadModel.getDescription()));
                    mBinding.tvViewMoreLess.setText("View Less");
                } else {
                    payloadList.get(position).setExpand(true);
                    mBinding.tvDesc.setText(Html.fromHtml(payloadModel.getDescription().substring(0, 18)));
                    mBinding.tvViewMoreLess.setText("View More");
                }
            } else {
                mBinding.tvDesc.setText(Html.fromHtml(payloadModel.getDescription()));
                mBinding.tvViewMoreLess.setVisibility(View.GONE);
            }
        }

        mBinding.tvViewMoreLess.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (payloadList.get(position).isExpand()) {
                    payloadList.get(position).setExpand(false);
                    mBinding.tvDesc.setText(Html.fromHtml(payloadModel.getDescription()));
                    mBinding.tvViewMoreLess.setText("View Less");
                } else {
                    payloadList.get(position).setExpand(true);
                    mBinding.tvDesc.setText(Html.fromHtml(payloadModel.getDescription().substring(0, 18)));
                    mBinding.tvViewMoreLess.setText("View More");
                }
            }
        });

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