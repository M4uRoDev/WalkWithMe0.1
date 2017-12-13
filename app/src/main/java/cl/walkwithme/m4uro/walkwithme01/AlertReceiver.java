package cl.walkwithme.m4uro.walkwithme01;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.NotificationCompat;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AlertReceiver extends Service {
    public static final int notification_id = 1;

    public void onCreate() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }


    public int onStartCommand(final Intent intent, int flags, int startId) {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                if(consultarAlertas().equals("error")){
                    Toast.makeText(getApplicationContext(), "Credenciales incorrectas", Toast.LENGTH_LONG).show();
                }else {
                    String[] parts = consultarAlertas().split(":");
                    String idalerta = parts[0];
                    String tipoalerta = parts[1];
                    String nombreapellido = parts[2];
                    Intent intent = new Intent(AlertReceiver.this, AlertResponse.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(AlertReceiver.this,0,intent,0);

                    dataApp.nombreAdulto = nombreapellido;
                    dataApp.tAlerta = tipoalerta;
                    dataApp.idAlerta = idalerta;

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(AlertReceiver.this);
                    builder.setSmallIcon(R.mipmap.ic_launcher);
                    builder.setContentIntent(pendingIntent);
                    builder.setAutoCancel(true);
                    builder.setVibrate(new long[] {1000,1000,1000,1000});
                    builder.setTicker("Alerta de "+ tipoalerta +"! - Walk With Me");
                    builder.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.mipmap.ic_launcher));
                    builder.setContentTitle("Alerta de "+ tipoalerta +"!");
                    builder.setContentTitle("El adulto "+nombreapellido+" ha sufrido una alerta");
                    builder.setSubText("Toque para atender alerta");

                    NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.notify(notification_id,builder.build());
                }
            }

        };


        new Thread(new Runnable(){
            public void run() {
                // TODO Auto-generated method stub
                while(true)
                {
                    try {
                        Thread.sleep(10000);
                        handler.sendEmptyMessage(0);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }
        }).start();
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Servicio monitor detenido!", Toast.LENGTH_SHORT).show();
    }

    protected String consultarAlertas(){
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("idredapoyo", dataApp.idredapoyo)
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server+"alert")
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = okHttpClient.newCall(request).execute();
            String responseStr = response.body().string();
            return responseStr;
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
