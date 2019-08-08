package com.san.app.merchant.fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.san.app.merchant.R;
import com.san.app.merchant.activity.DashboardActivity;
import com.san.app.merchant.adapter.CountryListAdapter;
import com.san.app.merchant.databinding.FragmentMyDetailEditBinding;
import com.san.app.merchant.model.CountryListModel;
import com.san.app.merchant.model.MerchantDateModel;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.utils.Constants;
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


public class MyDetailEditFragment extends BaseFragment {


    //class object declaration..
    FragmentMyDetailEditBinding mBinding;
    View rootView;
    Context mContext;
    private MerchantDateModel merchantDateModel;
    ArrayList<CountryListModel.Payload> countryListModelArrayList = new ArrayList<>();
    ArrayList<CountryListModel.Payload> countryListModelArrayList_Search = new ArrayList<>();
    Dialog listDialog;
    //variable declaration.
    private String TAG = MyDetailEditFragment.class.getSimpleName();
    private boolean isValid = true;
    int selectedIndex = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(
                    inflater, R.layout.fragment_my_detail_edit, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            prepareView();
            setUp();
        }
        return rootView;
    }

    private void prepareView() {
        merchantDateModel = new Pref(mContext).getInfo();
        mBinding.edtName.setText(merchantDateModel.name);
        mBinding.edtCompanyName.setText(merchantDateModel.company_name);
        mBinding.edtWebsite.setText(merchantDateModel.website);
        mBinding.edtCompanySst.setText(merchantDateModel.sst_certificate);
        mBinding.edtCountryName.setText(!TextUtils.isEmpty(merchantDateModel.country_name) ? merchantDateModel.country_name : "Malaysia");
        mBinding.edtCity.setText(merchantDateModel.city_name);
        mBinding.edtCountryCode.setText(!TextUtils.isEmpty(merchantDateModel.country_code) ? merchantDateModel.country_code : "+60");
        mBinding.edtPhone.setText(merchantDateModel.phone);
        mBinding.edtEmail.setText(merchantDateModel.email);
    }


    private void setUp() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Utils.showProgressNormal(mContext);
                callCountryList();
            }
        }, 700);

        mBinding.edtCountryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCountryListDialog(mContext, "country");
            }
        });

        mBinding.edtCountryCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCountryListDialog(mContext, "code");
            }
        });

        mBinding.btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isValid = true;

                if (TextUtils.isEmpty(mBinding.edtName.getText().toString().trim())) {//name
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_name), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtCompanyName.getText().toString().trim())) { //Company Name
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_company_name), R.mipmap.red_cross_er);
                } else if (TextUtils.isEmpty(mBinding.edtWebsite.getText().toString().trim())) { //website
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_website_url), R.mipmap.red_cross_er);
                } else if (!Utils.isValidWebsite(mBinding.edtWebsite.getText().toString())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_valid_website), R.mipmap.red_cross_er);

                } else if (TextUtils.isEmpty(mBinding.edtCompanySst.getText().toString().trim())) { //Company SST
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_company_sst), R.mipmap.red_cross_er);


                } else if (TextUtils.isEmpty(mBinding.edtCity.getText().toString().trim())) {  //City
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_city_name), R.mipmap.red_cross_er);


                } else if (TextUtils.isEmpty(mBinding.edtCountryName.getText().toString().trim())) { //Country Name
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_country_name), R.mipmap.red_cross_er);


                } else if (TextUtils.isEmpty(mBinding.edtCountryCode.getText().toString().trim())) { //Country Code
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_country_code), R.mipmap.red_cross_er);


                } else if (TextUtils.isEmpty(mBinding.edtPhone.getText().toString().trim())) { //Phone
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_phone_number), R.mipmap.red_cross_er);
                } else if (mBinding.edtPhone.getText().toString().trim().length() < 10 || mBinding.edtPhone.getText().toString().trim().length() > 10) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_valid_phone_number), R.mipmap.red_cross_er);


                } else if (TextUtils.isEmpty(mBinding.edtEmail.getText().toString().trim())) {  //Email
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_email), R.mipmap.red_cross_er);
                } else if (!Utils.isValidEmail(mBinding.edtEmail.getText().toString())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_valid_email), R.mipmap.red_cross_er);
                }


                if (isValid) {
                    hideSoftKeyboard();
                    Utils.showProgress(mContext);
                    callMyDetailsUpdateAPI();
                }
            }
        });


        mBinding.ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager().popBackStack();
                hideSoftKeyboard();
            }
        });


    }

    private void openCountryListDialog(Context mContext, final String type) {
        listDialog = new Dialog(mContext);
        final LayoutInflater li = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        listDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        listDialog.setContentView(R.layout.dialog_search_country);
        listDialog.setCanceledOnTouchOutside(true);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        Window window = listDialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.copyFrom(window.getAttributes());


        listDialog.getWindow().setGravity(Gravity.BOTTOM);
        listDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        final RecyclerView rvCountryList = (RecyclerView) listDialog.findViewById(R.id.rvCountryList);
        final EditText edtSearch = (EditText) listDialog.findViewById(R.id.edtSearch);
        final ImageView imgCancelSearch = (ImageView) listDialog.findViewById(R.id.imgCancelSearch);
        TextView tvCancel = (TextView) listDialog.findViewById(R.id.tvCancel);
        final LinearLayout lnNoFilterData = (LinearLayout) listDialog.findViewById(R.id.lnNoFilterData);
        edtSearch.setHint(type.equals("country") ? "Search Country" : "Search Country/Code");
        setCountryList(1, rvCountryList, type);
        edtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 0) searchCountry(edtSearch, lnNoFilterData, rvCountryList, type);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    imgCancelSearch.setVisibility(View.VISIBLE);
                } else {
                    setCountryList(1, rvCountryList, type);
                    imgCancelSearch.setVisibility(View.GONE);
                }
            }
        });

        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    searchCountry(edtSearch, lnNoFilterData, rvCountryList, type);
                    hideSoftKeyboard();
                    return true;
                }
                return false;
            }
        });

        imgCancelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtSearch.setText("");
                setCountryList(1, rvCountryList, type);
                hideSoftKeyboard();
            }
        });

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listDialog.dismiss();
            }
        });

        rvCountryList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (type.equals("country")) {
                    mBinding.edtCountryName.setText(countryListModelArrayList_Search.size() > 0 ? "" + countryListModelArrayList_Search.get(position).getCountryName() : "" + countryListModelArrayList.get(position).getCountryName());
                } else {
                    mBinding.edtCountryCode.setText(countryListModelArrayList_Search.size() > 0 ? "+" + countryListModelArrayList_Search.get(position).getCountryCode() : "+" + countryListModelArrayList.get(position).getCountryCode());
                }
                listDialog.dismiss();
            }
        }));

        listDialog.show();
    }


    private void searchCountry(EditText edtSearch, LinearLayout lnNoFilterData, RecyclerView rvCountryList, String type) {
        if (edtSearch.getText().toString().trim().length() > 0) {
            countryListModelArrayList_Search.clear();
            for (int i = 0; i < countryListModelArrayList.size(); i++) {
                if (type.equalsIgnoreCase("country")) {
                    if (countryListModelArrayList.get(i).getCountryName().toLowerCase().contains(edtSearch.getText().toString().toLowerCase())) {
                        countryListModelArrayList_Search.add(countryListModelArrayList.get(i));
                    }
                } else {
                    if (countryListModelArrayList.get(i).getCountryName().toLowerCase().contains(edtSearch.getText().toString().toLowerCase()) || countryListModelArrayList.get(i).getCountryCode().toString().contains(edtSearch.getText().toString().toLowerCase())) {
                        countryListModelArrayList_Search.add(countryListModelArrayList.get(i));
                    }
                }
            }
            lnNoFilterData.setVisibility(countryListModelArrayList_Search.size() == 0 ? View.VISIBLE : View.GONE);
            setCountryList(0, rvCountryList, type);
        }
    }

    private void setCountryList(int i, RecyclerView rvCountryList, String type) {
//country list
        final CountryListAdapter countryListAdapter;
        String typeValue = type.equals("country") ? "country" : "code";
        if (i == 1) {
            countryListAdapter = new CountryListAdapter(mContext, countryListModelArrayList, typeValue);
        } else {
            countryListAdapter = new CountryListAdapter(mContext, countryListModelArrayList_Search, typeValue);
        }
        rvCountryList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        rvCountryList.setItemAnimator(new DefaultItemAnimator());
        rvCountryList.setAdapter(countryListAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
    }

    private void callMyDetailsUpdateAPI() {

        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<ResponseBody> call = apiService.updateprofile(
                Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), mBinding.edtName.getText().toString(),
                mBinding.edtCompanyName.getText().toString(), mBinding.edtWebsite.getText().toString(), mBinding.edtCountryName.getText().toString(),
                mBinding.edtCity.getText().toString(), mBinding.edtCountryCode.getText().toString(), mBinding.edtPhone.getText().toString(),
                mBinding.edtCompanySst.getText().toString());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Utils.dismissProgress();
                try {
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Gson gson = new Gson();
                        MerchantDateModel merchantDateModel = gson.fromJson(jsonObject.optJSONObject("payload").toString(), MerchantDateModel.class);
                        new Pref(mContext).setUserInfo(merchantDateModel);
                        Toast.makeText(mContext, "" + jsonObject.optString("message"), Toast.LENGTH_SHORT).show();
                        getActivity().getSupportFragmentManager().popBackStack();
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
                Utils.dismissProgress();
            }
        });
    }

    private void callCountryList() {
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<CountryListModel> call = apiService.getcountrylist();
        call.enqueue(new Callback<CountryListModel>() {
            @Override
            public void onResponse(Call<CountryListModel> call, Response<CountryListModel> response) {
                Utils.dismissProgress();
                if (response.body() != null) {
                    CountryListModel countryListModel = response.body();
                    countryListModelArrayList.addAll(countryListModel.getPayload());
                } else {
                    errorBody(response.errorBody());
                }


            }

            @Override
            public void onFailure(Call<CountryListModel> call, Throwable t) {
                Log.e(TAG, "error " + t.getMessage());
                Utils.dismissProgress();
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
                        getActivity().getSupportFragmentManager().popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
