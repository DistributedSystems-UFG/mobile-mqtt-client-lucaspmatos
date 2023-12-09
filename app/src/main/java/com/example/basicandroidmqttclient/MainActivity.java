package com.example.basicandroidmqttclient;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hivemq.client.mqtt.datatypes.MqttQos;
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient;
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_MESSAGE = "com.example.basicandroidmqttclient.MESSAGE";
    public static final String brokerURI = "54.147.89.192";

    Activity thisActivity;
    TextView tempText;
    TextView lightText;
    TextView dateText;
    TextView batteryText;

    EditText value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        thisActivity = this;
        tempText = (TextView) findViewById(R.id.tempText);
        lightText = (TextView) findViewById(R.id.lightText);
        dateText = (TextView) findViewById(R.id.dateText);
        batteryText = (TextView) findViewById(R.id.batteryText);
        value = (EditText) findViewById(R.id.editTextValue);
    }

    /** Called when the user taps the Send button */
    public void publishMessage(View view) {
        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();
        client.publishWith().topic("minimum").qos(MqttQos.AT_LEAST_ONCE).payload(value.getText().toString().getBytes()).send();
        client.disconnect();
    }

    public void sendSubscription(View view) {
        EditText topicName = (EditText) findViewById(R.id.editTextTopicNameSub);

        Mqtt5BlockingClient client = Mqtt5Client.builder()
                .identifier(UUID.randomUUID().toString())
                .serverHost(brokerURI)
                .buildBlocking();

        client.connect();

        // Use a callback to show the message on the screen
        client.toAsync().subscribeWith()
                .topicFilter("temp")
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    thisActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            tempText.setText(new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8));
                        }
                    });
                })
                .send();

        client.toAsync().subscribeWith()
                .topicFilter("light")
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    thisActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            lightText.setText(new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8));
                        }
                    });
                })
                .send();

        client.toAsync().subscribeWith()
                .topicFilter("date")
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    thisActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            dateText.setText(new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8));
                        }
                    });
                })
                .send();

        client.toAsync().subscribeWith()
                .topicFilter("battery")
                .qos(MqttQos.AT_LEAST_ONCE)
                .callback(msg -> {
                    thisActivity.runOnUiThread(new Runnable() {
                        public void run() {
                            batteryText.setText(new String(msg.getPayloadAsBytes(), StandardCharsets.UTF_8));
                        }
                    });
                })
                .send();
    }
}