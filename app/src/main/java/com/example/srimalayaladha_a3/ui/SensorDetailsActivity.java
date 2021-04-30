package com.example.srimalayaladha_a3.ui;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srimalayaladha_a3.R;

import java.util.ArrayList;
import java.util.List;

public class SensorDetailsActivity extends AppCompatActivity implements SensorEventListener {

    String sensorName;
    Sensor mSensorClicked;
    SensorManager mSensorManager;
    List<Sensor> mSensorList = new ArrayList<>();
    List<String> infoList = new ArrayList<>();
    List<String> valList = new ArrayList<>();

    MyRecyclerViewAdapter info_adapter;
    MyRecyclerViewAdapter vals_adapter;
    RecyclerView mInfoRecycler;
    RecyclerView mValsRecycler;
    LinearLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sensor_details);

        // receive intent and stringExtra (Sensor name)
        receiveIntent();

        mLayoutManager = new LinearLayoutManager(this);

        findSensor();           // find the sensor that was clicked to launch this activity
        setupInfoRecycler();    // Information view
        setupValsRecycler();    // Value view
    }

    /**
     * Receive intent and string extra from the calling activity
     */
    private void receiveIntent() {
        Intent i = getIntent();
        sensorName = i.getStringExtra("SENSOR_NAME");
    }

    /**
     * Check the name of the sensor sent by MainActivity against the list of all the available sensors
     * Quit the activity if the sensor is not found
     * Register the Sensor Event Listener
     */
    private void findSensor() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor: mSensorList) {
            if(sensor.getName().equals(sensorName)) {
                mSensorClicked = sensor;
            }
        }
        if (mSensorClicked == null) {
            Toast.makeText(this, "ERROR: SENSOR NOT FOUND!", Toast.LENGTH_SHORT).show();
            finish();
        }

        mSensorManager.registerListener(this, mSensorClicked, SensorManager.SENSOR_DELAY_UI);
    }

    /**
     * Populate the list of sensor details
     */
    private void getSensorDetails() {
        infoList.add("Name: " + mSensorClicked.getName());
        infoList.add("Type: " + mSensorClicked.getStringType());
        infoList.add("Max Range: " + mSensorClicked.getMaximumRange());
        infoList.add("Resolution: " + mSensorClicked.getResolution());
        infoList.add("Power: " + mSensorClicked.getPower());
        infoList.add("Version: " + mSensorClicked.getVersion());
        infoList.add("Vendor: " + mSensorClicked.getVendor());
    }

    /**
     * Set up the Information view recycler view
     */
    private void setupInfoRecycler() {
        getSensorDetails();
        info_adapter = new MyRecyclerViewAdapter(this, infoList);
        mInfoRecycler = findViewById(R.id.sensor_info_recycler);
        mInfoRecycler.setLayoutManager(mLayoutManager);
        mInfoRecycler.setAdapter(info_adapter);
    }

    /**
     * Set up the Value view
     * Notify if the data is changed (because of constantly changing values)
     */
    private void setupValsRecycler() {
        mValsRecycler = findViewById(R.id.sensor_vals_recycler);
        mValsRecycler.setLayoutManager(new LinearLayoutManager(this));
        vals_adapter = new MyRecyclerViewAdapter(this, valList);
        mValsRecycler.setAdapter(vals_adapter);
        vals_adapter.notifyDataSetChanged();
    }

    /**
     * EventChange listener
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] vals = event.values;
        valList = new ArrayList<>();
        for(int i = 0; i < vals.length; i++) {
            valList.add("" + vals[i]);
        }
        vals_adapter.updateData(valList); // update data in adapter
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // do nothing
    }

    /**
     * Custom RecyclerView adapter
     * adopted from https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
     * Populate the List mData of Strings
     */
    public class MyRecyclerViewAdapter extends RecyclerView.Adapter<MyRecyclerViewAdapter.ViewHolder> {

        private List<String> mData;
        private LayoutInflater mInflater;

        // data is passed into the constructor
        MyRecyclerViewAdapter(Context context, List<String> data) {
            this.mInflater = LayoutInflater.from(context);
            this.mData = data;
        }

        // inflates the row layout from xml when needed
        @Override
        public MyRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recyclerview_layout, parent, false);
            return new MyRecyclerViewAdapter.ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(MyRecyclerViewAdapter.ViewHolder holder, int position) {
            String sensor = mData.get(position);
            holder.mTextView.setText(sensor);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
        }

        // Update the data without re-initializing the adapter (to enable scrolling)
        public void updateData(List<String> viewModels) {
            mData.clear();
            mData.addAll(viewModels);
            notifyDataSetChanged();
        }

        // stores and recycles views as they are scrolled off screen
        public class ViewHolder extends RecyclerView.ViewHolder{
            TextView mTextView;

            ViewHolder(final View itemView) {
                super(itemView);
                mTextView = itemView.findViewById(R.id.txtRecycler);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Do nothing
                    }
                });
            }
        }
    }

    /**
     * upon Activity resume, register the listener
     */
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensorClicked, SensorManager.SENSOR_DELAY_UI);
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