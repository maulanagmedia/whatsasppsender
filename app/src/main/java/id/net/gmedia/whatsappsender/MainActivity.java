package id.net.gmedia.whatsappsender;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.ArrayList;

import id.net.gmedia.whatsappsender.NotificationUtils.InitFirebaseSetting;
import id.net.gmedia.whatsappsender.Utils.MessageUtils;
import id.net.gmedia.whatsappsender.Utils.TimerService;
import id.net.gmedia.whatsappsender.Utils.WhatsappAccessibilityService;

public class MainActivity extends AppCompatActivity {

    public static Context context;
    private EditText edtNomor, edtPesan;
    private Button btnSend;
    public static boolean isActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        if(MessageUtils.listPesan == null) MessageUtils.listPesan = new ArrayList<>();
        context = this;
        isActive = true;
        InitFirebaseSetting.getFirebaseSetting(context);
        MessageUtils.fcmToken = FirebaseInstanceId.getInstance().getToken();
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

            //listPesan.add(new PesanModel(nomor, pesan));
            //sendMessage(nomor, pesan);
        }

        Intent i= new Intent(context, TimerService.class);
        i.putExtra("data", "start");
        context.startService(i);
    }

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

                MessageUtils.sendMessage(context, edtNomor.getText().toString(), edtPesan.getText().toString());
            }
        });
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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        isActive = false;
        super.onDestroy();
    }
}
