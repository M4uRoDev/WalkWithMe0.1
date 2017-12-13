package cl.walkwithme.m4uro.walkwithme01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.regex.Pattern;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterSupportNet extends AppCompatActivity {

    EditText nombreapellido,correo,contrasena,recontrasena;
    Button guardar;

    private boolean validarEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_support_net);

        nombreapellido = (EditText)findViewById(R.id.editText);
        correo = (EditText)findViewById(R.id.editText2);
        contrasena = (EditText)findViewById(R.id.editText3);
        recontrasena = (EditText)findViewById(R.id.editText4);

        guardar = (Button)findViewById(R.id.button4);

        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomape = nombreapellido.getText().toString();
                String usuario = correo.getText().toString();
                String pass = contrasena.getText().toString();
                String repass = recontrasena.getText().toString();

                if(nomape.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Debe ingresar nombre y apellido", Toast.LENGTH_SHORT).show();
                    nombreapellido.requestFocus();
                }else if(usuario.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Debe ingresar correo", Toast.LENGTH_SHORT).show();
                    correo.requestFocus();
                }else if(!validarEmail(usuario)){
                    Toast.makeText(getApplicationContext(), "Debe ingresar correo válido", Toast.LENGTH_SHORT).show();
                    correo.requestFocus();
                }else if(pass.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Debe ingresar contraseña", Toast.LENGTH_SHORT).show();
                    contrasena.requestFocus();
                }else if(repass.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Debe repetir su contraseña", Toast.LENGTH_SHORT).show();
                    recontrasena.requestFocus();
                }else if(!pass.equals(repass)){
                    Toast.makeText(getApplicationContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                    contrasena.requestFocus();
                }else{
                    if(guardarSupportNetAdmin(nomape,usuario,repass).equals("ok")){
                        Intent guardar = new Intent(RegisterSupportNet.this, Authentication.class);
                        Toast.makeText(getApplicationContext(), "¡Agregado exitosamente!", Toast.LENGTH_SHORT).show();
                        startActivity(guardar);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "¡Ups, ha ocurrio un problema!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

    }

    protected String guardarSupportNetAdmin(String nombreapellido, String mail,String contrasena){
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mac", dataApp.macDevice)
                    .addFormDataPart("nombreapellido", nombreapellido)
                    .addFormDataPart("mail", mail)
                    .addFormDataPart("password",contrasena)
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server+"login/nuevoRedApoyoAdmin")
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
