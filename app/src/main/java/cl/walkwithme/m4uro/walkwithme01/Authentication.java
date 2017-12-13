package cl.walkwithme.m4uro.walkwithme01;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Authentication extends AppCompatActivity {
    Button connect, ingresar;
    EditText mail, pass;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    private static final int REQUEST_ENABLE_BT = 1;


    public Boolean isOnlineNet() {

        try {
            Process p = java.lang.Runtime.getRuntime().exec("ping -c 1 67.205.177.134");

            int val           = p.waitFor();
            boolean reachable = (val == 0);
            return reachable;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        if(!isOnlineNet()){
            //termina aplicación, si estas en SplashActivity.
            Toast.makeText(getApplicationContext(),"¡La aplicación requiere conexión a internet!", Toast.LENGTH_LONG).show();
            finish();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        if (ContextCompat.checkSelfPermission(Authentication.this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(Authentication.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {

                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(Authentication.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

                // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                // app-defined int constant. The callback method gets the
                // result of the request.
            }
        }

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Bluetooth no soportado, la aplicación se cerrara",Toast.LENGTH_LONG).show();
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        mail = (EditText)findViewById(R.id.editText6);
        pass = (EditText)findViewById(R.id.editText7);

        connect = (Button)findViewById(R.id.button);

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent abrirListaBean = new Intent(Authentication.this, findDevices.class);
                startActivity(abrirListaBean);
            }
        });

        ingresar = (Button)findViewById(R.id.button2);

        ingresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mailConn = mail.getText().toString();
                String passConn = pass.getText().toString();

                if(mailConn.isEmpty() || passConn.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Debe completar los campos",Toast.LENGTH_LONG).show();
                }else if(!validarEmail(mailConn)){
                    Toast.makeText(getApplicationContext(), "Debe ingresar un mail válido",Toast.LENGTH_LONG).show();
                }else{
                    if(connectToLogin(mailConn,passConn).equals("error")){
                        Toast.makeText(getApplicationContext(), "Credenciales incorrectas", Toast.LENGTH_LONG).show();
                    }else{
                        String[] parts = connectToLogin(mailConn,passConn).split(":");
                        String idredapoyo = parts[0];
                        String nombreapellido = parts[1];
                        dataApp.idredapoyo = idredapoyo;
                        dataApp.nombreApellidoRedApoyo = nombreapellido;
                        Intent supportNetMenu = new Intent(Authentication.this, SupportNetMenu.class);
                        startActivity(supportNetMenu);
                    }
                }

            }
        });
    }

    protected String connectToLogin(String usuario, String clave){
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("usuario", usuario)
                    .addFormDataPart("password", clave)
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server+"login/redApoyo")
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
