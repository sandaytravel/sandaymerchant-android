package com.san.app.merchant.fragment;


import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.san.app.merchant.R;
import com.san.app.merchant.activity.ChangePassword;
import com.san.app.merchant.activity.DashboardActivity;
import com.san.app.merchant.activity.LoginActivity;
import com.san.app.merchant.databinding.FragmentSettingsBinding;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.utils.Constants;
import com.san.app.merchant.utils.FieldsValidator;
import com.san.app.merchant.utils.Pref;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends BaseFragment {
    FragmentSettingsBinding mBinding;
    View rootView;
    Context mContext;
    private Dialog dialog1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            setOnClickListener();
        }

        return rootView;
    }

    private void setOnClickListener() {
        mBinding.llmyProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment_back(new MyDetailEditFragment());
            }
        });
        mBinding.llLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog(mContext);
            }
        });
        /*mBinding.llPrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });*/
        mBinding.llChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ChangePassword.class));
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(true);
    }

    private void logoutDialog(final Context mContext) {
        dialog1 = new Dialog(mContext, R.style.PauseDialog);
        LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        dialog1.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog1.setContentView(R.layout.cust_logout_dialog);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog1.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog1.show();
        dialog1.getWindow().setAttributes(lp);
        dialog1.getWindow().setGravity(Gravity.CENTER);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        TextView mYesLogoutTv = (TextView) dialog1.findViewById(R.id.tv_yes);
        TextView mCancelTv = (TextView) dialog1.findViewById(R.id.tv_cancle);
        mYesLogoutTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callLogoutAPI();
                dialog1.dismiss();
            }
        });
        mCancelTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog1.dismiss();
            }
        });
        dialog1.show();
    }

    private void callLogoutAPI() {
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.logout(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        Pref.deleteAll(mContext);
                        getActivity().finishAffinity();
                        startActivity(new Intent(getActivity(), LoginActivity.class));
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
