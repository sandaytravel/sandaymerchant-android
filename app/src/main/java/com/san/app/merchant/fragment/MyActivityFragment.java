package com.san.app.merchant.fragment;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.google.gson.Gson;
import com.san.app.merchant.R;
import com.san.app.merchant.activity.DashboardActivity;
import com.san.app.merchant.adapter.BookingListAdapter;
import com.san.app.merchant.adapter.MyActivityListAdapter;
import com.san.app.merchant.databinding.FragmentMyactivityBinding;
import com.san.app.merchant.model.BookingModel;
import com.san.app.merchant.model.MyActivityModel;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.network.CategoryModel;
import com.san.app.merchant.utils.Constants;
import com.san.app.merchant.utils.EndlessRecyclerOnScrollListener;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class MyActivityFragment extends BaseFragment {
    FragmentMyactivityBinding mBinding;
    private String TAG = getClass().getSimpleName();
    View rootView;
    Context mContext;
    String searchTerms = "", category = "", location = "", statusActivity = "";
    int pageNumber = 1;
    boolean continuePaging = true;
    // Listing
    MyActivityListAdapter mAdapter;
    MyActivityModel myActivityModel;
    ArrayList<MyActivityModel.Payload> myActivityModelList = new ArrayList<MyActivityModel.Payload>();
    LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_myactivity, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setUp();

            setOnClickListner();
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(true);
    }

    private void setOnClickListner() {
    }

    private void setUp() {
        mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        //booking list
        mAdapter = new MyActivityListAdapter(mContext, myActivityModelList);
        mBinding.rvMyActivityList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvMyActivityList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvMyActivityList.setAdapter(mAdapter);
        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                callMyActivityList(); //get order list
            }
        }, 100);

        mBinding.rvMyActivityList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                ActivityDetailsFragment fragment = new ActivityDetailsFragment();
                Bundle bundle = new Bundle();
                bundle.putInt("activity_id", myActivityModelList.get(position).getId());
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        }));

        mBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.swipeContainer.setRefreshing(true);
                myActivityModelList.clear();
                pageNumber = 1;
                callMyActivityList(); //get cart list data
            }
        });

        mBinding.nestedMain.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {
            @Override
            public void onScrollChanged() {
                View view = (View) mBinding.nestedMain.getChildAt(mBinding.nestedMain.getChildCount() - 1);

                int diff = (view.getBottom() - (mBinding.nestedMain.getHeight() + mBinding.nestedMain.getScrollY()));

                if (diff == 0) {
                    // your pagination code
                    if (pageNumber != 1 && continuePaging) {

                        mBinding.swipeContainer.setPadding(0, 0, 0, mBinding.progressBar.getHeight());
                        mBinding.progressBar.setVisibility(View.VISIBLE);
                        continuePaging = false;
                        callMyActivityList();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                continuePaging = true;
                            }
                        }, 1000);

                    }
                }
            }
        });

    }

    private void callMyActivityList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("page", "" + pageNumber);
        Log.e(TAG, "page : " + pageNumber);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<MyActivityModel> call = apiService.merchantActivityList(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data, searchTerms, category, location, statusActivity);
        call.enqueue(new Callback<MyActivityModel>() {
            @Override
            public void onResponse(Call<MyActivityModel> call, Response<MyActivityModel> response) {
                Utils.dismissProgress();
                mBinding.progressBar.setVisibility(View.GONE);
                mBinding.lnMain.setVisibility(View.VISIBLE);
                mBinding.swipeContainer.setRefreshing(false);
                mBinding.swipeContainer.setPadding(0, 0, 0, 0);
                if (response.body() != null) {
                    myActivityModel = response.body();
                    Log.e(TAG, "myActivityModel size : " + myActivityModel.getPayload().size());
                    if (myActivityModel.getPayload().size() > 0) {
                        pageNumber = myActivityModel.getPage();
                        myActivityModelList.addAll(myActivityModel.getPayload());
                        mAdapter.notifyDataSetChanged();
                    } else {
                        pageNumber = 1;
                    }
                    mBinding.rvMyActivityList.setVisibility(myActivityModelList.size() > 0 ? View.VISIBLE : View.GONE);
                    mBinding.lnNoData.setVisibility(myActivityModelList.size() > 0 ? View.GONE : View.VISIBLE);
                    Log.e("TestData", "1111   " + myActivityModelList.size());
                } else {

                    errorBody(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<MyActivityModel> call, Throwable t) {
            }
        });
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
                        getActivity().finishAffinity();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
