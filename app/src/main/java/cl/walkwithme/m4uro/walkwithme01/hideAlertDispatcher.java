package cl.walkwithme.m4uro.walkwithme01;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.ScratchBank;

import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class hideAlertDispatcher extends Service {
    private StringBuilder sb = new StringBuilder();
    String texto = null;
    @Override
    public void onCreate() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    public int onStartCommand(final Intent intent, int flags, int startId) {
        final Bean wwmeDevice = (Bean) intent.getExtras().get("connect");
        if(wwmeDevice.getDevice().getAddress().equals("C4:BE:84:E4:EC:75")){
            Toast.makeText(getApplicationContext(), "Modo silencioso trabajando.", Toast.LENGTH_LONG).show();

            final BeanListener wwmeDeviceListener = new BeanListener() {
                @Override
                public void onConnected() {
                }

                @Override
                public void onConnectionFailed() {

                }

                @Override
                public void onDisconnected() {

                }

                @Override
                public void onSerialMessageReceived(byte[] data) {
                    String s = new String(data);
                    texto = s;

                    sb.append(s);                                                // append string
                    int endOfLineIndex = sb.indexOf("\r\n");                            // determine the end-of-line
                    if (endOfLineIndex > 0) {                                            // if end-of-line,
                        String sbprint = sb.substring(0, endOfLineIndex);               // extract string
                        sb.delete(0, sb.length());                                      // and clear
                        if(sbprint.equals("CAIDA")) {
                            for(int i=1;i<10;i++) {
                                wwmeDevice.setLed(dataApp.red);
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.red);
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.red);
                            }
                            wwmeDevice.setLed(dataApp.off);
                            generarAlerta();
                        }
                    }
                }

                @Override
                public void onScratchValueChanged(ScratchBank bank, byte[] value) {

                }

                @Override
                public void onError(BeanError error) {

                }

                @Override
                public void onReadRemoteRssi(int rssi) {

                }
            };
            wwmeDevice.connect(hideAlertDispatcher.this, wwmeDeviceListener);
        }else{
            Toast.makeText(getApplicationContext(), "Dispositivo no compatible con esta funcionalidad",Toast.LENGTH_LONG).show();
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Servicio detenido!", Toast.LENGTH_SHORT).show();
    }

    public hideAlertDispatcher() {
    }

    protected String generarAlerta(){
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mac", dataApp.macDevice)
                    .addFormDataPart("tipoalerta", "Caída")
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server+"alert/registrar_alerta")
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = okHttpClient.newCall(request).execute();
            String responseStr = response.body().string();
            Log.e("error", responseStr);
            return responseStr;
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error";
    }
}
