package com.san.app.merchant.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.san.app.merchant.R;
import com.san.app.merchant.activity.DashboardActivity;
import com.san.app.merchant.adapter.BookingListAdapter;
import com.san.app.merchant.databinding.FragmentBookingBinding;
import com.san.app.merchant.model.BookingModel;
import com.san.app.merchant.model.MerchantDateModel;
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

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BookingFragment extends BaseFragment {
    FragmentBookingBinding mBinding;
    private String TAG = getClass().getSimpleName();
    View rootView;
    Context mContext;
    private static int REQUEST_CODE_QR_SCAN = 11;
    String orderNumber = "", customerName = "", catID = "", fromDate = "",
            toDate = "", activityName = "", bookingStatus = "", isRedeem = "";
    int pageNumber;
    boolean continuePaging = true;
    // Listing
    BookingListAdapter mAdapter;
    BookingModel bookingModel;
    ArrayList<BookingModel.Payload> bookingModelsList = new ArrayList<BookingModel.Payload>();
    LinearLayoutManager mLayoutManager;
    ArrayList<CategoryModel> categoryModelList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            requestStoragePermission();
            setOnClickListner();
            mLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            setUp();
        }
        return rootView;
    }

    private void setOnClickListner() {
        mBinding.swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mBinding.swipeContainer.setRefreshing(true);
                bookingModelsList.clear();
                pageNumber = 1;
                callBookingList(); //get cart list data
            }
        });

        mBinding.lnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupFilter();
            }
        });
        mBinding.imgScanVoucher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Dexter.withActivity(getActivity())
                        .withPermissions(
                                Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                // check if all permissions are granted
                                if (report.areAllPermissionsGranted()) {

                                    Intent i = new Intent(getActivity(), QrCodeActivity.class);
                                    startActivityForResult(i, REQUEST_CODE_QR_SCAN);

                                }
                                // check for permanent denial of any permission
                                if (report.isAnyPermissionPermanentlyDenied() || report.getDeniedPermissionResponses().size() > 0) {
                                    // show alert dialog navigating to Settings
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }

                        }).
                        withErrorListener(new PermissionRequestErrorListener() {
                            @Override
                            public void onError(DexterError error) {
                                Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .onSameThread()
                        .check();
            }
        });

        mBinding.edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() > 1) {
                    // getNearByLocation(s.toString(), gpsTracker.getLatitude(), gpsTracker.getLongitude());
                    activityName = s.toString();
                    bookingModelsList.clear();
                    pageNumber = 1;
                    callBookingList();
                    mBinding.imgsearchClose.setVisibility(View.VISIBLE);
                } else {
                    Utils.showProgress(mContext);
                    activityName = s.toString();
                    bookingModelsList.clear();
                    pageNumber = 1;
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            callBookingList(); //get order list
                        }
                    }, 500);

                    mBinding.imgsearchClose.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mBinding.imgsearchClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utils.showProgress(mContext);
                activityName = "";
                bookingModelsList.clear();
                pageNumber = 1;
                mBinding.edSearch.setText("");
                hideSoftKeyboard();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        callBookingList(); //get order list
                    }
                }, 3000);

            }
        });

        mBinding.rvBookingList.addOnItemTouchListener(new RecyclerItemClickListener(mContext, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                Log.e("AAAAAAAA", "Order ID : " + bookingModelsList.get(position).getOrderId());
                hideSoftKeyboard();
                BookingDetailFragment fragment = new BookingDetailFragment();
                Bundle bundle = new Bundle();
                bundle.putString("booking_payload", new Gson().toJson(bookingModelsList.get(position), BookingModel.Payload.class));
                bundle.putString("is_from", "position");
                fragment.setArguments(bundle);
                changeFragment_back(fragment);
            }
        }));
    }

    private void popupFilter() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.setContentView(R.layout.popup_filter_booking);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
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
        final EditText edtActivityName = (EditText) dialog.findViewById(R.id.edtActivityName);
        final EditText edtFromDate = (EditText) dialog.findViewById(R.id.edtFromDate);
        final EditText edtToDate = (EditText) dialog.findViewById(R.id.edtToDate);
        final EditText edtStatus = (EditText) dialog.findViewById(R.id.edtStatus);
        final EditText edtRedeemStatus = (EditText) dialog.findViewById(R.id.edtRedeemStatus);
        final ImageView imgClose = (ImageView) dialog.findViewById(R.id.imgClose);

        final Spinner spCategory = (Spinner) dialog.findViewById(R.id.spCategory);
        Button btnClear = (Button) dialog.findViewById(R.id.btnClear);
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
        edtActivityName.setText("" + activityName);
        edtFromDate.setText("" + fromDate);
        edtToDate.setText("" + toDate);
        //   0 = Pending , 1 = Cancelled, 2 = Confirmed,3 = Expired
        if (bookingStatus.equalsIgnoreCase("0")) {
            edtStatus.setText("Pending");
        } else if (bookingStatus.equalsIgnoreCase("1")) {
            edtStatus.setText("Canceled");
        } else if (bookingStatus.equalsIgnoreCase("2")) {
            edtStatus.setText("Confirmed");
        } else if (bookingStatus.equalsIgnoreCase("3")) {
            edtStatus.setText("Expired");
        } else {
            edtStatus.setText("");
        }

        if (isRedeem.equalsIgnoreCase("1")) {
            edtRedeemStatus.setText("Redeemed");
        } else if (isRedeem.equalsIgnoreCase("2")) {
            edtRedeemStatus.setText("Expired");
        } else if (isRedeem.equalsIgnoreCase("0")) {
            edtRedeemStatus.setText("Awaiting Redeem");
        } else {
            edtRedeemStatus.setText("");
        }

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
        edtStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayStatusPopupWindow(v, edtStatus, edtRedeemStatus);
            }
        });
        edtRedeemStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayRedeemStatusPopupWindow(v, edtRedeemStatus, edtStatus);
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
                //    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
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
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromDate.equalsIgnoreCase("") && toDate.equalsIgnoreCase("") && bookingStatus.equalsIgnoreCase("") && isRedeem.equalsIgnoreCase("")) {
                    bookingModelsList.clear();
                    pageNumber = 1;
                    callBookingList();
                }
                dialog.dismiss();
            }
        });
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                edtBookingID.setText("");
                edtTravellerName.setText("");
                edtCategoryName.setText("");
                edtActivityName.setText("");
                edtFromDate.setText("");
                edtToDate.setText("");
                edtStatus.setText("");
                edtRedeemStatus.setText("");
                orderNumber = "";
                customerName = "";
                catID = "";
                fromDate = "";
                toDate = "";
                activityName = "";
                bookingStatus = "";
                isRedeem = "";
            }
        });

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderNumber = edtBookingID.getText().toString();
                customerName = edtTravellerName.getText().toString();
                // catID = edtCategoryName.getText().toString();
                activityName = edtActivityName.getText().toString();

                //   0 = Pending , 1 = Cancelled, 2 = Confirmed,3 = Expired
                if (edtStatus.getText().toString().equalsIgnoreCase("Pending")) {
                    bookingStatus = "0";
                } else if (edtStatus.getText().toString().equalsIgnoreCase("Canceled")) {
                    bookingStatus = "1";
                } else if (edtStatus.getText().toString().equalsIgnoreCase("Confirmed")) {
                    bookingStatus = "2";
                } else if (edtStatus.getText().toString().equalsIgnoreCase("Expired")) {
                    bookingStatus = "3";
                } else {
                    bookingStatus = "";
                }

                if (edtRedeemStatus.getText().toString().equalsIgnoreCase("Redeemed")) {
                    isRedeem = "1";
                } else if (edtRedeemStatus.getText().toString().equalsIgnoreCase("Expired")) {
                    isRedeem = "2";
                } else if (edtRedeemStatus.getText().toString().equalsIgnoreCase("Awaiting Redeem")) {
                    isRedeem = "0";
                } else {
                    isRedeem = "";
                }

                if (!fromDate.equalsIgnoreCase("") && toDate.equalsIgnoreCase("")) {
                    Toast.makeText(mContext, "Select to date first!", Toast.LENGTH_SHORT).show();
                } else {
                    bookingModelsList.clear();
                    pageNumber = 1;
                    callBookingList();
                    dialog.dismiss();
                }
            }
        });
        dialog.show();
    }

    private void displayRedeemStatusPopupWindow(View anchorView, final EditText edtRedeemStatus, final EditText edtStatus) {

        View layout = getLayoutInflater().inflate(R.layout.popup_status_redeem, null);
        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, 120);
        int measuredHeight = layout.getMeasuredHeight();
        final PopupWindow popup = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, measuredHeight);

        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        popup.setBackgroundDrawable(new BitmapDrawable());
        //popup.showAsDropDown(anchorView, -50, -70);
        popup.showAsDropDown(anchorView);
        final TextView optionRedeem = (TextView) layout.findViewById(R.id.optionRedeem);
        final TextView optionExpired = (TextView) layout.findViewById(R.id.optionExpired);
        final TextView optionAwaiting = (TextView) layout.findViewById(R.id.optionAwaiting);
        optionAwaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtRedeemStatus.setText(optionAwaiting.getText().toString());
                edtStatus.setText("Confirmed");
                bookingStatus = "2";
                popup.dismiss();
            }
        });
        optionRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtRedeemStatus.setText(optionRedeem.getText().toString());
                edtStatus.setText("Confirmed");
                bookingStatus = "2";
                popup.dismiss();
            }
        });

        optionExpired.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtRedeemStatus.setText(optionExpired.getText().toString());
                edtStatus.setText("Confirmed");
                bookingStatus = "2";
                popup.dismiss();
            }
        });
    }

    private void displayStatusPopupWindow(View anchorView, final EditText edtStatus, final EditText edtRedeemStatus) {

        View layout = getLayoutInflater().inflate(R.layout.popup_status_booking, null);
        layout.measure(ViewGroup.LayoutParams.WRAP_CONTENT, 120);
        int measuredHeight = layout.getMeasuredHeight();
        final PopupWindow popup = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT, measuredHeight);

        popup.setOutsideTouchable(true);
        popup.setFocusable(true);

        popup.setBackgroundDrawable(new BitmapDrawable());
        //popup.showAsDropDown(anchorView, -50, -70);
        popup.showAsDropDown(anchorView);
        final TextView optionPending = (TextView) layout.findViewById(R.id.optionPending);
        final TextView optionCancelled = (TextView) layout.findViewById(R.id.optionCancelled);
        final TextView optionConfirmed = (TextView) layout.findViewById(R.id.optionConfirmed);

        optionPending.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtStatus.setText(optionPending.getText().toString());
                edtRedeemStatus.setText("");
                isRedeem = "";
                popup.dismiss();
            }
        });

        optionCancelled.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtStatus.setText(optionCancelled.getText().toString());
                edtRedeemStatus.setText("");
                isRedeem = "";
                popup.dismiss();
            }
        });

        optionConfirmed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edtStatus.setText(optionConfirmed.getText().toString());
                popup.dismiss();
            }
        });

    }

    private void setUp() {

        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNumber = 1;
                callBookingList(); //get order list
            }
        }, 1500);

        //booking list
        mAdapter = new BookingListAdapter(mContext, bookingModelsList);
        mBinding.rvBookingList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvBookingList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvBookingList.setAdapter(mAdapter);
        mBinding.rvBookingList.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {
            @Override
            public void onScrolledToEnd() {
                Log.e("Position", "Last item reached " + pageNumber);
                if (pageNumber != 1 && continuePaging) {

                    mBinding.rvBookingList.setPadding(0, 0, 0, 50);
                    mBinding.progressBar.setVisibility(View.VISIBLE);
                    continuePaging = false;
                    callBookingList(); //get noti list
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            continuePaging = true;
                        }
                    }, 1000);
                }
            }
        });

    }

    private void callBookingList() {
        if (fromDate.equalsIgnoreCase("") && toDate.equalsIgnoreCase("") && bookingStatus.equalsIgnoreCase("") && isRedeem.equalsIgnoreCase("")) {
            mBinding.imgFilter.setColorFilter(ContextCompat.getColor(mContext, R.color.filter_gray), android.graphics.PorterDuff.Mode.SRC_IN);
            Log.e(TAG, "filter clear");
        } else {
            Log.e(TAG, "filter ON");
            mBinding.imgFilter.setColorFilter(ContextCompat.getColor(mContext, R.color.app_theme_dark), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        HashMap<String, String> data = new HashMap<>();
        data.put("page", "" + pageNumber);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Call<BookingModel> call = apiService.merchantBooking(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), data,
                fromDate, toDate, activityName, catID, customerName, bookingStatus, isRedeem, orderNumber);
        call.enqueue(new Callback<BookingModel>() {
            @Override
            public void onResponse(Call<BookingModel> call, Response<BookingModel> response) {
                Utils.dismissProgress();
                mBinding.progressBar.setVisibility(View.GONE);
                mBinding.swipeContainer.setRefreshing(false);
                mBinding.rvBookingList.setPadding(0, 0, 0, 0);
                if (response.body() != null) {
                    bookingModel = response.body();
                    bookingModelsList.addAll(bookingModel.getPayload());
                    mAdapter.notifyDataSetChanged();
                    mBinding.swipeContainer.setVisibility(bookingModelsList.size() > 0 ? View.VISIBLE : View.GONE);
                    mBinding.lnNoData.setVisibility(bookingModelsList.size() > 0 ? View.GONE : View.VISIBLE);

                    if (isParamEmpty()) {
                        mBinding.llSearch.setVisibility(bookingModelsList.size() > 0 ? View.VISIBLE : View.GONE);
                    } else {
                        mBinding.llSearch.setVisibility(View.VISIBLE);
                    }

                    Log.e(TAG, "Bookings : " + bookingModelsList.size());
                    pageNumber = bookingModel.getPage();
                    Log.e(TAG, "ONAPI Page : " + pageNumber);
                } else {
                    mBinding.progressBar.setVisibility(View.GONE);
                    errorBody(response.errorBody());
                }
            }

            @Override
            public void onFailure(Call<BookingModel> call, Throwable t) {
                Log.e(TAG, "error " + t.getMessage());
                Utils.dismissProgress();
            }
        });
    }

    private boolean isParamEmpty() {
        if (orderNumber.equals("") && customerName.equals("") && catID.equals("") && fromDate.equals("") &&
                toDate.equals("") && activityName.equals("") && bookingStatus.equals("") && isRedeem.equals("")) {
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
    public void onPause() {
        super.onPause();
        Log.e(TAG, "OnPause");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode != Activity.RESULT_OK) {
            Log.d(TAG, "COULD NOT GET A GOOD RESULT.");
            if (data == null)
                return;
            //Getting the passed result
            String result = data.getStringExtra("com.blikoon.qrcodescanner.error_decoding_image");
            if (result != null) {
                AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                alertDialog.setTitle("Scan Error");
                alertDialog.setMessage("QR Code could not be scanned");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            return;
        }
        if (requestCode == REQUEST_CODE_QR_SCAN) {
            if (data == null)
                return;
            String result = data.getStringExtra("com.blikoon.qrcodescanner.got_qr_scan_relult");
            callVoucherInfo(result);
        } else if (requestCode == 101) { //for runtim permission
            requestStoragePermission();
        }
    }

    private void callVoucherInfo(String voucherCode) {
        Utils.showProgress(mContext);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Log.e(TAG, "Voucher Code : REQ : " + voucherCode);

        Call<ResponseBody> call = apiService.voucherInfo(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), voucherCode);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Log.e(TAG, "Voucher Code : RES : " + jsonObject.toString());
                        Gson gson = new Gson();
                        BookingModel.Payload bookdetails = gson.fromJson(jsonObject.optJSONObject("payload").toString(), BookingModel.Payload.class);
                        BookingDetailFragment fragment = new BookingDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("booking_payload", new Gson().toJson(bookdetails, BookingModel.Payload.class));
                        bundle.putString("is_from", "scan");
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

    private void requestStoragePermission() {
        Dexter.withActivity(getActivity())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            // getSupportFragmentManager().beginTransaction().replace(R.id.frame, new MainFragment()).commit();
                        }
                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied() || report.getDeniedPermissionResponses().size() > 0) {
                            // show alert dialog navigating to Settings
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<com.karumi.dexter.listener.PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }

                }).
                withErrorListener(new PermissionRequestErrorListener() {
                    @Override
                    public void onError(DexterError error) {
                        Toast.makeText(getActivity(), "Error occurred! ", Toast.LENGTH_SHORT).show();
                    }
                })
                .onSameThread()
                .check();
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
