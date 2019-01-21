package com.example.eve.mymqtt;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

public class Workbench extends AppCompatActivity {
    MqttHelper mqttHelper;
    ChartHelper mChart;
    LineChart chart;
    TextView dataReceived1;
    TextView dataReceived2;
    TextView dataReceived3;
    TextView dataReceived4;
    TextView rfi2;
    TextView rfi1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workbench);


        dataReceived1 = (TextView) findViewById(R.id.tempReceived);
        dataReceived2 = (TextView) findViewById(R.id.humReceived);
        dataReceived3 = (TextView) findViewById(R.id.lumReceived);
        dataReceived4 = (TextView) findViewById(R.id.identity);

        rfi1 = (TextView) findViewById(R.id.rf1);
        rfi2 = (TextView) findViewById(R.id.rf2);

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





                JSONObject reader = new JSONObject(mqttMessage.toString());

                int tempcurrent = reader.getInt("temp");
                int humcurrent = reader.getInt("hum");
                int lumcurrent = reader.getInt("lum");
                String currentworker = reader.getString("ID");



                dataReceived1.setText(String.valueOf(tempcurrent));
                dataReceived2.setText(String.valueOf(humcurrent));
                dataReceived3.setText(String.valueOf(lumcurrent));
                dataReceived4.setText(String.valueOf(currentworker));





            }


            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }


    public void onToggleClicked(View view) {

        // включена ли кнопка
        boolean ON = ((ToggleButton) view).isChecked();
        final ImageView image= (ImageView)findViewById(R.id.turnoffimage);

        if (ON) {
            mqttHelper.publishTopic("ON");

            // действия если включена
            Toast.makeText(this, "Light is on!", Toast.LENGTH_LONG).show();
            image.setImageResource(R.drawable.ic_bulb_on);
        } else {
            // действия, если выключена
            Toast.makeText(this, "Light is off!", Toast.LENGTH_LONG).show();
            mqttHelper.publishTopic("OFF");
            image.setImageResource(R.drawable.ic_bulb_off);
        }
    }











}
