package com.san.app.merchant.activity;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.san.app.merchant.R;
import com.san.app.merchant.databinding.ActivityForgotPasswordBinding;
import com.san.app.merchant.model.MerchantDateModel;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.utils.FieldsValidator;
import com.san.app.merchant.utils.Pref;
import com.san.app.merchant.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.merchant.utils.Constants.DEVICE_TYPE;
import static com.san.app.merchant.utils.Constants.PREF_APP_TOKEN;

public class ForgotPassword extends BaseActivity {
    ActivityForgotPasswordBinding mBinding;
    private String TAG = getClass().getSimpleName();
    Context mContext;
    boolean isValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
        mContext = ForgotPassword.this;

        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = true;
                if (TextUtils.isEmpty(mBinding.edtEmail.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_email), R.mipmap.red_cross_er);
                } else if (!Utils.isValidEmail(mBinding.edtEmail.getText().toString())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_valid_email), R.mipmap.red_cross_er);
                }

                if (isValid) {
                    Utils.showProgress(mContext);
                    callForgotPasswordAPI(mBinding.edtEmail.getText().toString());
                }
            }
        });
    }

    private void callForgotPasswordAPI(String email) {
        ApiInterface apiService = ApiClient.getClient(ForgotPassword.this).create(ApiInterface.class);

        Log.e(TAG, "ForgotPassword-request : Email : " + email);

        Call<ResponseBody> call = apiService.forgotPassword(email);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        hideSoftKeyboard();
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        Log.e(TAG, "ForgotPassword-response " + jsonObject.toString());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                            }
                        }, 1500);
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
