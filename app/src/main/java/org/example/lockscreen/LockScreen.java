package org.example.lockscreen;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.graphics.drawable.TransitionDrawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class LockScreen extends Activity implements SensorEventListener {
    public boolean inPINscreen = false;
    private ArrayList<Pair<Long, double[]>> sensorLog;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private long sensingStartTime = 0;
    private boolean holdScreen = true;
    public LayoutInflater mInflater;
    public WindowManager mWindow;
    public View mLockScreenView;
    public View mPinEntryView;
    WindowManager.LayoutParams mParams;
    private TransitionDrawable buttonTrans;
    private TransitionDrawable backgroudTrans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File fromPINflag = new File(getFilesDir(), "pinstarted");
        boolean PINexists = new File(getApplicationContext().getFilesDir(), "PIN").exists();
        File firstCurve = new File(this.getFilesDir(), MainActivity.SHAPES_DIR + "c0");
        if (!PINexists || !firstCurve.exists() ||
                (!getIntent().hasExtra("fromlockscreen")
//                        && !fromPINflag.exists()
                )) {
            MainActivity.unlockScreen();
        }
        if (fromPINflag.exists()) {
            fromPINflag.delete();
        }
        Log.i("oncreate", "created");
        holdScreen = true;

        mParams = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD,
                PixelFormat.TRANSLUCENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mParams.flags = WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS;
        } else {
            mParams.flags = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
        }


        mInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        mWindow = (WindowManager) getSystemService(WINDOW_SERVICE);
        mLockScreenView = mInflater.inflate(R.layout.activity_lock_screen, null);
        mPinEntryView = mInflater.inflate(R.layout.activity_pin_entry, null);

        mWindow.addView(mLockScreenView, mParams);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        mLockScreenView.findViewById(R.id.btn_ls_touchToUnlcok).setOnTouchListener(otl_tryToUnlock);
        Button goToPin = (Button) mLockScreenView.findViewById(R.id.btn_ls_setPin);
        goToPin.setOnClickListener(ocl_startPIN());

    }

    @NonNull
    public View.OnClickListener ocl_pinOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinEntry = (EditText) mPinEntryView.findViewById(R.id.et_pe_pin);
                pinEntry.setText(pinEntry.getText() + ((Button) v).getText().toString());
            }
        };
    }

    @NonNull
    public View.OnClickListener ocl_startPIN() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inPINscreen = true;
                try {
                    new File(getApplicationContext().getFilesDir(), "pinstarted").createNewFile();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                mWindow.removeView(mLockScreenView);
                mWindow.addView(mPinEntryView, mParams);
                ((Button) mPinEntryView.findViewById(R.id.btn_pe_backToGesture)).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mWindow.removeView(mPinEntryView);
                        mWindow.addView(mLockScreenView, mParams);
                        inPINscreen = false;
                    }
                });
                final EditText pinEntry = (EditText) mPinEntryView.findViewById(R.id.et_pe_pin);
                View[] pinButtons = {
                        mPinEntryView.findViewById(R.id.btn_pe_bt1),
                        mPinEntryView.findViewById(R.id.btn_pe_bt2),
                        mPinEntryView.findViewById(R.id.btn_pe_bt3),
                        mPinEntryView.findViewById(R.id.btn_pe_bt4),
                        mPinEntryView.findViewById(R.id.btn_pe_bt5),
                        mPinEntryView.findViewById(R.id.btn_pe_bt6),
                        mPinEntryView.findViewById(R.id.btn_pe_bt7),
                        mPinEntryView.findViewById(R.id.btn_pe_bt8),
                        mPinEntryView.findViewById(R.id.btn_pe_bt9),
                        mPinEntryView.findViewById(R.id.btn_pe_bt0)};
                for (View btn : pinButtons) {
                    ((Button) btn).setOnClickListener(ocl_pinOnClick());
                }

                pinEntry.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if (s.length() < 4)
                            return;//do nothing
                        try {
                            File pinFile = new File(getApplicationContext().getFilesDir(), "PIN");
                            BufferedReader in = new BufferedReader(new FileReader(pinFile));
                            String pin_str = in.readLine();

                            if (pin_str.equals(s.toString())) {
                                mWindow.removeView(mPinEntryView);
                                finish();
                            } else {
                                ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(new long[]{0, 150, 25, 150}, -1);
                                pinEntry.setText("");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        };
    }


    private View.OnTouchListener otl_tryToUnlock = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            buttonTrans =
                    (TransitionDrawable) mLockScreenView.findViewById(R.id.btn_ls_touchToUnlcok).getBackground();
            backgroudTrans =
                    (TransitionDrawable) mLockScreenView.findViewById(R.id.lockBackground).getBackground();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    buttonTrans.startTransition(3000);
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
                            mWindow.removeView(mLockScreenView);
                            finish();
                        } else {
                            buttonTrans.resetTransition();
                            backgroudTrans.startTransition(330);
                            backgroudTrans.reverseTransition(330);
                            ((Vibrator) getSystemService(VIBRATOR_SERVICE)).vibrate(new long[]{0, 150, 25, 150}, -1);
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
            System.out.println("Ratio" + Double.toString(avgDist / initialDist));

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_HOME) {
            Log.i("Home Button", "Clicked");
        }
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inPINscreen) {
                mWindow.removeView(mPinEntryView);
                mWindow.addView(mLockScreenView, mParams);
                inPINscreen = false;
            }
        }
        return false;
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
