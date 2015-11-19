package org.example.lockscreen;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import static android.view.WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;


public class LockScreen extends Activity implements SensorEventListener {
    private ArrayList<Pair<Long, double[]>> sensorLog;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long sensingStartTime = 0;
    private HomeKeyLocker mHomeKeyLocker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lock_screen);
        this.getWindow().setType(WindowManager.LayoutParams.TYPE_KEYGUARD_DIALOG);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        findViewById(R.id.btn_ls_touchToUnlcok).setOnTouchListener(otl_tryToUnlock);
        Button goToPin = (Button)findViewById(R.id.btn_ls_setPin);
        goToPin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(getApplicationContext(),PinEntry.class).putExtra("tryPin",true)
                );
            }
        });
    }


    private View.OnTouchListener otl_tryToUnlock = new View.OnTouchListener() {
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
                        if (isCloseEnough(recordedGesture)) {
                            //TODO: respond
                            finish();
                        }
                    }
                    break;
            }
            return true;
        }
    };

    private boolean isCloseEnough(double[][] recordedGesture) {
        FileInputStream c1fis, c2fis, c3fis;
        ObjectInputStream c1ois, c2ois, c3ois;
        double[][] curve1, curve2, curve3;
        try {
            c1fis = new FileInputStream(new File(this.getFilesDir(), MainActivity.SHAPES_DIR + "c0"));
            c2fis = new FileInputStream(new File(this.getFilesDir(), MainActivity.SHAPES_DIR + "c1"));
            c3fis = new FileInputStream(new File(this.getFilesDir(), MainActivity.SHAPES_DIR + "c2"));

            c1ois = new ObjectInputStream(c1fis);
            c2ois = new ObjectInputStream(c2fis);
            c3ois = new ObjectInputStream(c3fis);

            curve1 = (double[][]) c1ois.readObject();
            curve2 = (double[][]) c2ois.readObject();
            curve3 = (double[][]) c3ois.readObject();

            double avgDist =
                    (GestureRecognizer.compareCleanArrays(recordedGesture, curve1) +
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
            System.out.println("Original distance " + Double.toString(initialDist));
            System.out.println("Found distance " + Double.toString(avgDist));
            System.out.println("Ratio" + Double.toString( avgDist/initialDist));

            if (avgDist / initialDist < 1.6)
                return true;
            return false;
        } catch (Exception e) {
            e.printStackTrace();

            return true;
        }
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
    }

    @Override
    public void onBackPressed() {
        return;
    }



    public void makeFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if(Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHomeKeyLocker =null;
    }
}
