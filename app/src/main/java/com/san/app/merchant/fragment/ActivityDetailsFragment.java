package com.san.app.merchant.fragment;


import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.san.app.merchant.R;
import com.san.app.merchant.activity.DashboardActivity;
import com.san.app.merchant.adapter.AllReviewListAdapter;
import com.san.app.merchant.adapter.FaqActivitiesDetailsAdapter;
import com.san.app.merchant.adapter.PackageOptionFromActivitiesAdapter;
import com.san.app.merchant.databinding.FragmentActivityDetailsBinding;
import com.san.app.merchant.model.ActivityDetailsModel;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.network.CategoryModel;
import com.san.app.merchant.utils.Constants;
import com.san.app.merchant.utils.Pref;
import com.san.app.merchant.utils.RecyclerItemClickListener;
import com.san.app.merchant.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.merchant.utils.Utils.getRMConverter;
import static com.san.app.merchant.utils.Utils.getThousandsNotation;

/**
 * A simple {@link Fragment} subclass.
 */
public class ActivityDetailsFragment extends BaseFragment {
    FragmentActivityDetailsBinding mBinding;
    private static String TAG = ActivityDetailsFragment.class.getSimpleName();
    View rootView;
    Context mContext;
    int activityId;
    Animation animShake;
    ActivityDetailsModel activityDetailsModel;

    //Review List
    ArrayList<ActivityDetailsModel.Review> reviewsArrayList = new ArrayList<>();
    AllReviewListAdapter allReviewListAdapter;

    //Package List
    ArrayList<ActivityDetailsModel.Packageoption> packageoptionArrayList = new ArrayList<>();
    PackageOptionFromActivitiesAdapter packageOptionFromActivitiesAdapter;

