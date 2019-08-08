package com.san.app.merchant.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import com.san.app.merchant.R;
import com.san.app.merchant.databinding.ActivityLoginBinding;
import com.san.app.merchant.model.MerchantDateModel;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.utils.Constants;
import com.san.app.merchant.utils.Pref;
import com.san.app.merchant.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.merchant.utils.Constants.DEVICE_TYPE;
import static com.san.app.merchant.utils.Constants.PREF_APP_TOKEN;

public class LoginActivity extends BaseActivity {
    ActivityLoginBinding mBinding;
    Context mContext;
    boolean isValid = true;
    private String TAG = getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        mContext = LoginActivity.this;
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }
                        String token = task.getResult().getToken();
                        Log.e("DeviceToken","000   " + token);
                        Pref.setValue(getApplicationContext(),Constants.TAG_DEVICE_TOKEN,token);
                        // Pref.setValue(SplashActivity.this, Constants.PREF_DEVICE_TOKEN, token);
                    }
                });
        setOnClickListener();
    }

    private void setOnClickListener() {

        mBinding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = true;
                if (TextUtils.isEmpty(mBinding.edtEmail.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_email), R.mipmap.red_cross_er);
                } else if (!Utils.isValidEmail(mBinding.edtEmail.getText().toString())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_valid_email), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtPassword.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_password), R.mipmap.red_cross_er);
                }
                if (isValid) {
                    Utils.showProgress(mContext);
                    callLoginAPI(mBinding.edtEmail.getText().toString(), mBinding.edtPassword.getText().toString());
                }
            }
        });

        mBinding.tvForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ForgotPassword.class));
            }
        });
    }

    private void callLoginAPI(String email, String password) {
        ApiInterface apiService = ApiClient.getClient(LoginActivity.this).create(ApiInterface.class);

        Log.e(TAG, "LogIN-request : TAG_DEVICE_TOKEN : " + Pref.getValue(getApplicationContext(), Constants.TAG_DEVICE_TOKEN,""));
        Log.e(TAG, "LogIN-request : Email : " + email + " \nPass : " + password);

        Call<ResponseBody> call = apiService.merchantLogin(email, password, Pref.getValue(getApplicationContext(), Constants.TAG_DEVICE_TOKEN,""), DEVICE_TYPE);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);

                        Gson gson = new Gson();
                        MerchantDateModel merchantDateModel = gson.fromJson(jsonObject.optJSONObject("payload").toString(), MerchantDateModel.class);
                        new Pref(mContext).setUserInfo(merchantDateModel);
                        Pref.setValue(mContext, PREF_APP_TOKEN, jsonObject.optString("_token"));

                        startActivity(new Intent(mContext, DashboardActivity.class));

                        Log.e(TAG, "LogIN-response " + jsonObject.toString());

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
}
