package com.san.app.merchant.activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

import com.san.app.merchant.R;
import com.san.app.merchant.utils.FieldsValidator;
import okhttp3.ResponseBody;


public class BaseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        StatusBar();
    }


    public void StatusBar() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        }

    }

    public void changeFragment(Fragment targetFragment) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.frame, targetFragment, "fragment")
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

    }

    public void changeFragment_back(Fragment targetFragment) {
        FragmentManager mFragmentManager = getSupportFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.frame, targetFragment, "fragment")
                .addToBackStack(null)
                .setTransitionStyle(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();

    }



    public void hideSoftKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);

        }
    }
    public void customToast(String msg, int imgResource) {
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.customtoast, null, false);
        TextView txtmsg = (TextView) v.findViewById(R.id.txtmsg);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        txtmsg.setText(msg);
        img.setImageResource(imgResource);

        //Creating the Toast object
        Toast toast = new Toast(BaseActivity.this);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setView(v);//setting the view of custom toast layout
        toast.show();
    }

    public void customToastError(String status, String msg, int imgResource) {
        WindowManager.LayoutParams mParams = new WindowManager.LayoutParams();
        LayoutInflater li = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = li.inflate(R.layout.customtoast_error, null, false);
        CardView cardView = v.findViewById(R.id.card_view);

        TextView txtmsg = (TextView) v.findViewById(R.id.txtmsg);
        TextView txtstatus = (TextView) v.findViewById(R.id.txtstatus);
        ImageView img = (ImageView) v.findViewById(R.id.img);
        txtmsg.setText(msg);
        txtstatus.setText(status);
        img.setImageResource(imgResource);

        //Creating the Toast object
        Toast toast = new Toast(BaseActivity.this);
        toast.setView(v);//setting the view of custom toast layout
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.BOTTOM, 0, 100);

        toast.show();
    }

    public void errorBody(ResponseBody responseBody) {

        try {
            String res = responseBody.string();
            JSONObject jsonObject = new JSONObject(res);
            new FieldsValidator(BaseActivity.this).customToast(jsonObject.getString("message"), R.mipmap.cancel_toast_new);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
