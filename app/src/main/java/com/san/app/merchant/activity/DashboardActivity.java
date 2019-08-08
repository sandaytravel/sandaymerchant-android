package com.san.app.merchant.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.internal.BottomNavigationItemView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.san.app.merchant.R;
import com.san.app.merchant.databinding.ActivityDashboardBinding;
import com.san.app.merchant.fragment.BookingDetailFragment;
import com.san.app.merchant.fragment.BookingFragment;
import com.san.app.merchant.fragment.MyActivityFragment;
import com.san.app.merchant.fragment.NotificationFragment;
import com.san.app.merchant.fragment.SalesReportFragment;
import com.san.app.merchant.fragment.SettingsFragment;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.network.CategoryModel;
import com.san.app.merchant.utils.Constants;
import com.san.app.merchant.utils.FieldsValidator;
import com.san.app.merchant.utils.Pref;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DashboardActivity extends BaseActivity {
    public ActivityDashboardBinding mBinding;
    public LinearLayout llBottombar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
        setOnClickListener();
        setUp();
    }

    private void setUp() {
        deactiveAllicons(3);
        llBottombar = (LinearLayout) mBinding.llBottombar;
        changeFragment(new BookingFragment());
        if (getIntent() != null) {
            if (!TextUtils.isEmpty(getIntent().getStringExtra("is_from")) && getIntent().getStringExtra("is_from").equals("notification")) {
                BookingDetailFragment fragment = new BookingDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("order_id", getIntent().getStringExtra("order_id"));
                bundle.putString("is_from", "notification");
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        }
    }

    private void setOnClickListener() {
        mBinding.sbSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactiveAllicons(1);
                changeFragment(new SalesReportFragment());
            }
        });
        mBinding.sbCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactiveAllicons(2);
                changeFragment(new MyActivityFragment());
            }
        });
        mBinding.llBooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactiveAllicons(3);
                changeFragment(new BookingFragment());
            }
        });
        mBinding.sbNotification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactiveAllicons(4);
                changeFragment(new NotificationFragment());
            }
        });
        mBinding.sbSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deactiveAllicons(5);
                changeFragment(new SettingsFragment());
            }
        });
    }

    private void deactiveAllicons(int position) {
        callGetCategories();
        mBinding.viewSales.setVisibility(View.GONE);
        mBinding.viewCalender.setVisibility(View.GONE);
        mBinding.viewNotification.setVisibility(View.GONE);
        mBinding.viewSettings.setVisibility(View.GONE);
        if (position == 1) {
            mBinding.viewSales.setVisibility(View.VISIBLE);
            mBinding.sbSales.setChecked(true);
            mBinding.sbCalender.setChecked(false);
            mBinding.sbNotification.setChecked(false);
            mBinding.sbSettings.setChecked(false);
        } else if (position == 2) {
            mBinding.viewCalender.setVisibility(View.VISIBLE);
            mBinding.sbSales.setChecked(false);
            mBinding.sbCalender.setChecked(true);
            mBinding.sbNotification.setChecked(false);
            mBinding.sbSettings.setChecked(false);
        } else if (position == 3) {
            mBinding.sbSales.setChecked(false);
            mBinding.sbCalender.setChecked(false);
            mBinding.sbNotification.setChecked(false);
            mBinding.sbSettings.setChecked(false);
        } else if (position == 4) {
            mBinding.viewNotification.setVisibility(View.VISIBLE);
            mBinding.sbSales.setChecked(false);
            mBinding.sbCalender.setChecked(false);
            mBinding.sbNotification.setChecked(true);
            mBinding.sbSettings.setChecked(false);
        } else if (position == 5) {
            mBinding.viewSettings.setVisibility(View.VISIBLE);
            mBinding.sbSales.setChecked(false);
            mBinding.sbCalender.setChecked(false);
            mBinding.sbNotification.setChecked(false);
            mBinding.sbSettings.setChecked(true);
        }
    }

    private void callGetCategories() {
        Log.e("AAAA", "AUTH TOKEN = " + Pref.getValue(getApplicationContext(), Constants.PREF_APP_TOKEN, ""));
        ApiInterface apiService = ApiClient.getClient(DashboardActivity.this).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.categoriesList(Pref.getValue(getApplicationContext(), Constants.PREF_APP_TOKEN, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        JSONArray jsonPayload = jsonObject.getJSONArray("payload");
                        ArrayList<CategoryModel> categoryModelList = new ArrayList<>();
                        Gson gson = new Gson();
                        for (int i = 0; i < jsonPayload.length(); i++) {
                            categoryModelList.add(gson.fromJson(jsonPayload.getJSONObject(i).toString(), CategoryModel.class));
                        }
                        Pref.setValue(getApplicationContext(), Constants.PREF_CAT_LIST, new Gson().toJson(categoryModelList));
                        Pref.setValue(getApplicationContext(), "badge", jsonObject.optInt("notification_count"));
                        getBadge();
                        Log.e("RES - CategoriesList", " : " + jsonObject.toString());
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
            public void onFailure(Call<ResponseBody> call, Throwable t) {
            }
        });
    }

    public void getBadge() {
        if (Pref.getValue(getApplicationContext(), "badge", 0) > 0) {
            mBinding.tvNotiBadge.setVisibility(View.VISIBLE);
            mBinding.tvNotiBadge.setText(Pref.getValue(getApplicationContext(), "badge", 0) < 10 ? "" + Pref.getValue(getApplicationContext(), "badge", 0) : "+9");
        } else {
            mBinding.tvNotiBadge.setVisibility(View.GONE);
        }
    }

    public void hideShowBottomNav(final boolean isShown) {
        mBinding.getRoot().getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        mBinding.llBottombar.setVisibility(isShown ? View.VISIBLE : View.GONE);
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getApplicationContext().registerReceiver(mMessageReceiver, new IntentFilter("notification"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        getApplicationContext().unregisterReceiver(mMessageReceiver);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onReceive(Context context, Intent intent) {
            getBadge();
        }
    };
}
