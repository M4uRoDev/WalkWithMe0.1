package cl.walkwithme.m4uro.walkwithme01;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.punchthrough.bean.sdk.Bean;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainMenu extends AppCompatActivity {

    Button btnTraining, btnClassifier, btnClassifierFeedback;
    ImageView imgwalk;
    int hideModeClassifier = 0;
    int count = 0;
    Handler mainThreadHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        final Bean conn = (Bean) getIntent().getExtras().get("connect");
        imgwalk = (ImageView)findViewById(R.id.imageView);
        imgwalk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count = count+1;
                if(count == 5 && hideModeClassifier == 0){
                    hideModeClassifier = 1;
                    count = 0;
                    Toast.makeText(getApplicationContext(),"Modo Clasificación Oculto: Activado",Toast.LENGTH_SHORT).show();
                    Intent hideAlert = new Intent(MainMenu.this, hideAlertDispatcher.class);
                    hideAlert.putExtra("connect", conn);
                    startService(hideAlert);
                }else if(count == 5 && hideModeClassifier ==1){
                    hideModeClassifier = 0;
                    count = 0;
                    Toast.makeText(getApplicationContext(),"Modo Clasificacion Oculto: Desactivado",Toast.LENGTH_SHORT).show();
                    Intent hideAlert = new Intent(MainMenu.this, hideAlertDispatcher.class);
                    stopService(hideAlert);
                }
            }
        });
        btnTraining = (Button)findViewById(R.id.btnTraining);
        btnTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trainingMenu = new Intent(MainMenu.this, TrainingMenu.class);
                trainingMenu.putExtra("connect", conn);
                startActivity(trainingMenu);
            }
        });

        btnClassifier = (Button)findViewById(R.id.btnClassification);
        btnClassifier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isTraining(dataApp.macDevice)){
                    Intent ToClassifier = new Intent(MainMenu.this, ClassifierClient.class);
                    ToClassifier.putExtra("connect", conn);
                    startActivity(ToClassifier);
                }
            }
        });

        btnClassifierFeedback = (Button)findViewById(R.id.button14);

        btnClassifierFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent feedBack = new Intent(MainMenu.this, FeedbackClassifier.class);
                feedBack.putExtra("connect", conn);
                startActivity(feedBack);            }
        });

    }

    protected boolean isTraining(String mac){
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("mac", mac)
                .addFormDataPart("action", "isTraining")
                .build();
        Request request = new Request.Builder()
                .url(dataApp.url_server+"csvFiles/ClassifierServer.php")
                .post(requestBody)
                .build();
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            Response response = okHttpClient.newCall(request).execute();
            String responseStr = response.body().string();
            //updateResult(responseStr);
            String status = null;
            String msg = null;
            try {
                status = new JSONObject(responseStr).getString("status");
                msg = new JSONObject(responseStr).getString("msg");

                if(status.equals("error")){
                    Toast.makeText(getApplicationContext(), msg+"\n¡Debe entrenar!", Toast.LENGTH_SHORT).show();
                    return false;
                }else{
                    return true;
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateResult(String myResponse) {
        Message msg = Message.obtain();
        msg.obj = myResponse;
        mainThreadHandler.sendMessage(msg);

        //Message msg = Message.obtain(mainThreadHandler);
        //msg.sendToTarget();
    }

    //funcion para conectarse a mongodb e insertar data.
    /*private class UpdateTask extends AsyncTask<Double, Double,Double> {
        protected Double doInBackground(Double... urls) {

            MongoClientURI uri  = new MongoClientURI("mongodb://wwme_user:mandrake@ds231715.mlab.com:31715/walkwithme_mdb");
            MongoClient client = new MongoClient(uri);
            MongoDatabase db = client.getDatabase(uri.getDatabase());
            MongoCollection<Document> coll = db.getCollection(getIntent().getExtras().getString("macConnect"));

            Document doc = new Document();
            doc.put("Time",Calendar.getInstance().getTime().toString());
            doc.put("Eje X",urls[0]);
            doc.put("Eje Y",urls[1]);
            doc.put("Eje Z",urls[2]);
            coll.insertOne(doc);
            client.close();
            return null;
        }

    }
    */


}
