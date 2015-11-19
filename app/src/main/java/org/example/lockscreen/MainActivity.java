package org.example.lockscreen;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

public class MainActivity extends Activity {
    public static final String SHAPES_DIR = "shapes/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set up our Lockscreen
        startService(new Intent(this, LockScreenService.class));

        //Check if being called from launcher or from lock screen
        if (this.getIntent().hasExtra("fromlockscreen"))
        {
            makeFullScreen();

            final Intent startLockScreen = new Intent(getApplicationContext(), LockScreen.class);
            startLockScreen.putExtra("fromlockscreen",true);
            startActivity(startLockScreen);
        }
        else{
            if(0 != (getIntent().getFlags() & (Intent.FLAG_FROM_BACKGROUND |
                    Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY) ))
            {
                finish();
            }
            //TODO: check if we should load welcome screen
            startActivity(new Intent(getApplicationContext(),Welcome.class));

        }

    }

    /**
     * A simple method that sets the screen to fullscreen.  It removes the Notifications bar,
     *   the Actionbar and the virtual keys (if they are on the phone)
     */

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
    public void onBackPressed() {

    }

    public void unlockScreen(View view) {
        //Instead of using finish(), this totally destroys the process
        android.os.Process.killProcess(android.os.Process.myPid());
    }

//    public void onStop(){
//        if (!this.getIntent().hasExtra("fromlockscreen")){
//            finish();
//        }
//    }
}