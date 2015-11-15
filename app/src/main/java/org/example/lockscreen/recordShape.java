package org.example.lockscreen;


import android.content.Intent;
import android.graphics.drawable.TransitionDrawable;
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
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.io.FileUtils;


public class recordShape extends Activity implements SensorEventListener {
    private ArrayList<Pair<Long, double[]>> sensorLog;
    private long sensingStartTime = 0;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private int curveCount = 0;
    private  TransitionDrawable trans ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        setContentView(R.layout.activity_record_shape);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        findViewById(R.id.btn_rs_recordRoundButton).setOnTouchListener(otl_holdToAddShape);


    }

    public static void SaveRecording(ArrayList<Pair<Long, double[]>> sensorLog, Activity activity,int curveCount) {
        File shapeDir = new File(activity.getFilesDir(), MainActivity.SHAPES_DIR);
        shapeDir.mkdirs();
        try {
            FileUtils.cleanDirectory(shapeDir);
        }catch (Exception e){};

        String fileName = "c" + Integer.toString(curveCount);
        File curve = new File(shapeDir, fileName);
        FileOutputStream fos ;
        try {
            fos = new FileOutputStream(curve);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            double[][] sensorData = GestureRecognizer.prepForComapre(sensorLog);
            os.writeObject(sensorData);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(activity, "Save file failed", Toast.LENGTH_SHORT).show();
        }

    }

    private View.OnTouchListener otl_holdToAddShape = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    trans =
                            (TransitionDrawable) findViewById(R.id.btn_rs_recordRoundButton).getBackground();
                    trans.startTransition(2000);
                    //start gathring
                    sensorLog = new ArrayList<>();
                    sensingStartTime = 0;
                    sensorManager.registerListener
                            (recordShape.this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST);
                    break;
                case MotionEvent.ACTION_UP:
                    //stop gathering
                    Log.d("Debug::", "Up");
                    sensorManager.unregisterListener(recordShape.this);
                    trans.resetTransition();
                    if (sensorLog.size() > 150) {
                        Toast.makeText(recordShape.this, "OK", Toast.LENGTH_SHORT).show();
                        SaveRecording(sensorLog, recordShape.this, curveCount);
                        curveCount++;
                        ((Button)findViewById(R.id.btn_rs_recordRoundButton)).setText(Integer.toString(3-curveCount));
                        if (curveCount== 3){
                            try {
                                double dist = getInitialDistance();
                                new File(MainActivity.SHAPES_DIR + "d" + Double.toString(dist)).createNewFile();
                            }catch (Exception e){}
                            startActivity(new Intent(getApplicationContext(),MainMenu.class));
                        }
                    }
                    else {
                        Toast.makeText(recordShape.this, "Try holding the button longer", Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
            return true;
        }
    };

    private double getInitialDistance() throws IOException, ClassNotFoundException {
        FileInputStream c1fis,c2fis ;
        ObjectInputStream c1ois, c2ois;
        double[][] curve1 , curve2;
        c1fis = new FileInputStream(new File(MainActivity.SHAPES_DIR+"c1"));
        c2fis = new FileInputStream(new File(MainActivity.SHAPES_DIR+"c2"));
        c1ois = new ObjectInputStream(c1fis);
        c2ois = new ObjectInputStream(c2fis);
        curve1 = (double[][])c1ois.readObject();
        curve2 = (double[][])c2ois.readObject();
        double[][] curve3 = GestureRecognizer.prepForComapre(sensorLog);
        return (GestureRecognizer.compareCleanArrays(curve1,curve2) +
        GestureRecognizer.compareCleanArrays(curve1,curve3) +
        GestureRecognizer.compareCleanArrays(curve2,curve3)) /3;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == accelerometer) {
            if (sensingStartTime == 0) sensingStartTime = event.timestamp;
            System.out.println(String.valueOf(event.timestamp));
            System.out.println("Sensor data : " + Arrays.toString(event.values));
            sensorLog.add(new Pair<>(event.timestamp,
                    new double[]{event.values[0], event.values[1], event.values[2]}));
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
//Do nothing
    }
}
