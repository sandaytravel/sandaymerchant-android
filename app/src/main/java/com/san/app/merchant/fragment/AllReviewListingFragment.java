package com.san.app.merchant.fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.san.app.merchant.R;
import com.san.app.merchant.adapter.AllReviewListAdapter;
import com.san.app.merchant.databinding.FragmentAllReviewListingBinding;
import com.san.app.merchant.model.ActivityDetailsModel;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class AllReviewListingFragment extends BaseFragment {
    FragmentAllReviewListingBinding mBinding;
    private String TAG = AllReviewListingFragment.class.getSimpleName();
    Context mContext;
    View rootView;

    private int pageNumber = 1;
    private int activity_id = 0;

    //Review List
    ActivityDetailsModel.Review reviewModel;
    AllReviewListAdapter allReviewListAdapter;
    ArrayList<ActivityDetailsModel.Review> reviewsArrayList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_all_review_listing, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            mLayoutManager=new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            setUp();
            setOnClickListner();
        }

        return rootView;
    }

    private void setOnClickListner() {
        mBinding.rvAllReviewList.addOnScrollListener(new com.san.app.merchant.utils.EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onScrolledToEnd() {
                Log.e("Position", "Last item reached " + pageNumber);
                if (pageNumber != 1) {
                    mBinding.rvAllReviewList.setPadding(0,0,0,50);
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            callAllReviewList(); //get noti list
                        }
                    }, 500);
                }
            }
        });

        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
            }
        });
    }

    private void setUp() {
        if (getArguments() != null) {
            activity_id = getArguments().getInt("activity_id");
        }

        //review  list
        allReviewListAdapter = new AllReviewListAdapter(mContext, reviewsArrayList, 1);
        mBinding.rvAllReviewList.setLayoutManager(mLayoutManager);
        mBinding.rvAllReviewList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvAllReviewList.setAdapter(allReviewListAdapter);

        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callAllReviewList(); //get ctyDetail
            }
        }, 1000);
    }

    private void callAllReviewList() {
        final HashMap<String, String> data = new HashMap<>();
        data.put("activity_id", "" + activity_id);
        data.put("page", "" + pageNumber);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.allActivityReview(data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    Utils.dismissProgress();
                    mBinding.progressBar.setVisibility(View.GONE);
                    mBinding.rvAllReviewList.setPadding(0, 0, 0, 0);
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Log.e(TAG, "res- All Review : " + jsonObject.toString());
                        pageNumber = jsonObject.optInt("page");
                        JSONObject payLoadObj = jsonObject.optJSONObject("payload");
                        Gson gson = new Gson();
                        if (payLoadObj.optJSONArray("reviews").length() > 0) {
                            for (int i = 0; i < payLoadObj.optJSONArray("reviews").length(); i++) {
                                JSONObject dataObj = payLoadObj.optJSONArray("reviews").getJSONObject(i);
                                reviewModel = gson.fromJson(dataObj.toString(), ActivityDetailsModel.Review.class);
                                reviewsArrayList.add(reviewModel);
                            }
                            allReviewListAdapter.notifyDataSetChanged();
                        } else {
                            pageNumber = 0;
                        }

                    } else {
                        mBinding.progressBar.setVisibility(View.GONE);
                        errorBody(response.errorBody());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ErroData", "000  " + t.getMessage());
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

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
                        getActivity().getSupportFragmentManager().popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }

}
