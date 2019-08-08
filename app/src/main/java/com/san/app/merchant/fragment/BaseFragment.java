package com.san.app.merchant.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.san.app.merchant.R;
import com.san.app.merchant.activity.LoginActivity;
import com.san.app.merchant.utils.FieldsValidator;
import com.san.app.merchant.utils.Pref;

import okhttp3.ResponseBody;


public class BaseFragment extends Fragment {


    public void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getActivity().getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

    }

    public void hideSoftKeyboard() {
        if (getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);

        }
    }

    public void changeFragment_back(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.anim_right, R.anim.anim_left, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        transaction.replace(R.id.frame, targetFragment, "fragment");
        transaction.addToBackStack(null);
        transaction.commit();

    }
    public void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Need Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("GOTO SETTINGS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public void changeFragment_up_bottom(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.bottom_up, 0, 0, R.anim.bottom_down);
        transaction.replace(R.id.frame, targetFragment, "fragment");
        transaction.addToBackStack(null);
        transaction.commit();

    }

    public void changeFragment_left_right(Fragment targetFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.setCustomAnimations(R.anim.activity_slide_in_left, R.anim.nothing, R.anim.nothing, R.anim.activity_slide_out_right);
        transaction.replace(R.id.frame, targetFragment, "fragment");
        transaction.addToBackStack(null);
        transaction.commit();

    }


    public void changeFragment(Fragment targetFragment) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

    }


    public void customToast(String msg, int imgResource) {
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.customtoast, null, false);
        TextView txtmsg = (TextView) v.findViewById(R.id.txtmsg);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        txtmsg.setText(msg);
        img.setImageResource(imgResource);

        //Creating the Toast object
        Toast toast = new Toast(getActivity());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(v);//setting the view of custom toast layout
        toast.show();
    }

    public void customToastError(String status, String msg, int imgResource) {
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.customtoast_error, null, false);
        CardView cardView = v.findViewById(R.id.card_view);

        TextView txtmsg = (TextView) v.findViewById(R.id.txtmsg);
        TextView txtstatus = (TextView) v.findViewById(R.id.txtstatus);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        txtmsg.setText(msg);
        txtstatus.setText(status);
        img.setImageResource(imgResource);

        //Creating the Toast object
        Toast toast = new Toast(getActivity());
        toast.setView(v);//setting the view of custom toast layout
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 100);

        toast.show();
    }

    public void errorBody(ResponseBody responseBody) {

        try {
            String res = responseBody.string();
            JSONObject jsonObject = new JSONObject(res);

            new FieldsValidator(getActivity()).customToast(jsonObject.getString("message"), R.mipmap.cancel_toast_new);
            if (jsonObject.optString("code").equals("500")) {
                Pref.deleteAll(getContext());
                getActivity().finishAffinity();
                startActivity(new Intent(getActivity(), LoginActivity.class));
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }


    public long getDateToTimemillies(String givenDateString) {
        long timeMillies = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date mDate = sdf.parse(givenDateString);
            timeMillies = mDate.getTime();
           // System.out.println("Date in milli :: " + timeInMilliseconds);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return timeMillies;
    }

}
