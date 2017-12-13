package cl.walkwithme.m4uro.walkwithme01;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanDiscoveryListener;
import com.punchthrough.bean.sdk.BeanManager;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by m4uro on 25-10-17.
 */

public class findDevices extends ListActivity implements Serializable  {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        final ArrayList<Bean> beans = new ArrayList<>();

        final ArrayAdapter<Bean> Dispositivos = new ArrayAdapter(this, android.R.layout.simple_list_item_1);

        BeanDiscoveryListener listener = new BeanDiscoveryListener() {
            @Override
            public void onBeanDiscovered(Bean bean, int rssi) {
                String addr = bean.getDevice().getAddress();
                for(Bean b: beans){
                    if(Dispositivos.getCount() > 0){

                        Bean ultimoRegistro = Dispositivos.getItem(Dispositivos.getCount()-1);
                        if(ultimoRegistro != b){
                            if(b.getDevice().getAddress().equals(addr)){
                                Dispositivos.add(b);
                            }
                        }
                    }else{
                        Dispositivos.add(b);
                    }
                }
                beans.add(bean);
                setListAdapter(Dispositivos);
            }
            @Override
            public void onDiscoveryComplete() {

            }
        };
        BeanManager.getInstance().setScanTimeout(5);  // Timeout in seconds, optional, default is 30 seconds
        BeanManager.getInstance().startDiscovery(listener);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        final Intent returnLectura = new Intent(this, MainMenu.class);
        Bean wwmesDevice = (Bean) l.getAdapter().getItem(position);
        returnLectura.putExtra("macConnect", wwmesDevice.getDevice().getAddress());
        returnLectura.putExtra("connect", wwmesDevice);
        dataApp.macDevice = wwmesDevice.getDevice().getAddress().replace(":","");
        if(connectToLogin(wwmesDevice.getDevice().getAddress().replace(":","")).equals("nuevo")){
            Intent regElderly = new Intent(findDevices.this, RegisterElderly.class);
            Toast.makeText(getApplicationContext(), "Nuevo dispositivo, registre adulto mayor", Toast.LENGTH_LONG).show();
            startActivity(regElderly);
        }else{
            startActivity(returnLectura);
        }
    }

    protected String connectToLogin(String mac){
        try {
            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("mac", mac)
                    .build();
            Request request = new Request.Builder()
                    .url(dataApp.url_server+"login")
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
