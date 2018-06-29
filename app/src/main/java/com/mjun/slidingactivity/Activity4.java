package com.mjun.slidingactivity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by huijun on 2018/6/11.
 */

public class Activity4 extends BaseTransitionActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.example4_1);
        ((TextView) findViewById(R.id.text)).setText(getString(R.string.example4_tip41));
        ((Button) findViewById(R.id.button)).setVisibility(View.GONE);
    }
}