    //FAQs List
    ArrayList<ActivityDetailsModel.Faqdetail> faqListModelArrayList = new ArrayList<>();
    FaqActivitiesDetailsAdapter faqActivitiesDetailsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_activity_details, container, false);
            mContext = getActivity();
            rootView = mBinding.getRoot();
            setUp();
            setOnClickListner();
        }
        return rootView;

    }

    private void setOnClickListner() {
        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStack();
            }
        });

        mBinding.tvReadReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllReviewListingFragment fragment = new AllReviewListingFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("activity_id", activityDetailsModel.getPayload().getBasicdetails().getActivityId());
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        });

        mBinding.rvFaqList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (faqListModelArrayList.get(position).isOpen()) {
                    faqListModelArrayList.get(position).setOpen(false);
                } else {
                    faqListModelArrayList.get(position).setOpen(true);
                }
                faqActivitiesDetailsAdapter.notifyDataSetChanged();
            }
        }));

    }

    private void setUp() {
        animShake = AnimationUtils.loadAnimation(mContext, R.anim.shake);

        if (getArguments() != null) activityId = getArguments().getInt("activity_id");
        Log.e(TAG, "IDD " + activityId);
        changeToolbarView();

        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callGetActivityDetails(); //get order list
            }
        }, 1500);

    }

    private void changeToolbarView() {
        mBinding.appBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = true;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset < 150) {
                    mBinding.tvTitle.setVisibility(View.VISIBLE);
                    mBinding.tvTitle.setText(mBinding.tvActivityName.getText());
                    mBinding.imgBackView.setColorFilter(ContextCompat.getColor(mContext, R.color.black));
                    isShow = true;
                } else if (isShow) {
                    mBinding.tvTitle.setVisibility(View.GONE);
                    mBinding.imgBackView.setColorFilter(ContextCompat.getColor(mContext, R.color.white));
                    isShow = false;
                }
            }
        });
    }

    private void callGetActivityDetails() {

        HashMap<String, String> data = new HashMap<>();
        data.put("activity_id", "" + activityId);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ActivityDetailsModel> call = apiService.viewActivityDetails(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ActivityDetailsModel>() {
            @Override
            public void onResponse(Call<ActivityDetailsModel> call, Response<ActivityDetailsModel> response) {
                Log.e("RES - Activity Details", " : " + response.toString());
                Utils.dismissProgress();
                if (response.body() != null) {
                    activityDetailsModel = response.body();
                    setActivityDetailDataRes(activityDetailsModel.getPayload());
                    mBinding.activityMain.setVisibility(View.VISIBLE);
                    if (activityDetailsModel.getPayload().getPolicydetail().size() > 0)
                        setPolicyDetailView();
                } else {
                    errorBody(response.errorBody());
                }

            }

            @Override
            public void onFailure(Call<ActivityDetailsModel> call, Throwable t) {
            }
        });
    }

    private void setPolicyDetailView() {
        mBinding.lnPolicyDetail.setVisibility(View.VISIBLE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        params.gravity = Gravity.CENTER;

        params.setMargins(5, 5, 5, 5);

        if (activityDetailsModel.getPayload().getPolicydetail().size() > 0) {
            for (int i = 0; i < activityDetailsModel.getPayload().getPolicydetail().size(); i++) {
                ImageView imageView = new ImageView(getActivity());
                imageView.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.circle_full_app_dark));
                TextView textView = new TextView(getActivity());
                textView.setPadding(15, 0, 0, 0);
                textView.setText(activityDetailsModel.getPayload().getPolicydetail().get(i).getName());
                textView.setTextSize(14f);
                textView.setTextColor(ContextCompat.getColor(mContext, R.color.black));
                LinearLayout ll = new LinearLayout(mContext);
                ll.setPadding(0, 12, 0, 12);
                ll.setOrientation(LinearLayout.HORIZONTAL);
                ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                ll.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                textView.setGravity(Gravity.CENTER);
                ll.addView(imageView);
                ll.addView(textView);
                imageView.getLayoutParams().width = 13;
                imageView.getLayoutParams().height = 13;
                mBinding.lnPolicyDetail.addView(ll);
            }
        }
    }

    private void setActivityDetailDataRes(ActivityDetailsModel.Payload payload) {

        // Activity Cover Image
        Glide.with(mContext)
                .load(payload.getBasicdetails().getImage())
                .apply(new RequestOptions().placeholder(R.color.colorPrimary)
                        .error(R.color.colorPrimary))
                .into(mBinding.imgMain);

        //Activity Name and Title
        mBinding.tvActivityName.setText(payload.getBasicdetails().getTitle());
        mBinding.tvSubTitleActivity.setVisibility(!TextUtils.isEmpty(payload.getBasicdetails().getSubtitle()) ? View.VISIBLE : View.GONE);
        mBinding.tvSubTitleActivity.setText(payload.getBasicdetails().getSubtitle());

        //Activity Price
        Log.e("AAAAAAAAAAA", "tvDisplayPrice : " + payload.getBasicdetails().getDisplayPrice());
        if (TextUtils.isEmpty(payload.getBasicdetails().getDisplayPrice()) && TextUtils.isEmpty(payload.getBasicdetails().getActualPrice())){
            mBinding.tvDisplayPrice.setVisibility(View.GONE);
        }
        mBinding.tvDisplayPrice.setText(!TextUtils.isEmpty(payload.getBasicdetails().getDisplayPrice()) ? getRMConverter(0.6f, getThousandsNotation(payload.getBasicdetails().getDisplayPrice())) : getRMConverter(0.6f, getThousandsNotation(payload.getBasicdetails().getActualPrice())));
        mBinding.tvActualPrice.setText(!TextUtils.isEmpty(payload.getBasicdetails().getDisplayPrice()) ? getThousandsNotation(payload.getBasicdetails().getActualPrice()) : "");
        mBinding.tvActualPrice.setPaintFlags(mBinding.tvActualPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        //Basic Details
        setWebViewSetting(mBinding.webDesc, payload.getBasicdetails().getDescription());

        //For Review
        mBinding.lnReviewsMain.setVisibility(payload.getReviews().size() > 0 ? View.VISIBLE : View.GONE);
        mBinding.tvReviewAvg.setText(String.format("%.1f", payload.getBasicdetails().getAverageReview()));
        mBinding.ratingBar.setStepSize(Float.parseFloat("0.01"));
        mBinding.ratingBar.setRating(Float.parseFloat(String.valueOf(payload.getBasicdetails().getAverageReview())));
        mBinding.tvTotalReview.setText("" + payload.getBasicdetails().getTotalReview() + " reviews");

        //For Review List
        reviewsArrayList.addAll(payload.getReviews());
        setReviewList();

        //For Packageing Option List
        mBinding.lnPackageOptionMain.setVisibility(payload.getPackageoptions().size() > 0 ? View.VISIBLE : View.GONE);
        packageoptionArrayList.addAll(payload.getPackageoptions());
        setPackageList();

        // What To Expect
        mBinding.lnWhatMain.setVisibility(!TextUtils.isEmpty(payload.getWhatToExpect().getWhatToExpectDescription().toString()) ? View.VISIBLE : View.GONE);
        setWebViewSetting(mBinding.webWhatToExpect, payload.getWhatToExpect().getWhatToExpectDescription());

        // Activity Information
        mBinding.lnActivityInfoMain.setVisibility(!TextUtils.isEmpty(payload.getActivityInformation().getActivityInformationDescription().toString()) ? View.VISIBLE : View.GONE);
        setWebViewSetting(mBinding.webActivityInfo, payload.getActivityInformation().getActivityInformationDescription());

        //How To Use
        mBinding.lnHowToUseMain.setVisibility(!TextUtils.isEmpty(payload.getHowToUse().getHowToUseDescription().toString()) ? View.VISIBLE : View.GONE);
        setWebViewSetting(mBinding.webHowToUse, payload.getHowToUse().getHowToUseDescription());

        //Cancelation Policy
        mBinding.lnCancelPolicyMian.setVisibility(!TextUtils.isEmpty(payload.getCancellationPolicy().getCancellationPolicyDescription().toString()) ? View.VISIBLE : View.GONE);
        setWebViewSetting(mBinding.webCancelPolicy, payload.getCancellationPolicy().getCancellationPolicyDescription());

        //For FAQs List
        mBinding.lnFAQMain.setVisibility(payload.getFaqdetail().size() > 0 ? View.VISIBLE : View.GONE);
        faqListModelArrayList.addAll(payload.getFaqdetail());
        setFAQsList();

    }

    private void setPackageList() {
        //package list
        packageOptionFromActivitiesAdapter = new PackageOptionFromActivitiesAdapter(mContext, packageoptionArrayList);
        mBinding.rvPackageOptionsList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvPackageOptionsList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvPackageOptionsList.setAdapter(packageOptionFromActivitiesAdapter);
    }

    private void setReviewList() {
        //review  list
        allReviewListAdapter = new AllReviewListAdapter(mContext, reviewsArrayList, 0);
        mBinding.rvReviewsList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvReviewsList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvReviewsList.setAdapter(allReviewListAdapter);
    }

    private void setFAQsList() {
        //faq list
        Log.e(TAG, "faqListModelArrayList : " + faqListModelArrayList.size());
        faqActivitiesDetailsAdapter = new FaqActivitiesDetailsAdapter(mContext, faqListModelArrayList);
        mBinding.rvFaqList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvFaqList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvFaqList.setAdapter(faqActivitiesDetailsAdapter);
    }
    private void setWebViewSetting(WebView webView, String webData) {
        webView.loadDataWithBaseURL("http://localhost",webData, "text/html; video/mpeg", "UTF-8","");
        webView.getSettings().setDefaultFontSize(38);
        webView.setInitialScale(1);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.setVerticalScrollBarEnabled(false);
        webView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.white));
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ViewGroup parentViewGroup = (ViewGroup) rootView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();

        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
