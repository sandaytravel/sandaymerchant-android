package com.san.app.merchant.fragment;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.PermissionRequest;
import android.widget.Toast;

import com.blikoon.qrcodescanner.QrCodeActivity;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.DexterError;
import com.karumi.dexter.listener.PermissionRequestErrorListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.san.app.merchant.R;
import com.san.app.merchant.activity.DashboardActivity;
import com.san.app.merchant.activity.ForgotPassword;
import com.san.app.merchant.adapter.NoteListAdapter;
import com.san.app.merchant.adapter.NotificationListAdapter;
import com.san.app.merchant.databinding.FragmentBookingDetailBinding;
import com.san.app.merchant.model.BookingModel;
import com.san.app.merchant.model.NoteModel;
import com.san.app.merchant.model.NotificationModel;
import com.san.app.merchant.network.ApiClient;
import com.san.app.merchant.network.ApiInterface;
import com.san.app.merchant.utils.Constants;
import com.san.app.merchant.utils.FieldsValidator;
import com.san.app.merchant.utils.Pref;
import com.san.app.merchant.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.san.app.merchant.utils.Utils.chageDateFormat;
import static com.san.app.merchant.utils.Utils.getThousandsNotation;
import static com.san.app.merchant.utils.Utils.showProgress;

/**
 * A simple {@link Fragment} subclass.
 */
public class BookingDetailFragment extends BaseFragment {
    private static String TAG = BookingDetailFragment.class.getSimpleName();
    FragmentBookingDetailBinding mBinding;
    Context mContext;
    View rootView;
    BookingModel.Payload bookingPayload;
    Animation animShake;
    private static int REQUEST_CODE_QR_SCAN = 11;
    boolean isValid = true;

