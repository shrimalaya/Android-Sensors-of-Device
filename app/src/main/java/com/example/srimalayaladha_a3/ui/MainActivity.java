package com.example.srimalayaladha_a3.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.srimalayaladha_a3.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RecyclerView mRecycler;
    LinearLayoutManager mLayoutManager;
    MyRecyclerViewAdapter adapter;
    SensorManager mSensorManager;

    List<Sensor> mSensorList = new ArrayList<>();
    List<String> mList = new ArrayList<>();

    Button lightCheck;
    Button flatCheck;
    Button moveCheck;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateSensors();  // populates the list of sensors
        setupRecyclerView(); // sets up the recycler view using custom adapter
        setupButtons(); // set up the 3 buttons
    }

    /**
     * Populate the List of Strings which will store the names of all individual sensors
     */
    private void populateSensors() {
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        mSensorList = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor: mSensorList) {
            mList.add(sensor.getName());
        }
    }


    private void setupRecyclerView() {
        adapter = new MyRecyclerViewAdapter(this, mList);
        mRecycler = findViewById(R.id.main_recycler);
        mLayoutManager = new LinearLayoutManager(this);
        mRecycler.setLayoutManager(mLayoutManager);
        mRecycler.setAdapter(adapter);
    }

    /**
     * Launch respective activities upon Button click
     */
    private void setupButtons() {
        lightCheck = findViewById(R.id.btnLightCheck);
        lightCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LightSensorActivity.class);
                startActivity(intent);
            }
        });

        flatCheck = findViewById(R.id.btnFlatCheck);
        flatCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FlatCheckActivity.class);
                startActivity(intent);
            }
        });

        moveCheck = findViewById(R.id.btnMoveCheck);
        moveCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MovementCheckActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Custom RecyclerView adapter
     * adopted from https://stackoverflow.com/questions/40584424/simple-android-recyclerview-example
     * Populate the List mData of Strings
     * Display a toast message and launch the SensorDetailsActivity when a view is clicked
     * When using intents, put the name of sensor as extra to identify the sensor in the receiving activity
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
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mInflater.inflate(R.layout.recyclerview_layout, parent, false);
            return new ViewHolder(view);
        }

        // binds the data to the TextView in each row
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            String sensor = mData.get(position);
            holder.mTextView.setText(sensor);
        }

        // total number of rows
        @Override
        public int getItemCount() {
            return mData.size();
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
                        Toast.makeText(MainActivity.this, "You clicked " + mData.get(getAdapterPosition()), Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getBaseContext(), SensorDetailsActivity.class);
                        i.putExtra("SENSOR_NAME", mData.get(getAdapterPosition()));
                        startActivity(i);
                    }
                });
            }
        }
    }
}