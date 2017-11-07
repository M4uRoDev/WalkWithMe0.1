package cl.walkwithme.m4uro.walkwithme01;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.punchthrough.bean.sdk.Bean;
import com.punchthrough.bean.sdk.BeanListener;
import com.punchthrough.bean.sdk.message.Acceleration;
import com.punchthrough.bean.sdk.message.BeanError;
import com.punchthrough.bean.sdk.message.ScratchBank;

import org.bson.Document;

import java.util.Calendar;

public class MainMenu extends AppCompatActivity {

    Button btnTraining;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        btnTraining = (Button)findViewById(R.id.btnTraining);
        btnTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent trainingMenu = new Intent(MainMenu.this, TrainingMenu.class);
                Bean conn = (Bean) getIntent().getExtras().get("connect");
                trainingMenu.putExtra("connect", conn);
                startActivity(trainingMenu);
            }
        });

    }


    //funcion para conectarse a mongodb e insertar data.
    private class UpdateTask extends AsyncTask<Double, Double,Double> {
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



}