    NoteModel noteModel;
    NoteListAdapter mAdapter;
    ArrayList<NoteModel> noteList = new ArrayList<>();
    LinearLayoutManager mLayoutManager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null) {
            mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_booking_detail, container, false);
            rootView = mBinding.getRoot();
            mContext = getActivity();
            requestStoragePermission();
            setUp();
            setOnClickListner();
        }

        return rootView;
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

    private void setOnClickListner() {
        mBinding.imgBackView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSoftKeyboard();

                getFragmentManager().popBackStack();

            }
        });

        mBinding.btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = true;
                if (TextUtils.isEmpty(mBinding.edtNoteDesc.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_note), R.mipmap.red_cross_er);
                }

                if (isValid) {
                    Utils.showProgress(mContext);
                    callSendNoteAPI("New Message for " + bookingPayload.getActivityName(), mBinding.edtNoteDesc.getText().toString());
                    hideSoftKeyboard();
                }
            }
        });
        mBinding.btnApplyReedem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isValid = true;
                if (TextUtils.isEmpty(mBinding.edtreedem.getText().toString().trim())) {
                    isValid = false;
                    customToastError(getString(R.string.error), getString(R.string.please_provide_voucher), R.mipmap.red_cross_er);
                }
                if (isValid) {
                    popupConfirmation(3, mBinding.edtreedem.getText().toString().trim());

                    //  Utils.showProgress(mContext);
                    //  callSendNoteAPI(mBinding.edtNoteTitle.getText().toString(), mBinding.edtNoteDesc.getText().toString());
                }
            }
        });
        mBinding.btnCancelBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupConfirmation(1, "");
            }
        });
        mBinding.btnConfirmBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupConfirmation(2, "");

            }
        });
        mBinding.imgScan.setOnClickListener(new View.OnClickListener() {
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
    }

    private void popupConfirmation(final int status, final String qr) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        if (status == 1) {
            builder.setMessage("Are you sure you want to cancel this booking ?");
        } else if (status == 2) {
            builder.setMessage("Are you sure you want to confirm this booking ?");
        } else {
            builder.setMessage("Are you sure to confirm this redemption ?");
        }

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (status == 3) {
                    callRedeemAPI(qr);
                } else {
                    callBookingAction(status);
                }
                dialog.dismiss();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // do nothing
                dialog.dismiss();
            }
        }).setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    private void callBookingAction(final int status) {
        Utils.showProgress(mContext);
        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);

        Log.e(TAG, "SendBooking Action_REQ : status : " + status);

        Call<ResponseBody> call = apiService.bookingStatus(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), bookingPayload.getOrderId(), status);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        Log.e(TAG, "SendBooking Action : RES : " + jsonObject.toString());
                        bookingPayload.setStatus(status);
                        setLayoutStatus();
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

    private void callSendNoteAPI(String title, String description) {

        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);

        Log.e(TAG, "SendNote-request : title : " + title);
        Log.e(TAG, "SendNote-request : description : " + description);

        Call<ResponseBody> call = apiService.sendBookingNote(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), bookingPayload.getOrderNumber(), title, description);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        Log.e(TAG, "SendNote-response " + jsonObject.toString());
                        // mBinding.edtNoteTitle.setText("");
                        mBinding.edtNoteDesc.setText("");
                        getNoteList();
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

    private void callRedeemAPI(String voucherNumber) {

        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);

        Log.e(TAG, "VoucherRedeem-request : voucherNumber : " + bookingPayload.getOrderNumber());
        Log.e(TAG, "VoucherRedeem-request : voucherNumber : " + voucherNumber);

        Call<ResponseBody> call = apiService.voucherRedeem(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), bookingPayload.getOrderNumber(), voucherNumber);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    mBinding.edtreedem.setText("");
                    Utils.dismissProgress();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        new FieldsValidator(mContext).customToast(jsonObject.optString("message"), R.mipmap.green_yes);
                        Log.e(TAG, "VoucherRedeem-response " + jsonObject.toString());
                        bookingPayload.setIs_redeem(1);
                        setLayoutStatus();
                        hideSoftKeyboard();
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
        animShake = AnimationUtils.loadAnimation(mContext, R.anim.shake);

        if (getArguments() != null) {
            if (!TextUtils.isEmpty(getArguments().getString("is_from")) && getArguments().getString("is_from").equals("notification")) {
                getBookingDetails(getArguments().getString("order_id"));
            } else {
                bookingPayload = new Gson().fromJson(getArguments().getString("booking_payload"), BookingModel.Payload.class);
                fillArea(true);
            }
        }

    }

    private void getBookingDetails(String orderID) {
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
                        bookingPayload = gson.fromJson(jsonObject.optJSONObject("payload").toString(), BookingModel.Payload.class);
                        fillArea(false);
                    } else {
                        errorBody(response.errorBody());
                        mBinding.llMain.setVisibility(View.GONE);
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

    private void fillArea(boolean isDirect) {

        if (bookingPayload != null) {

            if (isDirect)
                mBinding.edtreedem.setText(getArguments().getString("is_from").equalsIgnoreCase("scan") ? bookingPayload.getVoucher_number() : "");

            mBinding.tvCategoryName.setText("" + bookingPayload.getCategory());

            mBinding.tvOrderNo.setText("" + bookingPayload.getOrderNumber());
            mBinding.tvBookingDate.setText("" + chageDateFormat(bookingPayload.getBookingDate()));
            mBinding.tvParticipationDate.setText("" + chageDateFormat(bookingPayload.getParticipationDate()));

            mBinding.tvActivityName.setText("" + bookingPayload.getActivityName());
            mBinding.tvPackageTitle.setText("" + bookingPayload.getPackageTitle());
            String s = "RM " + getThousandsNotation(bookingPayload.getTotalPrice());
            SpannableString ss1 = new SpannableString(s);
            ss1.setSpan(new RelativeSizeSpan(0.7f), 0, 2, 0); // set size

            mBinding.tvTotalPrice.setText(ss1);

            if (bookingPayload.getPackagequantity().size() == 1) {
                mBinding.tvQty.setText("" + bookingPayload.getPackagequantity().get(0).getQuantityName() + " - " + bookingPayload.getPackagequantity().get(0).getQuantity());
            } else {
                StringBuilder commaSepValueBuilder = new StringBuilder();
                for (int i = 0; i < bookingPayload.getPackagequantity().size(); i++) {
                    commaSepValueBuilder.append("" + bookingPayload.getPackagequantity().get(i).getQuantityName() + " - " + bookingPayload.getPackagequantity().get(i).getQuantity());
                    if (i != bookingPayload.getPackagequantity().size() - 1) {
                        commaSepValueBuilder.append("  ");
                    }
                }
                mBinding.tvQty.setText(commaSepValueBuilder);
            }
            setLayoutStatus();

            mBinding.tvPersonName.setText(bookingPayload.getTitle() + " " + bookingPayload.getCustomerName());
            mBinding.tvPersonContact.setText(!TextUtils.isEmpty(bookingPayload.getCustomerContactNumber()) ? bookingPayload.getCustomerContactNumber() : "-");
            mBinding.tvPersonEmail.setText("" + bookingPayload.getCustomerEmail());
            mBinding.tvPersonCountry.setText(bookingPayload.getCountryName());
            mBinding.tvPersonBirthdate.setText("" + chageDateFormat(bookingPayload.getBirth_date()));

            setUpNotes();
        }
    }

    private void setUpNotes() {
        Utils.showProgress(mContext);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getNoteList();
            }
        }, 500);

        mAdapter = new NoteListAdapter(mContext, noteList);
        mBinding.rvNoteList.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        mBinding.rvNoteList.setItemAnimator(new DefaultItemAnimator());
        mBinding.rvNoteList.setAdapter(mAdapter);
    }

    private void getNoteList() {

        ApiInterface apiService = ApiClient.getClient(getActivity()).create(ApiInterface.class);
        Log.e(TAG, "orderID :Note REQ : " + String.valueOf(bookingPayload.getOrderId()));

        Call<ResponseBody> call = apiService.notes(Pref.getValue(mContext, Constants.PREF_APP_TOKEN, ""), String.valueOf(bookingPayload.getOrderId()));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    Utils.dismissProgress();
                    noteList.clear();
                    if (response.isSuccessful()) {
                        String res = response.body().string();
                        JSONObject jsonObject = new JSONObject(res);
                        Log.e(TAG, "Note list : RES : " + jsonObject.toString());

                        JSONArray jsonPayload = jsonObject.getJSONArray("payload");
                        for (int i = 0; i < jsonPayload.length(); i++) {
                            JSONObject jsonSub = jsonPayload.getJSONObject(i);
                            noteList.add(new NoteModel(jsonSub.optString("Note"), jsonSub.optString("date")));
                        }

                        mAdapter.notifyDataSetChanged();

                    } else {
                        // errorBody(response.errorBody());
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

    private void setLayoutStatus() {
        //  0 = Pending ,1 = Cancel, 2 = Confirm, 3 = Expired
        if (bookingPayload.getStatus() == 0) {
            mBinding.llBookingBtn.setVisibility(View.VISIBLE);
            mBinding.llReedem.setVisibility(View.GONE);
            mBinding.llReedemMsg.setVisibility(View.GONE);
            mBinding.tvStatus.setText("Pending");
            mBinding.cdStatus.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.pending));
        } else if (bookingPayload.getStatus() == 1) {
            mBinding.llBookingBtn.setVisibility(View.GONE);
            mBinding.llReedem.setVisibility(View.GONE);
            mBinding.llReedemMsg.setVisibility(View.GONE);
            mBinding.tvStatus.setText("Canceled");
            mBinding.cdStatus.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.cancel));
        } else if (bookingPayload.getStatus() == 2) {
            mBinding.llBookingBtn.setVisibility(View.GONE);
            mBinding.llReedem.setVisibility(bookingPayload.getIs_redeem() == 0 ? View.VISIBLE : View.GONE);
            mBinding.llReedemMsg.setVisibility(bookingPayload.getIs_redeem() == 0 ? View.GONE : View.VISIBLE);
            mBinding.llNewNote.setVisibility(bookingPayload.getIs_redeem() == 0 ? View.VISIBLE : View.GONE);
            mBinding.txtRedeem.setText(bookingPayload.getIs_redeem() == 1 ? "voucher redeemed" : "voucher expired");
            mBinding.imgRedeem.setImageDrawable(ContextCompat.getDrawable(mContext, bookingPayload.getIs_redeem() == 1 ? R.drawable.gift : R.drawable.expire));
            mBinding.tvStatus.setText("Confirmed");
            mBinding.cdStatus.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.confirm));
        } else if (bookingPayload.getStatus() == 3) {
            mBinding.llBookingBtn.setVisibility(View.GONE);
            mBinding.llReedem.setVisibility(View.GONE);
            mBinding.llReedemMsg.setVisibility(View.GONE);
            mBinding.llNewNote.setVisibility(View.GONE);
            mBinding.tvStatus.setText("Expired");
            mBinding.cdStatus.setCardBackgroundColor(ContextCompat.getColor(mContext, R.color.expired));
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        ((DashboardActivity) mContext).hideShowBottomNav(false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (rootView != null) {
            ViewGroup parentViewGroup = (ViewGroup) rootView.getParent();
            if (parentViewGroup != null) {
                parentViewGroup.removeAllViews();
            }
        }
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
            mBinding.edtreedem.setText("" + result);

        } else if (requestCode == 101) { //for runtim permission
            requestStoragePermission();
        }
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
                        FragmentManager fm = getActivity().getSupportFragmentManager();
                        fm.popBackStack();
                        return true;
                    }
                }
                return false;
            }
        });
    }
}
