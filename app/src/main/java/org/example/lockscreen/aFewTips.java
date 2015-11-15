package org.example.lockscreen;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;

public class aFewTips extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_a_few_tips);
        findViewById(R.id.btn_aft_okayImReady).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),recordShape.class));
            }
        });
    }

}
