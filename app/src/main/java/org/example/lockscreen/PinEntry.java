package org.example.lockscreen;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;

public class PinEntry extends Activity {
    private boolean settingNewPIN = true;



    @NonNull
    public View.OnClickListener ocl_pinOnClick() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText pinEntry = (EditText) findViewById(R.id.et_pe_pin);
                pinEntry.setText(pinEntry.getText() + ((Button) v).getText().toString());
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        
        makeFullScreen();
        setContentView(R.layout.activity_pin_entry);

        View[] pinButtons = {
                findViewById(R.id.btn_pe_bt1),
                findViewById(R.id.btn_pe_bt2),
                findViewById(R.id.btn_pe_bt3),
                findViewById(R.id.btn_pe_bt4),
                findViewById(R.id.btn_pe_bt5),
                findViewById(R.id.btn_pe_bt6),
                findViewById(R.id.btn_pe_bt7),
                findViewById(R.id.btn_pe_bt8),
                findViewById(R.id.btn_pe_bt9),
                findViewById(R.id.btn_pe_bt0)};
        for (View btn : pinButtons) {
            ((Button) btn).setOnClickListener(ocl_pinOnClick());
        }
        findViewById(R.id.btn_pe_backToGesture).setVisibility(View.GONE);

        final EditText pinEntry = (EditText) findViewById(R.id.et_pe_pin);
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

                                                    BufferedWriter out = new BufferedWriter(new FileWriter(pinFile));
                                                    out.write(s.toString());
                                                    out.flush();
                                                    startActivity(new Intent(getApplicationContext(), MainMenu.class));
                                                } catch (
                                                        Exception e
                                                        )

                                                {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

        );

    }





    public void makeFullScreen() {
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        if (Build.VERSION.SDK_INT < 19) { //View.SYSTEM_UI_FLAG_IMMERSIVE is only on API 19+
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        } else {
            this.getWindow().getDecorView()
                    .setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE);
        }
    }


}