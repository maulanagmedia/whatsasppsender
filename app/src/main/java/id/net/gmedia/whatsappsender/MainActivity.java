package id.net.gmedia.whatsappsender;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import id.net.gmedia.whatsappsender.NotificationUtils.InitFirebaseSetting;

public class MainActivity extends AppCompatActivity {

    private Context context;
    private EditText edtNomor, edtPesan;
    private Button btnSend;
    public static List<PesanModel> listPesan = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        context = this;
        InitFirebaseSetting.getFirebaseSetting(context);
        String refreshToken = FirebaseInstanceId.getInstance().getToken();
        initUI();
        initEvent();

        if (!isAccessibilityOn (context, WhatsappAccessibilityService.class)) {
            Intent intent = new Intent (Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivity (intent);
        }

        Bundle bundle = getIntent().getExtras();

        if(bundle != null){

            String nomor = bundle.getString("nomor","");
            String pesan =  bundle.getString("pesan", "");

            edtNomor.setText(nomor);
            edtPesan.setText(pesan);

            listPesan.add(new PesanModel(nomor, pesan));
            //sendMessage(nomor, pesan);
        }

        Timer timer = new Timer();
        TimerTask t = new TimerTask() {
            @Override
            public void run() {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if(listPesan.size() > 0){

                            String nomor = listPesan.get(0).getNomor();
                            String pesan = listPesan.get(0).getPesan();
                            sendMessage(nomor, pesan);
                            listPesan.remove(0);
                        }
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(t,1000,2000);    }

    private void initUI() {

        edtNomor = (EditText) findViewById(R.id.edt_nomor);
        edtPesan = (EditText) findViewById(R.id.edt_pesan);
        btnSend = (Button) findViewById(R.id.btn_send);
    }

    private void initEvent() {

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(edtNomor.getText().toString().isEmpty()){

                    edtNomor.setError("Harap diisi");
                    edtNomor.requestFocus();
                    return;
                }else{

                    edtNomor.setError(null);
                }

                if(edtPesan.getText().toString().isEmpty()){

                    edtPesan.setError("Harap diisi");
                    edtPesan.requestFocus();
                    return;
                }else{

                    edtPesan.setError(null);
                }

                sendMessage(edtNomor.getText().toString(), edtPesan.getText().toString());
            }
        });
    }

    private void sendMessage(String number, String messange){

        number = "+62" +number;
        PackageManager packageManager = context.getPackageManager();
        Intent i = new Intent(Intent.ACTION_VIEW);
        try {
            String url = "https://api.whatsapp.com/send?phone=" + number + "&text=" + URLEncoder.encode(messange, "UTF-8") + context.getResources().getString(R.string.whatsapp_suffix);
            i.setPackage("com.whatsapp");
            i.setData(Uri.parse(url));
            if (i.resolveActivity(packageManager) != null) {
                context.startActivity(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isAccessibilityOn (Context context, Class<? extends AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName () + "/" + clazz.getCanonicalName ();
        try {
            accessibilityEnabled = Settings.Secure.getInt (context.getApplicationContext ().getContentResolver (), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {  }

        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter (':');

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString (context.getApplicationContext ().getContentResolver (), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString (settingValue);
                while (colonSplitter.hasNext ()) {
                    String accessibilityService = colonSplitter.next ();

                    if (accessibilityService.equalsIgnoreCase (service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
