package cl.walkwithme.m4uro.walkwithme01;

import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.ScratchBank;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ClassifierClient extends AppCompatActivity {

    String datas = "";
    TextView predictEvent;
    Button buttonExit;
    String predictShow = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classifier_client);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        predictEvent = (TextView)findViewById(R.id.predictEvent);
        buttonExit = (Button)findViewById(R.id.button3);

        buttonExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        final Bean wwmeDevice = (Bean) getIntent().getExtras().get("connect");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {

                        final CountDownTimer timer = new CountDownTimer(7000, 200) {
                            public void onTick(long millisUntilFinished) {
                                wwmeDevice.setLed(dataApp.green);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x()+","+result.y()+","+result.z()+","+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date())+"\n";
                                    }
                                });

                            }

                            public void onFinish() {
                                guardarData("dataLive",datas);
                                datas = "";
                            }
                        };
                        timer.start();
                    }

                    @Override
                    public void onConnectionFailed() {

                    }

                    @Override
                    public void onDisconnected() {

                    }

                    @Override
                    public void onSerialMessageReceived(byte[] data) {

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
                wwmeDevice.connect(ClassifierClient.this, wwmeDeviceListener);

            }

        };


        new Thread(new Runnable(){
            public void run() {
                // TODO Auto-generated method stub
                while(true)
                {
                    try {
                        Thread.sleep(15000);
                        handler.sendEmptyMessage(0);

                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }

            }
        }).start();

    }
    protected void guardarData(String actionName, String datas){
        File Root = Environment.getExternalStorageDirectory();
        File dir = new File(Root + "/csvFiles");
        if(!dir.exists()){
            dir.mkdir();
        }
        File file = new File(dir, actionName + ".csv");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(datas.getBytes());
            fileOutputStream.close();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mac", dataApp.macDevice)
                    .addFormDataPart("action", "classifier")
                    .addFormDataPart("file", actionName+".csv", RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server+"/csvFiles/ClassifierServer.php")
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = okHttpClient.newCall(request).execute();
            String responseStr = response.body().string();
            String event ="";
            if (!responseStr.isEmpty()) {
                String[] parts = responseStr.split(",");
                event = parts[0];
            }
            predictShow = event;
            predictEvent.setText(predictShow);
        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}