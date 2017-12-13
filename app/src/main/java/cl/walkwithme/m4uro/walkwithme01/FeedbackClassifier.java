package cl.walkwithme.m4uro.walkwithme01;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.ScratchBank;

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

import static android.R.color.darker_gray;
import static android.R.color.holo_red_dark;
import static java.lang.Math.round;

public class FeedbackClassifier extends AppCompatActivity {
    String datas = "";
    Button caminar, correr, saltar, reposo, acostarse_pararse, sentarse_pararse, salir;
    TextView label, porcentaje, evento;
    ImageView alertColor;
    File dataFeedBack;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback_classifier);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        if (ContextCompat.checkSelfPermission(FeedbackClassifier.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(FeedbackClassifier.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(FeedbackClassifier.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if (ContextCompat.checkSelfPermission(FeedbackClassifier.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(FeedbackClassifier.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(FeedbackClassifier.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        caminar = (Button) findViewById(R.id.button15);
        correr = (Button) findViewById(R.id.button16);
        saltar = (Button) findViewById(R.id.button17);
        reposo = (Button) findViewById(R.id.button18);
        acostarse_pararse = (Button) findViewById(R.id.button19);
        sentarse_pararse = (Button) findViewById(R.id.button20);
        salir = (Button) findViewById(R.id.button21);

        label = (TextView) findViewById(R.id.textView16);
        porcentaje = (TextView) findViewById(R.id.textView17);
        evento = (TextView) findViewById(R.id.textView19);

        alertColor = (ImageView) findViewById(R.id.imageView2);

        caminar.setVisibility(View.INVISIBLE);
        correr.setVisibility(View.INVISIBLE);
        saltar.setVisibility(View.INVISIBLE);
        reposo.setVisibility(View.INVISIBLE);
        sentarse_pararse.setVisibility(View.INVISIBLE);
        acostarse_pararse.setVisibility(View.INVISIBLE);


        final Bean wwmeDevice = (Bean) getIntent().getExtras().get("connect");
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                super.handleMessage(msg);
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {

                        final CountDownTimer timer = new CountDownTimer(8000, 200) {
                            public void onTick(long millisUntilFinished) {
                                wwmeDevice.setLed(dataApp.green);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x() + "," + result.y() + "," + result.z() + "," + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()) + "\n";
                                    }
                                });

                            }

                            public void onFinish() {
                                guardarData("dataLive", datas);
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
                wwmeDevice.connect(FeedbackClassifier.this, wwmeDeviceListener);

            }

        };

        new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                while (true) {
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

        caminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDataFeedback("caminar", dataFeedBack);
            }
        });

        correr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDataFeedback("correr", dataFeedBack);
            }
        });

        saltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDataFeedback("saltar", dataFeedBack);
            }
        });

        reposo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDataFeedback("reposo",dataFeedBack );
            }
        });

        sentarse_pararse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDataFeedback("sentarse-pararse",dataFeedBack );
            }
        });

        acostarse_pararse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarDataFeedback("acostarse-pararse",dataFeedBack );
            }
        });

        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();
            }
        });
    }


    protected void guardarData(String actionName, String datas) {
        File Root = Environment.getExternalStorageDirectory();
        File dir = new File(Root + "/csvFiles");
        if (!dir.exists()) {
            dir.mkdir();
        }
        File file = new File(dir, actionName + ".csv");
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(datas.getBytes());
            fileOutputStream.close();
            dataFeedBack = file;
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mac", dataApp.macDevice)
                    .addFormDataPart("action", "classifier")
                    .addFormDataPart("file", actionName + ".csv", RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server + "/csvFiles/ClassifierServer.php")
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = okHttpClient.newCall(request).execute();
            String responseStr = response.body().string();
            if (!responseStr.isEmpty()) {
                String[] parts = responseStr.split(",");
                String event = parts[0];
                String porcent = parts[1];
                porcent = porcent.substring(1, porcent.length() - 1);
                evento.setText(event);
                Double porcentDouble = Double.parseDouble(porcent) * 100;
                porcentaje.setText(round(porcentDouble) + "%");

                if (porcentDouble <= 35) {
                    alertColor.setBackgroundColor(Color.argb(255, 255, 0, 0));
                    label.setText("Ups, creo que me he equivocado \n" +
                            "¿Puedes decirme que acción realizaste?");
                    caminar.setVisibility(View.VISIBLE);
                    correr.setVisibility(View.VISIBLE);
                    saltar.setVisibility(View.VISIBLE);
                    reposo.setVisibility(View.VISIBLE);
                    sentarse_pararse.setVisibility(View.VISIBLE);
                    acostarse_pararse.setVisibility(View.VISIBLE);
                } else if (porcentDouble > 35 && porcentDouble < 50) {
                    alertColor.setBackgroundColor(Color.argb(255, 255, 137, 0));
                    label.setText("Corrigeme si me equivoco...");
                    caminar.setVisibility(View.VISIBLE);
                    correr.setVisibility(View.VISIBLE);
                    saltar.setVisibility(View.VISIBLE);
                    reposo.setVisibility(View.VISIBLE);
                    sentarse_pararse.setVisibility(View.VISIBLE);
                    acostarse_pararse.setVisibility(View.VISIBLE);
                } else if (porcentDouble > 50){
                    alertColor.setBackgroundColor(Color.argb(255, 0, 255, 0));
                    label.setText("Prediciendo...");
                    caminar.setVisibility(View.INVISIBLE);
                    correr.setVisibility(View.INVISIBLE);
                    saltar.setVisibility(View.INVISIBLE);
                    reposo.setVisibility(View.INVISIBLE);
                    sentarse_pararse.setVisibility(View.INVISIBLE);
                    acostarse_pararse.setVisibility(View.INVISIBLE);
                }

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void guardarDataFeedback(String actionName, File file) {
        caminar.setVisibility(View.INVISIBLE);
        correr.setVisibility(View.INVISIBLE);
        saltar.setVisibility(View.INVISIBLE);
        reposo.setVisibility(View.INVISIBLE);
        sentarse_pararse.setVisibility(View.INVISIBLE);
        acostarse_pararse.setVisibility(View.INVISIBLE);
        try {

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mac", dataApp.macDevice)
                    .addFormDataPart("action", "uploadFeedback")
                    .addFormDataPart("file", actionName + "_feedback_" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()) + ".csv", RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server + "/csvFiles/UploadFeedback.php")
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = okHttpClient.newCall(request).execute();
            String responseStr = response.body().string();
            Toast.makeText(getApplicationContext(), responseStr, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

