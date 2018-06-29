package com.mjun.slidingactivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by huijun on 2018/6/11.
 */

public class Activity3 extends BaseTransitionActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example4_1);
        ((TextView) findViewById(R.id.text)).setText(getString(R.string.example4_tip31));
        ((Button) findViewById(R.id.button)).setText(getString(R.string.example4_tip32));
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity3.this, Activity4.class);
                startActivity(intent);
            }
        });
    }
}
