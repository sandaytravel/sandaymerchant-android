package com.san.app.merchant.fragment;


import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.san.app.merchant.R;
import com.san.app.merchant.activity.DashboardActivity;
import com.san.app.merchant.activity.ForgotPassword;
import com.san.app.merchant.adapter.BookingListAdapter;
import com.san.app.merchant.adapter.NotificationListAdapter;
import com.san.app.merchant.databinding.FragmentNotificationBinding;
import com.san.app.merchant.model.BookingModel;
import com.san.app.merchant.model.NotificationModel;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.utils.Constants;
import com.san.app.merchant.utils.EndlessRecyclerOnScrollListener;
import com.san.app.merchant.utils.Pref;
import com.san.app.merchant.utils.RecyclerItemClickListener;
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
public class NotificationFragment extends BaseFragment {
    FragmentNotificationBinding mBinding;
    View rootView;
    Context mContext;
    int pageNumber;
    private String TAG = getClass().getSimpleName();
    boolean continuePaging = true;
    NotificationModel notificationModel;
    NotificationListAdapter mAdapter;
    ArrayList<NotificationModel.Payload> notificationModelList = new ArrayList<NotificationModel.Payload>();
    LinearLayoutManager mLayoutManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            setUp();
            setOnClickListner();
        }
        return rootView;
    }

    private void setOnClickListner() {
        mBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.swipeContainer.setRefreshing(true);
                notificationModelList.clear();
                pageNumber = 1;
                callNotificationList(); //get cart list data
            }
        });
        mBinding.rvNotificationList.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onScrolledToEnd() {
                Log.e("Position", "Last item reached " + pageNumber);

                if (pageNumber != 1 && continuePaging) {
                    mBinding.rvNotificationList.setPadding(0, 0, 0, 50);
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                    continuePaging = false;
                    callNotificationList(); //get noti list
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            continuePaging = true;
                        }
                    }, 1000);

                }

            }
        });

        mBinding.rvNotificationList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                hideSoftKeyboard();
                callReportInfo(String.valueOf(notificationModelList.get(position).getOrder_id()));
                Log.e("AAAAAAAAAAAA", "Order ID : " + notificationModelList.get(position).getOrder_id());
            }
        }));

    }

    private void callReportInfo(String orderID) {
        Utils.showProgress(mContext);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Log.e(TAG, "orderID : REQ : " + orderID);

        Call<ResponseBody> call = apiService.reportDetail(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), orderID);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Log.e(TAG, "Report Details : RES : " + jsonObject.toString());
                        Gson gson = new Gson();
                        BookingModel.Payload bookdetails = gson.fromJson(jsonObject.optJSONObject("payload").toString(), BookingModel.Payload.class);
                        BookingDetailFragment fragment = new BookingDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("booking_payload", new Gson().toJson(bookdetails, BookingModel.Payload.class));
                        bundle.putString("is_from", "report");
                        fragment.setArguments(bundle);
                        changeFragment_back(fragment);
                    } else {
                        errorBody(response.errorBody());
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void setUp() {

        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNumber = 1;
                callNotificationList(); //get order list
            }
        }, 1500);

        mAdapter = new NotificationListAdapter(mContext, notificationModelList);
        mBinding.rvNotificationList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvNotificationList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvNotificationList.setAdapter(mAdapter);
    }

    private void callNotificationList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("page", "" + pageNumber);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<NotificationModel> call = apiService.bookingNotification(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<NotificationModel>() {
            @Override
            public void onResponse(retrofit2.Call<NotificationModel> call, Response<NotificationModel> response) {
                Utils.dismissProgress();
                mBinding.progressBar.setVisibility(View.GONE);
                mBinding.swipeContainer.setRefreshing(false);
                mBinding.rvNotificationList.setPadding(0, 0, 0, 0);
                if (response.body() != null) {
                    notificationModel = response.body();
                    if (notificationModel.getPayload().size() > 0) {
                        Log.e(TAG, "ONAPI Page : " + pageNumber);
                        pageNumber = notificationModel.getPage();
                        notificationModelList.addAll(notificationModel.getPayload());
                        mAdapter.notifyDataSetChanged();
                        Pref.setValue(mContext, "badge", 0);
                        ((DashboardActivity) mContext).getBadge();
                    } else {
                        pageNumber = 1;
                    }

                    mBinding.swipeContainer.setVisibility(notificationModelList.size() > 0 ? View.VISIBLE : View.GONE);
                    mBinding.lnNoData.setVisibility(notificationModelList.size() > 0 ? View.GONE : View.VISIBLE);
                    Log.e(TAG, "Notification - response : " + notificationModelList.size());

                } else {
                    mBinding.progressBar.setVisibility(View.GONE);
                    errorBody(response.errorBody());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<NotificationModel> call, Throwable t) {
                mBinding.progressBar.setVisibility(View.GONE);
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "OnResume");
        ((DashboardActivity) mContext).hideShowBottomNav(true);
        Utils.getKeyboardOpenorNot(mContext, rootView, ((DashboardActivity) mContext).mBinding.llBottombar);
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