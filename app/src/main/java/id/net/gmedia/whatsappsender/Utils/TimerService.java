package id.net.gmedia.whatsappsender.Utils;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

import id.net.gmedia.whatsappsender.PesanModel;

public class TimerService extends Service {

    private static Timer timer = new Timer();
    private static Timer timerServer = new Timer();
    private Context context;
    private String TAG = "TimerService";
    private int timerGetServer = 15 * 1000; // 5 detik

    public IBinder onBind(Intent arg0)
    {
        return null;
    }

    public void onCreate()
    {
        super.onCreate();

        context = this;
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        @SuppressLint("InvalidWakeLockTag") PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK, "My Tag");
        wl.acquire();
        wl.release();

        startService();
    }

    private void startService()
    {
        timer.scheduleAtFixedRate(new mainTask(), 0, 10000);
        timerServer.scheduleAtFixedRate(new serverTask(), 0, timerGetServer);
    }

    private class mainTask extends TimerTask
    {
        public void run()
        {

            if(MessageUtils.listPesan.size() > 0){

                String nomor = MessageUtils.listPesan.get(0).getNomor();
                String pesan = MessageUtils.listPesan.get(0).getPesan();
                MessageUtils.sendMessage(context, nomor, pesan);
                MessageUtils.listPesan.remove(0);
            }

            //Log.d(TAG, "run: ");
        }
    }

    private class serverTask extends TimerTask
    {
        public void run()
        {

            JSONObject jBody = new JSONObject();
            try {
                jBody.put("sender", MessageUtils.fcmToken);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ApiVolley request = new ApiVolley(context, jBody, "POST", ServerURL.getMessage, new ApiVolley.VolleyCallback() {
                @Override
                public void onSuccess(String result) {

                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getJSONObject("metadata").getString("status");
                        if(status.equals("200")){

                            JSONArray ja = response.getJSONArray("response");

                            for(int i = 0; i < ja.length(); i++){

                                JSONObject jo = ja.getJSONObject(i);
                                String nomor = jo.getString("nomor");
                                String pesan = jo.getString("pesan");
                                if(!nomor.isEmpty() && !pesan.isEmpty()) MessageUtils.listPesan.add(new PesanModel(nomor,pesan));
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(String result) {

                }
            });
        }
    }

    public void onDestroy()
    {
        super.onDestroy();
        Toast.makeText(this, "Service terhenti", Toast.LENGTH_SHORT).show();
    }
}
