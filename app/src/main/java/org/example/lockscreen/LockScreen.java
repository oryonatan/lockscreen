package org.example.lockscreen;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class LockScreen extends Activity implements SensorEventListener{

    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        findViewById(R.id.btn_ls_touchToUnlcok).setOnTouchListener(otl_tryToUnlock);
    }

    private View.OnTouchListener otl_testShape = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    //start gathring
                    sensorLog = new ArrayList<>();
                    sensingStartTime = 0;
                    sensorManager.registerListener
                            (TestShape.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
                    break;
                case MotionEvent.ACTION_UP:
                    //stop gathering
                    Log.d("Debug::", "Up");
                    sensorManager.unregisterListener(TestShape.this);
                    if (sensorLog.size() > 150 ) {
                        String bestMatch = matchSensorLog(sensorLog);
                        Toast.makeText(TestShape.this, "Best match is: " + bestMatch, Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return true;
        }
    };


    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
