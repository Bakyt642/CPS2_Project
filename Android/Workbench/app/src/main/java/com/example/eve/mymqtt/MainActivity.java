package com.example.eve.mymqtt;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class MainActivity extends AppCompatActivity {
    TextView view;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView visulize = (TextView) findViewById(R.id.progress1);
        TextView workbench = (TextView) findViewById(R.id.workbench);
        TextView workbenchb = (TextView) findViewById(R.id.workbench2);


// Set a click listener on that View
        visulize.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.


            @Override
            public void onClick(View view) {
                Intent vizIntent = new Intent(MainActivity.this, Visual.class);
                startActivity(vizIntent);
                Toast.makeText(getApplicationContext(), "Retriving data from Arduino", Toast.LENGTH_LONG).show();

            }
        });
        workbench.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.


            @Override
            public void onClick(View view) {
                Intent vizIntent = new Intent(MainActivity.this, Workbench.class);
                startActivity(vizIntent);
                Toast.makeText(getApplicationContext(), "Retriving data from Arduino", Toast.LENGTH_LONG).show();

            }
        });

        workbenchb.setOnClickListener(new View.OnClickListener() {
            // The code in this method will be executed when the numbers View is clicked on.


            @Override
            public void onClick(View view) {
                Intent vizIntent = new Intent(MainActivity.this, WorkbenchB.class);
                startActivity(vizIntent);
                Toast.makeText(getApplicationContext(), "Retriving data from Arduino", Toast.LENGTH_LONG).show();

            }
        });
    }
}