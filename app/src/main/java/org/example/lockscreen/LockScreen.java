package org.example.lockscreen;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class LockScreen extends Activity implements SensorEventListener {
    private ArrayList<Pair<Long, double[]>> sensorLog;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long sensingStartTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        findViewById(R.id.btn_ls_touchToUnlcok).setOnTouchListener(otl_tryToUnlock);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //start gathring
                sensorLog = new ArrayList<>();
                sensingStartTime = 0;
                sensorManager.registerListener
                        (LockScreen.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
                break;
            case MotionEvent.ACTION_UP:
                //stop gathering
                Log.d("Debug::", "Up");
                sensorManager.unregisterListener(LockScreen.this);
                if (sensorLog.size() > 150) {
                    double[][] recordedGesture = GestureRecognizer.prepForComapre(sensorLog);
                    if (isCloseEnough(recordedGesture)){
                        //TODO: respond
                    }
                }
                break;
        }
        return true;
    }

    private boolean isCloseEnough(double[][] recordedGesture) {
        FileInputStream c1fis, c2fis, c3fis;
        ObjectInputStream c1ois, c2ois, c3ois;
        double[][] curve1, curve2, curve3;
        try {
            c1fis = new FileInputStream(new File(MainActivity.SHAPES_DIR + "c1"));

            c2fis = new FileInputStream(new File(MainActivity.SHAPES_DIR + "c2"));
            c3fis = new FileInputStream(new File(MainActivity.SHAPES_DIR + "c3"));
            c1ois = new ObjectInputStream(c1fis);
            c2ois = new ObjectInputStream(c2fis);
            c3ois = new ObjectInputStream(c3fis);
            curve1 = (double[][]) c1ois.readObject();
            curve2 = (double[][]) c2ois.readObject();
            curve3 = (double[][]) c3ois.readObject();

            double avgDist = (GestureRecognizer.compareCleanArrays(recordedGesture, curve1) +
                    GestureRecognizer.compareCleanArrays(recordedGesture, curve2) +
                    GestureRecognizer.compareCleanArrays(recordedGesture, curve3)) / 3;

            File dirFiles[] = new File(this.getFilesDir(), MainActivity.SHAPES_DIR).listFiles();
            double initialDist = 0;
            for (File aFile : dirFiles) {
                if (aFile.getName().startsWith("d")) {
                    String restOfFileName = aFile.getName().substring(1);
                    initialDist = Double.parseDouble(restOfFileName);
                    break;
                }
            }
            if (initialDist / avgDist > 1.6)
                return true;
            return false;
        } catch (Exception e){
                return true;
            }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
