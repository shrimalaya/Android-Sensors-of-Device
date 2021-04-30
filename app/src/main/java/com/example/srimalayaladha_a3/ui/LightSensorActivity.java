package com.example.srimalayaladha_a3.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.media.ToneGenerator;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srimalayaladha_a3.R;

import java.io.IOException;

public class LightSensorActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor mSensor;
    TextView valText;
    MediaPlayer mPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light_sensor);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_UI);
        valText = findViewById(R.id.txtLightVal);
    }

    /**
     * Depending on the val, play the media player
     * Play the beep in loop
     */
    private void playRingtone(float val) {
        stopPlaying(); // to stop any existing media from playing
        mPlayer = MediaPlayer.create(this, R.raw.beep);
        mPlayer.setLooping(true);

        if(val == 0) {
            try {
                mPlayer.prepare();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mPlayer.start();
        } else {
                stopPlaying();
            }

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
     * Update the display with the current light sensor value
     * Send the sensor value to playRingtone() (to play ringtone)
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float val = event.values[0];
        valText.setText("" + val);
        playRingtone(val);
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
     * stop the media player
     */
    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
        stopPlaying();
    }
}