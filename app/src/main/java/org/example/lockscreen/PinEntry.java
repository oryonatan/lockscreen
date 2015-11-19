package org.example.lockscreen;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.OutputStream;

public class PinEntry extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        makeFullScreen();
        setContentView(R.layout.activity_pin_entry);
        final EditText pinEntry = (EditText) findViewById(R.id.et_pe_pin);
        pinEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //nothing
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 4)
                    return;//do nothing
                try {


                    File pinFile = new File(getApplicationContext().getFilesDir(), "PIN");
                    if (getIntent().hasExtra("setPin")) {
                        BufferedWriter out = new BufferedWriter(new FileWriter(pinFile));
                        out.write(s.toString());
                        out.flush();
                        startActivity(new Intent(getApplicationContext(), MainMenu.class));
                    }


                    else if (getIntent().hasExtra("tryPin")) {
                        BufferedReader in = new BufferedReader(new FileReader(pinFile));
                        String pin_str= in.readLine();

                        if (pin_str.equals(s.toString())){
                            finish();
                        }
                        else {
                            pinEntry.setText("");
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        pinEntry.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, LockScreen.class));
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
}
