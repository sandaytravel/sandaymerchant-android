package com.san.app.merchant.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.san.app.merchant.R;
import com.san.app.merchant.databinding.RowReviewActivityListBinding;
import com.san.app.merchant.model.ActivityDetailsModel;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;

import static com.san.app.merchant.utils.Utils.chageDateFormat;


public class AllReviewListAdapter extends RecyclerView.Adapter<AllReviewListAdapter.MyViewHolder> {

    private Context mContext;
    private RowReviewActivityListBinding mBinding;
    private int type = 0;
    private ArrayList<ActivityDetailsModel.Review> payloadList;
    private ArrayList<String> imageList = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;

        public MyViewHolder(View view) {
            super(view);

        }
    }


    public AllReviewListAdapter(Context mContext, ArrayList<ActivityDetailsModel.Review> restaurantListModelArrayList, int type) {
        this.mContext = mContext;
        payloadList = restaurantListModelArrayList;
        this.type = type;
    }

    public AllReviewListAdapter(ArrayList<ActivityDetailsModel.Review> myList) {
        payloadList = myList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        mBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.row_review_activity_list, parent, false);

        return new MyViewHolder(mBinding.getRoot());
    }
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final ActivityDetailsModel.Review payloadModel = payloadList.get(position);
        mBinding.view.setVisibility(type == 0 ? View.GONE : View.VISIBLE);
        mBinding.lnReviewImages.setVisibility(payloadModel.getReviewImages().size() > 0 ? View.VISIBLE : View.GONE);
        mBinding.tvUserName.setText(payloadModel.getCustomerName());
        mBinding.tvDesc.setText(payloadModel.getReview());

        mBinding.ratingBar.setRating(Float.parseFloat("" + payloadModel.getRating()));
        mBinding.tvReviewDate.setText(chageDateFormat(payloadModel.getReviewDate()));
        Glide.with(mContext)
                .load(payloadModel.getProfilePic())
                .apply(new RequestOptions().placeholder(R.color.colorPrimary)
                        .error(R.color.colorPrimary))
                .into(mBinding.imgUser);


        if (payloadModel.getReviewImages().size() > 0) {
            mBinding.cardFirst.setVisibility(View.VISIBLE);
            Glide.with(mContext)
                    .load(payloadModel.getReviewImages().get(0).getResizeImage())
                    .apply(new RequestOptions().placeholder(R.color.colorPrimary)
                            .error(R.color.colorPrimary))
                    .into(mBinding.imageFirst);

            if (payloadModel.getReviewImages().size() > 1) {
                mBinding.cardSecond.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(payloadModel.getReviewImages().get(1).getResizeImage())
                        .apply(new RequestOptions().placeholder(R.color.colorPrimary)
                                .error(R.color.colorPrimary))
                        .into(mBinding.imageSecond);
            }
            Log.e("AAAAAAAA" ,"getReviewImages : "+payloadModel.getReviewImages().size());
            if (payloadModel.getReviewImages().size() > 2) {
                mBinding.cardFirstThird.setVisibility(View.VISIBLE);
                Glide.with(mContext)
                        .load(payloadModel.getReviewImages().get(2).getResizeImage())
                        .apply(new RequestOptions().placeholder(R.color.colorPrimary)
                                .error(R.color.colorPrimary))
                        .into(mBinding.imageThird);
                mBinding.tvMoreImages.setVisibility(payloadModel.getReviewImages().size() > 3 ? View.VISIBLE : View.GONE);
                mBinding.tvMoreImages.setText("+" + String.valueOf(payloadModel.getReviewImages().size() - 3));
            }

            mBinding.imageFirst.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageList.clear();
                    for (int i = 0; i < payloadModel.getReviewImages().size(); i++) {
                        imageList.add(payloadModel.getReviewImages().get(i).getFullsizeImage());
                    }
                    ImagePopup((Activity) mContext, imageList, 0);
                }
            });

            mBinding.imageSecond.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageList.clear();
                    for (int i = 0; i < payloadModel.getReviewImages().size(); i++) {
                        imageList.add(payloadModel.getReviewImages().get(i).getFullsizeImage());
                    }
                    ImagePopup((Activity) mContext, imageList, 1);
                }
            });

            mBinding.imageThird.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    imageList.clear();
                    for (int i = 0; i < payloadModel.getReviewImages().size(); i++) {
                        imageList.add(payloadModel.getReviewImages().get(i).getFullsizeImage());
                    }
                    ImagePopup((Activity) mContext, imageList, 2);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return type == 0 ? 1 : payloadList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


    private void ImagePopup(Activity activity, ArrayList<String> imgeselectedList, int i) {

        ArrayList<String> ImagesArray = imgeselectedList;

        ViewPager mPager;
        final int[] currentPage = {0};
        final Dialog dialogS = new Dialog(activity);
        dialogS.setContentView(R.layout.popup_image_show);
        dialogS.setCanceledOnTouchOutside(false);
        dialogS.setCancelable(true);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialogS.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialogS.show();
        dialogS.getWindow().setAttributes(lp);

        dialogS.getWindow().setGravity(Gravity.CENTER);
        dialogS.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        mPager = dialogS.findViewById(R.id.pager);
        mPager.setAdapter(new SlidingImage_Adapter(activity, ImagesArray));
        mPager.setCurrentItem(i);


        ImageView tv_g_cancel = dialogS.findViewById(R.id.tv_close);

        tv_g_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogS.dismiss();
            }
        });

        dialogS.show();
    }
}