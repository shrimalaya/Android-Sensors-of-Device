package com.example.srimalayaladha_a3.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srimalayaladha_a3.R;

import java.util.Timer;
import java.util.TimerTask;

public class FlatCheckActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor mSensor;
    MediaPlayer mPlayer;
    TextView displayFlat;
    Timer timer;    // to set timer of 5 seconds
    boolean alreadyFlat = false; // to check change in state from flat to not-flat


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_flat_check);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

        displayFlat = findViewById(R.id.txtFlatVal);
    }

    /**
     * Play the ringtone (beep)
     * Play only for 5 seconds or until the stopPlaying() func is called, whichever is earlier
     */
    private void playRingtone() {
        mPlayer = MediaPlayer.create(this, R.raw.beep);
        mPlayer.setLooping(false);

        try {
            mPlayer.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(this, "device flat - beep", Toast.LENGTH_LONG).show();
        mPlayer.start();

        /*
        From previous assignment:
        https://github.com/shrimalaya/Zombie-Mine-Seeker
         */
        timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                stopPlaying();
            }
        }, 5000);
    }

    /**
     * Function to stop the media player
     * Release and reset the media player
     */
    private void stopPlaying() {
        if(mPlayer != null) {
            mPlayer.reset();
            mPlayer.stop();
            mPlayer.release();
            mPlayer = null;
        }
    }

    /**
     * If values change, check for the condition again
     * Send the values to checkFlat() to determine further action
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float x = event.values[0];
        float y = event.values[1];
        float z = event.values[2];

        checkFlat(x, y, z);
    }

    /**
     * Depending on the y, & z values, determine if the device is flat
     * Flat can be either screen up or screen down (y-values would reflect up/down state)
     * Depending on state, display the appropriate message
     */
    private void checkFlat(float x, float y, float z) {
        // check screen up or screen down (flat)
        boolean yFlat = (y < 3 && y > -3) || ((y > 177 && y < 180) || (y > -180 && y < -177));
        boolean zFlat = z < 3 && z > -3;
        boolean flat = yFlat && zFlat;

        if(flat && alreadyFlat == false) {
            displayFlat.setText("Flat on table");
            alreadyFlat = true; // to check if the condition has changed
            playRingtone();
        } else if(!flat) {
            // stop playing the sound (this is a personal choice since this condition is not specified)
            stopPlaying();
            alreadyFlat = false;
            displayFlat.setText("Not Flat");
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
        sensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    /**
     * Release the listener to free the sensor
     * stop the media player
     */
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        stopPlaying();
    }
}