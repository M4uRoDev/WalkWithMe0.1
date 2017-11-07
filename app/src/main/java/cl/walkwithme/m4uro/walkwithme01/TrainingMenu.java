package cl.walkwithme.m4uro.walkwithme01;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import okhttp3.Authenticator;
import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;

public class TrainingMenu extends AppCompatActivity {

    Button btnCaminar, btnSaltar, btnCorrer, btnReposo, btnSentarParar, btnAcostarParar;
    String datas = "axisX,axisY,axisZ,time\n";
    TextView label, debug;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_menu);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        btnSentarParar = (Button) findViewById(R.id.btnSentarParar);
        btnSentarParar.setVisibility(View.INVISIBLE);
        btnAcostarParar = (Button) findViewById(R.id.btnAcostarParar);
        btnAcostarParar.setVisibility(View.INVISIBLE);
        label = (TextView)findViewById(R.id.textView4);
        debug = (TextView)findViewById(R.id.textView);
        final Bean wwmeDevice = (Bean) getIntent().getExtras().get("connect");

        if (ContextCompat.checkSelfPermission(TrainingMenu.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(TrainingMenu.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(TrainingMenu.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        if (ContextCompat.checkSelfPermission(TrainingMenu.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(TrainingMenu.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(TrainingMenu.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }
        btnCaminar = (Button)findViewById(R.id.btnCaminar);
        btnCaminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCaminar.setVisibility(View.INVISIBLE);
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        datas = "axisX,axisY,axisZ,time\n";
                        new CountDownTimer(60000, 333) {
                            public void onTick(long millisUntilFinished) {
                                label.setText("Camine... \nSegundos restantes: " + millisUntilFinished / 1000);
                                debug.setText(":" + (millisUntilFinished/1000)%2);
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x()*100+","+result.y()*100+","+result.z()*100+","+ Calendar.getInstance().getTime()+"\n";
                                        //new UpdateTask().execute(100 * result.x(), 100 * result.y(), 100 * result.z());
                                    }
                                });

                            }

                            public void onFinish() {
                                label.setText("Terminado!");
                                btnCaminar.setText("Caminar: OK!");
                                btnCaminar.setVisibility(View.VISIBLE);
                                guardarData("caminando",datas);
                            }
                        }.start();
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
                wwmeDevice.connect(TrainingMenu.this, wwmeDeviceListener);
            }
        });

        btnSaltar = (Button)findViewById(R.id.btnSaltar);
        btnSaltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnSaltar.setVisibility(View.INVISIBLE);
                debug.setVisibility(View.INVISIBLE);
                debug.setText("Salte!");
                Toast.makeText(getApplicationContext(), "Cuando diga Salte!", Toast.LENGTH_LONG).show();
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        datas = "axisX,axisY,axisZ,time\n";
                        new CountDownTimer(60000, 333) {
                            public void onTick(long millisUntilFinished) {
                                label.setText("Segundos restantes: " + millisUntilFinished / 1000);
                                if((millisUntilFinished / 1000)%5 == 0){
                                    debug.setVisibility(View.VISIBLE);
                                }else{
                                    debug.setVisibility(View.INVISIBLE);
                                }
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x() * 100 + "," + result.y() * 100 + "," + result.z() * 100 + "," + Calendar.getInstance().getTime() + "\n";
                                        //new UpdateTask().execute(100 * result.x(), 100 * result.y(), 100 * result.z());
                                    }
                                });

                            }

                            public void onFinish() {
                                label.setText("Terminado!");
                                btnSaltar.setText("Saltar: OK!");
                                btnSaltar.setVisibility(View.VISIBLE);
                                guardarData("saltando", datas);
                                debug.setVisibility(View.INVISIBLE);
                            }
                        }.start();
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
                wwmeDevice.connect(TrainingMenu.this, wwmeDeviceListener);
            }
        });

        btnCorrer = (Button)findViewById(R.id.btnCorrer);
        btnCorrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnCorrer.setVisibility(View.INVISIBLE);
                debug.setVisibility(View.INVISIBLE);
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        datas = "axisX,axisY,axisZ,time\n";
                        new CountDownTimer(60000, 333) {
                            public void onTick(long millisUntilFinished) {
                                label.setText("Corra...!\nSegundos restantes: " + millisUntilFinished / 1000);
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x() * 100 + "," + result.y() * 100 + "," + result.z() * 100 + "," + Calendar.getInstance().getTime() + "\n";
                                        //new UpdateTask().execute(100 * result.x(), 100 * result.y(), 100 * result.z());
                                    }
                                });

                            }

                            public void onFinish() {
                                label.setText("Terminado!");
                                btnCorrer.setText("Correr: OK!");
                                btnCorrer.setVisibility(View.VISIBLE);
                                guardarData("corriendo", datas);
                            }
                        }.start();
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
                wwmeDevice.connect(TrainingMenu.this, wwmeDeviceListener);
            }
        });

        btnReposo = (Button)findViewById(R.id.btnReposo);
        btnReposo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnReposo.setVisibility(View.INVISIBLE);
                debug.setVisibility(View.INVISIBLE);
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        datas = "axisX,axisY,axisZ,time\n";
                        new CountDownTimer(60000, 333) {
                            public void onTick(long millisUntilFinished) {
                                label.setText("Mantengase en reposo...!\nSegundos restantes: " + millisUntilFinished / 1000);
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x() * 100 + "," + result.y() * 100 + "," + result.z() * 100 + "," + Calendar.getInstance().getTime() + "\n";
                                        //new UpdateTask().execute(100 * result.x(), 100 * result.y(), 100 * result.z());
                                    }
                                });

                            }

                            public void onFinish() {
                                label.setText("Terminado!");
                                btnReposo.setText("Reposo: OK!");
                                btnReposo.setVisibility(View.VISIBLE);
                                guardarData("reposo", datas);
                            }
                        }.start();
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
                wwmeDevice.connect(TrainingMenu.this, wwmeDeviceListener);
            }
        });
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
            Toast.makeText(getApplicationContext(), "Data Guardada", Toast.LENGTH_LONG).show();

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mac", dataApp.macDevice)
                    .addFormDataPart("file", actionName+".csv", RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server+"/upload_file/file")
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = okHttpClient.newCall(request).execute();
            Toast.makeText(getApplicationContext(),"Data: "+response.toString(),Toast.LENGTH_LONG).show();


        } catch (FileNotFoundException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
