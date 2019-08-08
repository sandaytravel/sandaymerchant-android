package com.san.app.merchant.activity;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.san.app.merchant.R;
import com.san.app.merchant.databinding.ActivityChangePasswordBinding;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.utils.Constants;

import com.san.app.merchant.utils.FieldsValidator;
import com.san.app.merchant.utils.Pref;
import com.san.app.merchant.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePassword extends BaseActivity {
    ActivityChangePasswordBinding mBinding;
    private String TAG = getClass().getSimpleName();
    Context mContext;
    private boolean isValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        mContext = ChangePassword.this;
        mBinding.ivChangeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mBinding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = true;

                if (TextUtils.isEmpty(mBinding.edtOldPassword.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_old_pwd), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtNewPassword.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_new_pwd), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtConfirmPassword.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_confirm_new_pwd), R.mipmap.red_cross_er);
                } else if (!mBinding.edtNewPassword.getText().toString().equals(mBinding.edtConfirmPassword.getText().toString())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.both_pwd_doest_not_match), R.mipmap.red_cross_er);
                }

                if (isValid) {
                    Utils.showProgress(mContext);
                    callChangePasswordAPI();
                }
            }
        });

    }

    private void callChangePasswordAPI() {

        HashMap<String, String> data = new HashMap<>();
        data.put("old_password", mBinding.edtOldPassword.getText().toString().trim());
        data.put("new_password", mBinding.edtConfirmPassword.getText().toString().trim());
        ApiInterface apiService = ApiClient.getClient(ChangePassword.this).create(ApiInterface.class);
        Log.e(TAG,"Token : "+Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""));
        Log.e(TAG,"Old password : "+ mBinding.edtOldPassword.getText().toString().trim());
        Log.e(TAG,"New password : "+ mBinding.edtConfirmPassword.getText().toString().trim());

        Call<ResponseBody> call = apiService.changePassword(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        finish();
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        Log.e(TAG, "ChangePassword-response " + jsonObject.toString());
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
}
