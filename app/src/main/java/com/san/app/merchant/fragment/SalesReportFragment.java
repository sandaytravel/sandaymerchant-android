package com.san.app.merchant.fragment;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.san.app.merchant.R;
import com.san.app.merchant.activity.DashboardActivity;
import com.san.app.merchant.adapter.SalesReportListAdapter;
import com.san.app.merchant.databinding.FragmentSalesReportBinding;
import com.san.app.merchant.model.BookingModel;
import com.san.app.merchant.model.SalesReportModel;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.network.CategoryModel;
import com.san.app.merchant.utils.Constants;
import com.san.app.merchant.utils.EndlessRecyclerOnScrollListener;
import com.san.app.merchant.utils.FieldsValidator;
import com.san.app.merchant.utils.Pref;
import com.san.app.merchant.utils.RecyclerItemClickListener;
import com.san.app.merchant.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.merchant.utils.Utils.getThousandsNotation;

/**
 * A simple {@link Fragment} subclass.
 */
public class SalesReportFragment extends BaseFragment {
    FragmentSalesReportBinding mBinding;
    private String TAG = getClass().getSimpleName();
    View rootView;
    Context mContext;
    int pageNumber;
    boolean continuePaging = true;
    String orderNumber = "", customerName = "", catID = "", fromDate = "",
            toDate = "";
    // Listing
    SalesReportListAdapter mAdapter;
    SalesReportModel reportModel;
    ArrayList<SalesReportModel.Payload> salesReportModelsList = new ArrayList<SalesReportModel.Payload>();
    ArrayList<CategoryModel> categoryModelList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sales_report, container, false);
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
                salesReportModelsList.clear();
                pageNumber = 1;
                callReportList(); //get cart list data
            }
        });

        mBinding.rvReportList.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onScrolledToEnd() {
                Log.e("Position", "Last item reached " + pageNumber);
                if (pageNumber != 1 && continuePaging) {
                    mBinding.rvReportList.setPadding(0,0,0,50);
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                    continuePaging = false;
                    callReportList();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            continuePaging = true;
                        }
                    }, 1000);
                }
            }
        });

        mBinding.edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar mcurrentDate = Calendar.getInstance();
                int year = mcurrentDate.get(Calendar.YEAR);
                int month = mcurrentDate.get(Calendar.MONTH);
                int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog;
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String myFormat = "yyyy-MM-dd"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        mcurrentDate.set(Calendar.YEAR, year);
                        mcurrentDate.set(Calendar.MONTH, month);
                        mcurrentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        mBinding.edtFromDate.setText(sdf.format(mcurrentDate.getTime()));
                        fromDate = mBinding.edtFromDate.getText().toString();
                        mBinding.tvClearSearch.setVisibility(View.VISIBLE);
                    }
                }, year, month, day);
                if (toDate.length()>0)datePickerDialog.getDatePicker().setMaxDate(getDateToTimemillies(toDate));
                datePickerDialog.setTitle("Select Start Date");
                datePickerDialog.show();
            }
        });

        mBinding.edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromDate.equalsIgnoreCase("")) {
                    Toast.makeText(mContext, "Select from date first!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("AAAAAAAAAAAAA", "fromDate : " + fromDate);
                    final Calendar mcurrentDate = Calendar.getInstance();
                    int year = mcurrentDate.get(Calendar.YEAR);
                    int month = mcurrentDate.get(Calendar.MONTH);
                    int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog;
                    datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                            String myFormat = "yyyy-MM-dd"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                            mcurrentDate.set(Calendar.YEAR, year);
                            mcurrentDate.set(Calendar.MONTH, month);
                            mcurrentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            mBinding.edtToDate.setText(sdf.format(mcurrentDate.getTime()));
                            toDate = mBinding.edtToDate.getText().toString();
                        }
                    }, year, month, day);

                    datePickerDialog.getDatePicker().setMinDate(getDateToTimemillies(fromDate));

                    datePickerDialog.setTitle("Select End Date");
                    datePickerDialog.show();
                }
            }
        });

        mBinding.btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!fromDate.equalsIgnoreCase("") || !toDate.equalsIgnoreCase("")) {
                    salesReportModelsList.clear();
                    pageNumber = 1;
                    callReportList();
                } else {
                    Toast.makeText(mContext, "Select date first!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mBinding.tvClearSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBinding.edtFromDate.setText("");
                mBinding.edtToDate.setText("");
                fromDate = "";
                toDate = "";
                salesReportModelsList.clear();
                pageNumber = 1;
                callReportList();
                mBinding.tvClearSearch.setVisibility(View.GONE);
            }
        });

        mBinding.rvReportList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                hideSoftKeyboard();

                callReportInfo(String.valueOf(salesReportModelsList.get(position).getOrderId()));
                Log.e("AAAAAAAAAAAA","Order ID : "+salesReportModelsList.get(position).getOrderId());
            }
        }));
    }
    private void setUp() {
        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNumber = 1;
                callReportList(); //get order list
            }
        }, 1500);

        //booking list
        mAdapter = new SalesReportListAdapter(mContext, salesReportModelsList);
        mBinding.rvReportList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvReportList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvReportList.setAdapter(mAdapter);
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



    private void callReportList() {
        HashMap<String, String> data = new HashMap<>();
        data.put("page", "" + pageNumber);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<SalesReportModel> call = apiService.merchantBookingReport(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data,
                fromDate, toDate, "", catID, customerName, orderNumber);
        call.enqueue(new Callback<SalesReportModel>() {
            @Override
            public void onResponse(Call<SalesReportModel> call, Response<SalesReportModel> response) {
                Utils.dismissProgress();
                mBinding.progressBar.setVisibility(View.GONE);
                mBinding.swipeContainer.setRefreshing(false);
                mBinding.rvReportList.setPadding(0, 0, 0, 0);
                if (response.body() != null) {
                    reportModel = response.body();
                    if(reportModel.getPayload().size()>0) {
                        salesReportModelsList.addAll(reportModel.getPayload());
                        mAdapter.notifyDataSetChanged();
                       if (isParamEmpty()) {
                            mBinding.lnFilter.setVisibility(salesReportModelsList.size() > 0 ? View.VISIBLE : View.GONE);
                        } else {
                            mBinding.lnFilter.setVisibility(View.VISIBLE);
                        }
                        Log.e(TAG, "Bookings : " + salesReportModelsList.size());
                        String s = "RM " + getThousandsNotation(reportModel.getTotal_earn());
                        SpannableString ss1 = new SpannableString(s);
                        ss1.setSpan(new RelativeSizeSpan(0.7f), 0, 2, 0); // set size
                        mBinding.tvTotalEarn.setText(ss1);
                        pageNumber = reportModel.getPage();
                    }else{
                        pageNumber=1;
                    }

                    mBinding.swipeContainer.setVisibility(salesReportModelsList.size() > 0 ? View.VISIBLE : View.GONE);
                    mBinding.llTotal.setVisibility(salesReportModelsList.size() > 0 ? View.VISIBLE : View.GONE);
                    mBinding.lnNoData.setVisibility(salesReportModelsList.size() > 0 ? View.GONE : View.VISIBLE);

                    Log.e(TAG, "ONAPI Page : " + pageNumber);
                } else {
                    mBinding.progressBar.setVisibility(View.GONE);
                    errorBody(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<SalesReportModel> call, Throwable t) {
                mBinding.progressBar.setVisibility(View.GONE);
            }
        });
    }

    private boolean isParamEmpty() {
        if (orderNumber.equals("") && customerName.equals("") && catID.equals("") && fromDate.equals("") && toDate.equals("")) {
            return true;
        } else {
            return false;
        }
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


    /*private void popupFilter() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.popup_filter_sales_report);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        dialog.getWindow().setGravity(Gravity.CENTER);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        Type type = new TypeToken<ArrayList<CategoryModel>>() {
        }.getType();

        categoryModelList = new Gson().fromJson(Pref.getValue(getActivity(), Constants.PREF_CAT_LIST, ""), type);
        List<String> catList = new ArrayList<>();


        final EditText edtBookingID = (EditText) dialog.findViewById(R.id.edtBookingID);
        final EditText edtTravellerName = (EditText) dialog.findViewById(R.id.edtTravellerName);
        final EditText edtCategoryName = (EditText) dialog.findViewById(R.id.edtCategoryName);
        final EditText edtFromDate = (EditText) dialog.findViewById(R.id.edtFromDate);
        final EditText edtToDate = (EditText) dialog.findViewById(R.id.edtToDate);
        final Spinner spCategory = (Spinner) dialog.findViewById(R.id.spCategory);
        final TextView tvClearAll = (TextView) dialog.findViewById(R.id.tvClearAll);
        Button btnCancel = (Button) dialog.findViewById(R.id.btnCancel);
        Button btnDone = (Button) dialog.findViewById(R.id.btnDone);

        if (categoryModelList.size() > 0)
            for (int i = 0; i < categoryModelList.size(); i++) {
                if (catID.equalsIgnoreCase("" + categoryModelList.get(i).category_id)) {
                    Log.e("SELECTED", "CATEGORY NAME : : " + categoryModelList.get(i).getCategory_name());
                    //  et_project.setText(Project.get(i).getProjectName());
                    final int finalI = i;
                    spCategory.post(new Runnable() {
                        public void run() {
                            spCategory.setSelection(finalI);
                        }
                    });
                    spCategory.setSelection(i);
                }

                catList.add("" + categoryModelList.get(i).category_name);
                Log.e("SELECTED", "CategoryId :" + catID);
            }

        ArrayAdapter CatarrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.spinner_category_item,
                catList);
        CatarrayAdapter.setDropDownViewResource(R.layout.spinner_category_item);
        spCategory.setAdapter(CatarrayAdapter);

        edtBookingID.setText("" + orderNumber);
        edtTravellerName.setText("" + customerName);
        edtFromDate.setText("" + fromDate);
        edtToDate.setText("" + toDate);
        edtCategoryName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spCategory.performClick();
            }
        });

        spCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                catID = "" + categoryModelList.get(position).getCategory_id();
                edtCategoryName.setText(categoryModelList.get(position).getCategory_name());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        edtFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar mcurrentDate = Calendar.getInstance();
                int year = mcurrentDate.get(Calendar.YEAR);
                int month = mcurrentDate.get(Calendar.MONTH);
                int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog;
                datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        String myFormat = "yyyy-MM-dd"; //In which you need put here
                        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                        mcurrentDate.set(Calendar.YEAR, year);
                        mcurrentDate.set(Calendar.MONTH, month);
                        mcurrentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        edtFromDate.setText(sdf.format(mcurrentDate.getTime()));
                        fromDate = edtFromDate.getText().toString();
                    }
                }, year, month, day);
                // datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
                datePickerDialog.setTitle("Select Start Date");
                datePickerDialog.show();
            }
        });

        edtToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromDate.equalsIgnoreCase("")) {
                    Toast.makeText(mContext, "Select from date first!", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("AAAAAAAAAAAAA", "fromDate : " + fromDate);
                    final Calendar mcurrentDate = Calendar.getInstance();
                    int year = mcurrentDate.get(Calendar.YEAR);
                    int month = mcurrentDate.get(Calendar.MONTH);
                    int day = mcurrentDate.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog;
                    datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                            String myFormat = "yyyy-MM-dd"; //In which you need put here
                            SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

                            mcurrentDate.set(Calendar.YEAR, year);
                            mcurrentDate.set(Calendar.MONTH, month);
                            mcurrentDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                            edtToDate.setText(sdf.format(mcurrentDate.getTime()));
                            toDate = edtToDate.getText().toString();
                        }
                    }, year, month, day);

                    datePickerDialog.getDatePicker().setMinDate(getDateToTimemillies(fromDate));
                    datePickerDialog.setTitle("Select End Date");
                    datePickerDialog.show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        tvClearAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtBookingID.setText("");
                edtTravellerName.setText("");
                edtCategoryName.setText("");

                edtFromDate.setText("");
                edtToDate.setText("");
                fromDate = "";
                toDate = "";

                orderNumber = "";
                customerName = "";
                catID = "";
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderNumber = edtBookingID.getText().toString();
                customerName = edtTravellerName.getText().toString();
                salesReportModelsList.clear();
                pageNumber = 1;
                callReportList(); //get cart list data

                dialog.dismiss();
            }
        });
        dialog.show();

    }
*/
}
