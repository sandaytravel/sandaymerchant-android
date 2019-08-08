package com.san.app.merchant.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


import com.san.app.merchant.R;
import com.san.app.merchant.activity.DashboardActivity;
import com.san.app.merchant.utils.Constants;

import java.util.concurrent.atomic.AtomicInteger;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Dharmesh on 19/2/2019.
 */

public class MyNotificationManager {

    private Context mCtx;
    private static MyNotificationManager mInstance;
    private final static AtomicInteger c = new AtomicInteger(0);

    private MyNotificationManager(Context context) {
        mCtx = context;
    }

    public static synchronized MyNotificationManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new MyNotificationManager(context);
        }
        return mInstance;
    }

    public void displayNotification(String title, String body, String order_id) {

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(mCtx, Constants.CHANNEL_ID)
                        .setSmallIcon(R.mipmap.ic_launcherr)
                        .setContentTitle(title)
                        .setAutoCancel(true)
                        .setContentText(body);

        /*
        *  Clicking on the notification will take us to this intent
        *  Right now we are using the MainActivity as this is the only activity we have in our application
        *  But for your project you can customize it as you want
        * */

        Intent resultIntent = new Intent(mCtx, DashboardActivity.class).putExtra("is_from", "notification").putExtra("order_id", "" + order_id);

        /*
        *  Now we will create a pending intent
        *  The method getActivity is taking 4 parameters
        *  All paramters are describing themselves
        *  0 is the request code (the second parameter)
        *  We can detect this code in the activity that will open by this we can get
        *  Which notification opened the activity
        * */
        PendingIntent pendingIntent = PendingIntent.getActivity(mCtx, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        /*
        *  Setting the pending intent to notification builder
        * */

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager mNotifyMgr =
                (NotificationManager) mCtx.getSystemService(NOTIFICATION_SERVICE);

        mNotifyMgr.cancelAll();
/*
* The first parameter is the notification id
* better don't give a literal here (right now we are giving a int literal)
* because using this id we can modify it later
* */
        if (mNotifyMgr != null) {
            mNotifyMgr.notify(getID(), mBuilder.build());
        }
    }

    public int getID() {
        return c.incrementAndGet();
    }

}
