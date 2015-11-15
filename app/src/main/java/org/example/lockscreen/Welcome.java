package org.example.lockscreen;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

public class Welcome extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        findViewById(R.id.btn_wcm_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),aFewTips.class));
            }
        });
    }

}
