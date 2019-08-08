package com.san.app.merchant.services;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.san.app.merchant.utils.Pref;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by Dharmesh on 19/2/2019.
 */

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "FCM Service";
    String order_id = "";
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d("MyNotification", remoteMessage.getNotification().toString());
        Log.d("MyNotification", remoteMessage.getNotification().toString());
        Log.e("WWW", "remoteMessage : : " + remoteMessage.getData().toString());

        Log.e(TAG, "Notification Message Body : " + remoteMessage.getNotification().getBody());

        Map<String, String> params = remoteMessage.getData();
        JSONObject object = new JSONObject(params);
        order_id = object.optString("order_id");

        if (remoteMessage.getData().size() > 0) {
            //handle the data message here
        }

        //getting the title and the body
        String title = remoteMessage.getNotification().getTitle();
        String body = remoteMessage.getNotification().getBody();

        //then here we can use the title and body to build a notification
        MyNotificationManager.getInstance(getApplicationContext()).displayNotification(title, body,order_id);
        updateMyActivity(this,"notification","notification");
    }

    void updateMyActivity(Context context, String type, String From) {
        Pref.setValue(this,"badge",Pref.getValue(this,"badge",0)+1);
        Intent intent = new Intent(From);
        //put whatever data you want to send, if any
        intent.putExtra("type", type);
        //send broadcast
        context.sendBroadcast(intent);
    }
}
