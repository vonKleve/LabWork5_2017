package com.example.dell.myapplication;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.util.Vector;

public class MainActivity extends AppCompatActivity implements SensorEventListener
{

    TextView x_coordinate,y_coordinate,z_coordinate;
    private SensorManager mSensorManager;
    private Sensor AccelerometerSensor,GravitySensor;
    private float []gravity = new float[3];
    private Vector<Point>linear_acceleration = new Vector<>();
    //private long last_update = 0;

    GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        AccelerometerSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        GravitySensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        mSensorManager.registerListener(this, AccelerometerSensor,mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,GravitySensor,mSensorManager.SENSOR_DELAY_NORMAL);


        // gravity init
        gravity[0] = gravity[1] = gravity[2] = 0;

        // find appropriate output
        x_coordinate = (TextView)findViewById(R.id.x_coordinate);
        y_coordinate = (TextView)findViewById(R.id.y_coordinate);
        z_coordinate = (TextView)findViewById(R.id.z_coordinate);
    }

    @Override
    protected void onPause()
    {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        mSensorManager.registerListener(this, AccelerometerSensor,mSensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this,GravitySensor,mSensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor mySensor = event.sensor;


        if(mySensor.getType() == Sensor.TYPE_ACCELEROMETER)
        {
            // check whether more than 10mls have passed since
            // the last time onSensorChanged was invoked
            //long curTime = System.currentTimeMillis();

            if( linear_acceleration.size() < 100 )
            {
            //    last_update = curTime;

                // Isolate the force of gravity with the low-pass filter.
                final float alpha = (float)0.8;
                gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0];
                gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1];
                gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2];

                // Remove the gravity contribution with the high-pass filter.
                Point point = new Point();
                point.x = event.values[0] - gravity[0];
                point.y = event.values[1] - gravity[1];
                point.z = event.values[2] - gravity[2];
                linear_acceleration.add(point);
            }
        }
        else if(mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION)
        {
            Point point = new Point();
            point.x = event.values[0];
            point.y = event.values[1];
            point.z = event.values[2];
            linear_acceleration.add(point);
        }
        else if(mySensor.getType() == Sensor.TYPE_GRAVITY)
        {
            gravity[0] = event.values[0];
            gravity[1] = event.values[1];
            gravity[2] = event.values[2];
        }

        x_coordinate.setText(String.valueOf(linear_acceleration.lastElement().x));
        y_coordinate.setText(String.valueOf(linear_acceleration.lastElement().y));
        z_coordinate.setText(String.valueOf(linear_acceleration.lastElement().z));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onDrawClick(View view)
    {
        onPause();
        ShowGraphic();
    }

    public void ShowGraphic()
    {
        View v = findViewById(R.id.draw_button);
        v.setVisibility(View.GONE);
        float []xAr = new float[linear_acceleration.size()];
        float []yAr = new float[linear_acceleration.size()];
        float []zAr = new float[linear_acceleration.size()];
        for(int i=0;i<linear_acceleration.size();i++)
        {
            xAr[i] = linear_acceleration.get(i).x/100;
            yAr[i] = linear_acceleration.get(i).y/100;
            zAr[i] = linear_acceleration.get(i).z/100;
        }

        mGLView = new mGLSurfaceView(this,xAr,yAr,zAr);
        setContentView(mGLView);
    }

}