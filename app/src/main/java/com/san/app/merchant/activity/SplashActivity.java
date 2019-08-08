package com.san.app.merchant.activity;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.san.app.merchant.R;
import com.san.app.merchant.databinding.ActivitySplashBinding;
import com.san.app.merchant.utils.Constants;
import com.san.app.merchant.utils.Pref;
import io.fabric.sdk.android.Fabric;


public class SplashActivity extends BaseActivity {
    boolean isFinish = false;
    int _splashTime = 3000;
    Handler mSplashHandler;
    ActivitySplashBinding mBinding;
    private Runnable mSplashRunnable = new Runnable() {
        @Override
        public void run() {
            isFinish = true;
            if (!TextUtils.isEmpty(Pref.getValue(SplashActivity.this, Constants.PREF_APP_TOKEN, ""))) {
                startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
            } else {
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            }
            //startActivity(new Intent(SplashActivity.this, LoginActivity.class));
            finish();

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);

      /*  String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.e("Token", "token : " + refreshedToken);
        Pref.setValue(getApplicationContext(),Constants.TAG_DEVICE_TOKEN,refreshedToken);*/
        startAnimations();

        createNotificationChannel();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSplashHandler = new Handler();
        mSplashHandler.postDelayed(mSplashRunnable, _splashTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isFinish) {
            if (mSplashRunnable != null) {
                if (mSplashHandler != null) {
                    mSplashHandler.removeCallbacks(mSplashRunnable);
                }
            }
        }
    }

    private void startAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        mBinding.llSplash.clearAnimation();
        mBinding.llSplash.startAnimation(anim);
    }

    private void createNotificationChannel() {
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

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(Constants.CHANNEL_ID, Constants.CHANNEL_NAME, importance);
            mChannel.setDescription(Constants.CHANNEL_DESCRIPTION);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

}
