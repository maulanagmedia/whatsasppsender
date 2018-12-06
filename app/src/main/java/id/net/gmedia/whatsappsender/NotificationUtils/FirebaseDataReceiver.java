package id.net.gmedia.whatsappsender.NotificationUtils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import id.net.gmedia.whatsappsender.MainActivity;
import id.net.gmedia.whatsappsender.PesanModel;
import id.net.gmedia.whatsappsender.Utils.MessageUtils;

public class FirebaseDataReceiver extends WakefulBroadcastReceiver {

    private final String TAG = "FirebaseDataReceiver";

    public void onReceive(Context context, Intent intent) {

        Bundle dataBundle = intent.getExtras();
        if(dataBundle != null) {

            String nomor = dataBundle.getString("nomor", "");
            String pesan = dataBundle.getString("pesan", "");
            if(!nomor.isEmpty() && !pesan.isEmpty()) MessageUtils.listPesan.add(new PesanModel(nomor,pesan));
        }
    }
}