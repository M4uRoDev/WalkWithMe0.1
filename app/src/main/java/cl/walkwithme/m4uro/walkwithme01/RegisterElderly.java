package cl.walkwithme.m4uro.walkwithme01;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterElderly extends AppCompatActivity {
    EditText nombreapellido, rut, fechanacimiento;
    Button guardar, cancelar;
    Calendar myCalendar = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {



        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_elderly);

        nombreapellido = (EditText)findViewById(R.id.editText);
        rut = (EditText)findViewById(R.id.editText8);
        fechanacimiento = (EditText)findViewById(R.id.editText11);

        guardar = (Button)findViewById(R.id.button6);
        cancelar = (Button)findViewById(R.id.button7);

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }

        };


        fechanacimiento.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(RegisterElderly.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



        guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nomape = nombreapellido.getText().toString();
                String rutText = rut.getText().toString();
                String fnaci = fechanacimiento.getText().toString();
                if(nomape.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Debe ingresar nombre",Toast.LENGTH_SHORT).show();
                    nombreapellido.requestFocus();
                }else if(rutText.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Debe ingresar rut",Toast.LENGTH_SHORT).show();
                    rut.requestFocus();
                }else if(fnaci.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Debe ingresar fecha nacimiento", Toast.LENGTH_SHORT).show();
                    fechanacimiento.requestFocus();
                }else{
                    if(guardarElderly(nomape,rutText,fnaci).equals("ok")){
                        Intent SupportNet = new Intent(RegisterElderly.this, RegisterSupportNet.class);
                        Toast.makeText(getApplicationContext(), "¡Agregado exitosamente!", Toast.LENGTH_SHORT).show();
                        startActivity(SupportNet);
                        finish();
                    }else{
                        Toast.makeText(getApplicationContext(), "¡Ups, ha ocurrio un problema!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        fechanacimiento.setText(sdf.format(myCalendar.getTime()));
    }

    protected String guardarElderly(String nombreapellido, String rut,String fechanacimiento){
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mac", dataApp.macDevice)
                    .addFormDataPart("nombreapellido", nombreapellido)
                    .addFormDataPart("rut", rut)
                    .addFormDataPart("fechanacimiento",fechanacimiento)
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server+"login/nuevoAdultoMayor")
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
