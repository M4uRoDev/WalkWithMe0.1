package cl.walkwithme.m4uro.walkwithme01;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.PowerManager;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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
import java.text.SimpleDateFormat;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class TrainingMenu extends AppCompatActivity {

    Button btnCaminar, btnSaltar, btnCorrer, btnReposo, btnSentarParar, btnAcostarParar, btnCancelar;
    String datas = "";
    TextView label;

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_menu);
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        final PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK, "DoNotDimScreen");
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        btnCancelar = (Button) findViewById(R.id.btnCancelar);
        btnCancelar.setVisibility(View.INVISIBLE);
        label = (TextView) findViewById(R.id.textView4);
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
        btnCaminar = (Button) findViewById(R.id.btnCaminar);
        btnCaminar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas = "";
                btnCancelar.setVisibility(View.VISIBLE);
                btnCaminar.setVisibility(View.INVISIBLE);
                wl.acquire(65000);
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        final CountDownTimer timer = new CountDownTimer(60000, 200) {
                            public void onTick(long millisUntilFinished) {
                                label.setText("¡Camine!\nDurante: " + millisUntilFinished / 1000 + " segundos.");
                                btnCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancel();
                                        btnCancelar.setVisibility(View.INVISIBLE);
                                    }
                                });
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x() + "," + result.y() + "," + result.z() + "," + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()) + "\n";
                                    }
                                });

                            }

                            public void onFinish() {
                                label.setText("Entrene Acción");
                                btnCaminar.setText("Caminar: OK");
                                btnCaminar.setVisibility(View.VISIBLE);
                                btnCancelar.setVisibility(View.INVISIBLE);
                                guardarData("caminando", datas);
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
                wwmeDevice.connect(TrainingMenu.this, wwmeDeviceListener);
            }
        });

        btnSaltar = (Button) findViewById(R.id.btnSaltar);
        btnSaltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas = "";
                wl.acquire(65000);
                btnCancelar.setVisibility(View.VISIBLE);
                btnSaltar.setVisibility(View.INVISIBLE);
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        final CountDownTimer timer = new CountDownTimer(60000, 200) {
                            public void onTick(long millisUntilFinished) {
                                btnCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancel();
                                        btnCancelar.setVisibility(View.INVISIBLE);
                                    }
                                });
                                if ((millisUntilFinished / 1000) % 5 == 0) {
                                    label.setText("¡Salte!\nDurante: " + millisUntilFinished / 1000 + " segundos.");
                                } else {
                                    label.setText("\nDurante: " + millisUntilFinished / 1000 + " segundos.");
                                }
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x() + "," + result.y() + "," + result.z() + "," + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()) + "\n";
                                        //new UpdateTask().execute(100 * result.x(), 100 * result.y(), 100 * result.z());
                                    }
                                });

                            }

                            public void onFinish() {
                                label.setText("Entrene Acción");
                                btnSaltar.setText("Saltar: OK");
                                btnSaltar.setVisibility(View.VISIBLE);
                                guardarData("saltando", datas);
                                btnCancelar.setVisibility(View.INVISIBLE);
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
                wwmeDevice.connect(TrainingMenu.this, wwmeDeviceListener);
            }
        });

        btnCorrer = (Button) findViewById(R.id.btnCorrer);
        btnCorrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas = "";
                wl.acquire(65000);
                btnCorrer.setVisibility(View.INVISIBLE);
                btnCancelar.setVisibility(View.VISIBLE);
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        final CountDownTimer timer = new CountDownTimer(60000, 200) {
                            public void onTick(long millisUntilFinished) {
                                btnCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancel();
                                        btnCancelar.setVisibility(View.INVISIBLE);
                                    }
                                });
                                label.setText("¡Corra!\nDurante: " + millisUntilFinished / 1000 + " segundos.");
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x() + "," + result.y() + "," + result.z() + "," + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()) + "\n";
                                        //new UpdateTask().execute(100 * result.x(), 100 * result.y(), 100 * result.z());
                                    }
                                });

                            }

                            public void onFinish() {
                                label.setText("Entrene Acción");
                                btnCorrer.setText("Correr: OK");
                                btnCorrer.setVisibility(View.VISIBLE);
                                guardarData("corriendo", datas);
                                btnCancelar.setVisibility(View.INVISIBLE);
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
                wwmeDevice.connect(TrainingMenu.this, wwmeDeviceListener);
            }
        });

        btnReposo = (Button) findViewById(R.id.btnReposo);
        btnReposo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas = "";
                wl.acquire(65000);
                btnCancelar.setVisibility(View.VISIBLE);
                btnReposo.setVisibility(View.INVISIBLE);
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        final CountDownTimer timer = new CountDownTimer(60000, 200) {
                            public void onTick(long millisUntilFinished) {
                                btnCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancel();
                                        btnCancelar.setVisibility(View.INVISIBLE);
                                    }
                                });
                                label.setText("¡Mantengase en reposo!\nDurante: " + millisUntilFinished / 1000 + " segundos.");
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x() + "," + result.y() + "," + result.z() + "," + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()) + "\n";
                                        //new UpdateTask().execute(100 * result.x(), 100 * result.y(), 100 * result.z());
                                    }
                                });

                            }

                            public void onFinish() {
                                label.setText("Entrene Acción");
                                btnReposo.setText("Reposo: OK");
                                btnReposo.setVisibility(View.VISIBLE);
                                guardarData("reposo", datas);
                                btnCancelar.setVisibility(View.INVISIBLE);
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
                wwmeDevice.connect(TrainingMenu.this, wwmeDeviceListener);
            }
        });

        btnSentarParar = (Button) findViewById(R.id.btnSentarParar);
        btnSentarParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas = "";
                wl.acquire(65000);
                btnCancelar.setVisibility(View.VISIBLE);
                btnSentarParar.setVisibility(View.INVISIBLE);
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        final CountDownTimer timer = new CountDownTimer(60000, 200) {
                            public void onTick(long millisUntilFinished) {
                                btnCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancel();
                                        btnCancelar.setVisibility(View.INVISIBLE);
                                    }
                                });
                                if ((millisUntilFinished / 1000) % 5 == 0) {
                                    label.setText("Sientese -> Parese \nDurante: " + millisUntilFinished / 1000 + " segundos.");
                                } else {
                                    label.setText("¡Haga la acción!\nDurante: " + millisUntilFinished / 1000 + " segundos.");
                                }
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x() + "," + result.y() + "," + result.z() + "," + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()) + "\n";
                                        //new UpdateTask().execute(100 * result.x(), 100 * result.y(), 100 * result.z());
                                    }
                                });

                            }

                            public void onFinish() {
                                label.setText("Entrene Acción");
                                btnSentarParar.setText("Sentarse - Pararse: OK");
                                btnSentarParar.setVisibility(View.VISIBLE);
                                guardarData("sentarse-pararse", datas);
                                btnCancelar.setVisibility(View.INVISIBLE);
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
                wwmeDevice.connect(TrainingMenu.this, wwmeDeviceListener);
            }
        });

        btnAcostarParar = (Button) findViewById(R.id.btnAcostarParar);
        btnAcostarParar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datas = "";
                wl.acquire(65000);
                btnCancelar.setVisibility(View.VISIBLE);
                btnAcostarParar.setVisibility(View.INVISIBLE);
                final BeanListener wwmeDeviceListener = new BeanListener() {
                    @Override
                    public void onConnected() {
                        final String[] accion = {"¡Acuestese!"};
                        final CountDownTimer timer = new CountDownTimer(60000, 200) {
                            public void onTick(long millisUntilFinished) {
                                btnCancelar.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        cancel();
                                        btnCancelar.setVisibility(View.INVISIBLE);
                                    }
                                });
                                if ((millisUntilFinished / 1000) % 5 == 0) {
                                    label.setText("Acuestese -> Parese \nDurante: " + millisUntilFinished / 1000 + " segundos.");
                                } else {
                                    label.setText("¡Haga la acción!\nDurante: " + millisUntilFinished / 1000 + " segundos.");
                                }
                                wwmeDevice.setLed(dataApp.blue);
                                wwmeDevice.setLed(dataApp.off);
                                wwmeDevice.readAcceleration(new com.punchthrough.bean.sdk.message.Callback<Acceleration>() {
                                    @Override
                                    public void onResult(Acceleration result) {
                                        datas = datas + result.x() + "," + result.y() + "," + result.z() + "," + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date()) + "\n";
                                        //new UpdateTask().execute(100 * result.x(), 100 * result.y(), 100 * result.z());
                                    }
                                });

                            }

                            public void onFinish() {
                                label.setText("¡Terminado!");
                                btnAcostarParar.setText("Acostarse - Pararse: OK");
                                btnAcostarParar.setVisibility(View.VISIBLE);
                                guardarData("acostarse-pararse", datas);
                                btnCancelar.setVisibility(View.INVISIBLE);
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
                wwmeDevice.connect(TrainingMenu.this, wwmeDeviceListener);
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
            Toast.makeText(getApplicationContext(), "¡LISTO!", Toast.LENGTH_SHORT).show();
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mac", dataApp.macDevice)
                    .addFormDataPart("file", actionName + ".csv", RequestBody.create(MediaType.parse("application/octet-stream"), file))
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server + "upload_file/file")
                    .post(requestBody)
                    .build();
            OkHttpClient okHttpClient = new OkHttpClient();
            Response response = okHttpClient.newCall(request).execute();
            String responseStr = response.body().string();
            Toast.makeText(getApplication(), responseStr, Toast.LENGTH_LONG).show();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}