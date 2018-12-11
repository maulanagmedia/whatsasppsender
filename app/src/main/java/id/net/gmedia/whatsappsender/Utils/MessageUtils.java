package id.net.gmedia.whatsappsender.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.view.Window;
import android.view.WindowManager;

import java.net.URLEncoder;
import java.util.List;

import id.net.gmedia.whatsappsender.MainActivity;
import id.net.gmedia.whatsappsender.PesanModel;
import id.net.gmedia.whatsappsender.R;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MessageUtils {

    public static List<PesanModel> listPesan;
    public static String fcmToken = "";

    public static void sendMessage(Context context, String number, String messange){

        MainActivity.wakeUpScreen();

        number = "+62" +number;
        PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode(messange, "UTF-8") + context.getResources().getString(R.string.whatsapp_suffix);
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            i.setFlags(FLAG_ACTIVITY_NEW_TASK);
            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
