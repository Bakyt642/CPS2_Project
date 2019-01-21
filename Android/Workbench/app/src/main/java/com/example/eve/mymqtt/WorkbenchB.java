package com.example.eve.mymqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class WorkbenchB extends AppCompatActivity {
    MqttHelperB mqttHelperB;
    ChartHelper mChart;
    LineChart chart;
    TextView dataReceived1b;
    TextView dataReceived2b;
    TextView dataReceived3b;
    TextView dataReceived4b;
    TextView rfi2b;
    TextView rfi1b;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workbench_b);


        dataReceived1b = (TextView) findViewById(R.id.tempReceivedb);
        dataReceived2b = (TextView) findViewById(R.id.humReceivedb);
        dataReceived3b = (TextView) findViewById(R.id.lumReceivedb);
        dataReceived4b = (TextView) findViewById(R.id.identityb);

        rfi1b = (TextView) findViewById(R.id.rf1);
        rfi2b = (TextView) findViewById(R.id.rf2);

        startMqtt();
    }

    private void startMqtt(){
        mqttHelperB = new MqttHelperB(getApplicationContext());
        mqttHelperB.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.w("Debug","Connected");
            }

            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                Log.w("Debug",mqttMessage.toString());





                JSONObject reader = new JSONObject(mqttMessage.toString());

//                int tempcurrent = reader.getInt("temp");
//                int humcurrent = reader.getInt("hum");
//                int lumcurrent = reader.getInt("lum");
//                String currentworker = reader.getString("ID");


//
//                dataReceived1b.setText(String.valueOf(tempcurrent));
//                dataReceived2b.setText(String.valueOf(humcurrent));
//                dataReceived3b.setText(String.valueOf(lumcurrent));
//                dataReceived4b.setText(String.valueOf(currentworker));





            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }


    public void onToggleClickedb(View view) {

        // включена ли кнопка
        boolean ON = ((ToggleButton) view).isChecked();
        final ImageView image= (ImageView)findViewById(R.id.turnoffimage);

        if (ON) {
            mqttHelperB.publishTopic("ON");

            // действия если включена
            Toast.makeText(this, "Light is on!", Toast.LENGTH_LONG).show();
            image.setImageResource(R.drawable.ic_bulb_on);
        } else {
            // действия, если выключена
            Toast.makeText(this, "Light is off!", Toast.LENGTH_LONG).show();
            mqttHelperB.publishTopic("OFF");
            image.setImageResource(R.drawable.ic_bulb_off);
        }
    }











}