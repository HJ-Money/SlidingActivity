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

public class Activity2 extends BaseTransitionActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example4_1);
        ((TextView) findViewById(R.id.text)).setText(getString(R.string.example4_tip21));
        ((Button) findViewById(R.id.button)).setText(getString(R.string.example4_tip22));
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Activity2.this, Activity3.class);
                startActivity(intent);
            }
        });
    }
}
