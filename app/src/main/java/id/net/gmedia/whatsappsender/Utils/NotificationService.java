package id.net.gmedia.whatsappsender.Utils;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;

public class NotificationService extends NotificationListenerService {

    private static final String TAG = "NOTIF";
    Context context;

    @Override

    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

    }
    @Override

    public void onNotificationPosted(StatusBarNotification sbn) {
        String pack = sbn.getPackageName();
        String ticker ="";
        if(sbn.getNotification().tickerText !=null) {
            ticker = sbn.getNotification().tickerText.toString();
        }
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString("android.title");
        String text = extras.getCharSequence("android.text").toString();
        int id1 = extras.getInt(Notification.EXTRA_SMALL_ICON);
        Bitmap id = sbn.getNotification().largeIcon;


        Log.i("Package",pack);
        Log.i("Ticker",ticker);
        Log.i("Title",title);
        Log.i("Text",text);

        Intent msgrcv = new Intent("Msg");
        msgrcv.putExtra("package", pack);
        msgrcv.putExtra("ticker", ticker);
        msgrcv.putExtra("title", title);
        msgrcv.putExtra("text", text);
        if(id != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            id.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            msgrcv.putExtra("icon",byteArray);
        }
        
        saveNotif(title, text);
        LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);
    }

    private void saveNotif(String title, String text) {
        
        JSONObject jBody = new JSONObject();
        try {
            jBody.put("title", title.trim());
            jBody.put("text", text.trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        

        ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.saveBalasan, new ApiVolley.VolleyCallback() {
            @Override
            public void onSuccess(String result) {

                Log.d(TAG, "onSuccess: ");
            }

            @Override
            public void onError(String result) {

                Log.d(TAG, "onError: ");
            }
        });
    }

    @Override

    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");

    }
}