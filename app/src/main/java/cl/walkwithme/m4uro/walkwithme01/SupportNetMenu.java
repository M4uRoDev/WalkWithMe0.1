package cl.walkwithme.m4uro.walkwithme01;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SupportNetMenu extends AppCompatActivity {

    TextView loginData;
    Button AlertMode, nuevoRedApoyo;
    int modoAlerta = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suppor_net_menu);

        loginData = (TextView)findViewById(R.id.textView10);
        AlertMode = (Button)findViewById(R.id.button8);

        AlertMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modoAlerta == 0) {
                    modoAlerta = 1;
                    final Intent GoToAlertReceiver = new Intent(SupportNetMenu.this, AlertReceiver.class);
                    startService(GoToAlertReceiver);
                    AlertMode.setText("Desactivar Modo Alerta");
                }else if(modoAlerta == 1){
                    stopService(new Intent(SupportNetMenu.this, AlertReceiver.class));
                    modoAlerta = 0;
                    AlertMode.setText("Modo Alerta");
                }
            }
        });

        nuevoRedApoyo = (Button)findViewById(R.id.button9);

        nuevoRedApoyo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nuevaRed = new Intent(SupportNetMenu.this, RegisterNewSupportNet.class);
                startActivity(nuevaRed);
            }
        });

        loginData.setText("Bienvenido: "+dataApp.nombreApellidoRedApoyo);

    }
}
