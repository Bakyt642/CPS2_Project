package com.example.eve.mymqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

public class Visual extends AppCompatActivity {
    MqttHelper mqttHelper;
    ChartHelper mChart;
    ChartHelperB mChart2;
    LineChart chart;
    LineChart chart2;
    TextView dataReceived1;
    TextView dataReceived2;
    TextView rfi2;
    TextView rfi1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visual);
        dataReceived1 = (TextView) findViewById(R.id.dataReceived1);
        dataReceived2 = (TextView) findViewById(R.id.dataReceived2);
        rfi1 = (TextView) findViewById(R.id.rf1);
        rfi2 = (TextView) findViewById(R.id.rf2);
        chart = (LineChart) findViewById(R.id.chart);
        chart2 = (LineChart) findViewById(R.id.chart2);
        mChart = new ChartHelper(chart);
        mChart2 = new ChartHelperB(chart2);

        startMqtt();
    }

    private void startMqtt(){
        mqttHelper = new MqttHelper(getApplicationContext());
        mqttHelper.mqttAndroidClient.setCallback(new MqttCallbackExtended() {
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

//                dataReceived.setText(mqttMessage.toString());




                      JSONObject reader = new JSONObject(mqttMessage.toString());

                      int products1 = reader.getInt("Products1");
                      int products2 = reader.getInt("Products2");
//                      int rfid1=reader.getInt("RFID");
//                      String rfid2=reader.getString("nb");

                      dataReceived1.setText(String.valueOf(products1));
                      dataReceived2.setText(String.valueOf(products2));
//                      rfi1.setText(String.valueOf(rfid1));
//                      rfi2.setText(String.valueOf(rfid2));


                      mChart.addEntry(Float.valueOf(products1));

                      mChart2.addEntry(Float.valueOf(products2));




            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }




    }

