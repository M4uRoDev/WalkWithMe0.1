package cl.walkwithme.m4uro.walkwithme01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AlertResponse extends AppCompatActivity {

    TextView nombre, alerta;
    Button socorrer, saltar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alert_response);
        nombre = (TextView)findViewById(R.id.textView13);
        alerta = (TextView)findViewById(R.id.textView14);
        socorrer = (Button)findViewById(R.id.button12);
        saltar = (Button)findViewById(R.id.button13);

        nombre.setText(dataApp.nombreAdulto);
        alerta.setText(dataApp.tAlerta);

        socorrer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(socorrer_web(dataApp.idAlerta).equals("ok")){
                    Toast.makeText(getApplicationContext(), "Alerta aceptada",Toast.LENGTH_SHORT).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Hubo un error",Toast.LENGTH_SHORT).show();
                }
            }
        });

        saltar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(escalar_web(dataApp.idAlerta).equals("ok")){
                    Toast.makeText(getApplicationContext(), "Alerta escalada, se notificara al proximo red de apoyo",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(), "Solo tiene una red de apoyo",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    protected String socorrer_web(String idalerta){
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("idalerta", idalerta)
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server+"alert/respuesta_alerta")
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

    protected String escalar_web(String idalerta){
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("idalerta", idalerta)
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server+"alert/escala_alerta")
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
