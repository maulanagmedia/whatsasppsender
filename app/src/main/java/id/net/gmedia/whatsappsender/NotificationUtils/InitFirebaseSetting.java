package id.net.gmedia.whatsappsender.NotificationUtils;

import android.content.Context;
import android.util.Log;

import com.google.firebase.FirebaseApp;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;


/**
 * Created by Shin on 2/17/2017.
 */

public class InitFirebaseSetting {

    public static String token = "";

    public static void getFirebaseSetting(Context context){
        /*FirebaseOptions options = new FirebaseOptions.Builder()
                .build();*/
        FirebaseApp.initializeApp(context);
        token = FirebaseInstanceId.getInstance().getToken();
        FirebaseMessaging.getInstance().subscribeToTopic("gmedia_whatsappsender");
        String TAG = "FirebaseSetting";
        Log.d(TAG, "Firebase token: " + token);
    }
}
