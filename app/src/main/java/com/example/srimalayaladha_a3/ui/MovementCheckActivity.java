package com.example.srimalayaladha_a3.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.FloatMath;
import android.widget.Button;
import android.widget.TextView;

import com.example.srimalayaladha_a3.R;

public class MovementCheckActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor mSensor;
    TextView valText; // Text which displays the status

    private double mAccel; // Finally calculated acceleration
    private double mAccelCurrent; // Current acceleration from the sensor
    private double mAccelLast; // Last recorded acceleration from the sensor

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movement_check);

        init(); // initialize all the arguments
    }

    /**
     * Setup the sensorManager, Sensor, DisplayText, and acceleration values
     */
    private void init() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
        valText = findViewById(R.id.txtMoveVal);
        mAccel = 0;
        mAccelCurrent = SensorManager.GRAVITY_EARTH;
        mAccelLast = SensorManager.GRAVITY_EARTH;
    }

    /**
     * EventChange listener
     * Gets the sensor values and sends them forward to process
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float vals[] = event.values;
        checkMovement(vals);
    }

    /**
     * Use the X, Y, and Z gravitational acceleration values to determine the acceleration of device
     * Account for error by using current and previous values
     * Use the conditions to determine if the device is stationary or moving
     */
    private void checkMovement(float[] vals) {
        float x = vals[0];
        float y = vals[1];
        float z = vals[2];

        // adapted from: https://stackoverflow.com/a/14574992
        mAccelLast = mAccelCurrent;
        mAccelCurrent = Math.sqrt(x*x + y*y + z*z);
        double delta = mAccelCurrent - mAccelLast;
        mAccel = mAccel * 0.9f + delta;

        boolean move = mAccel > 0.2 || mAccel < -0.2;

        if(!move) {
            valText.setText("Stationary");
        } else {
            valText.setText("Moving");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * upon Activity resume, register the listener
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Release the listener to free the sensor
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}