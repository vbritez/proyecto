package com.tigo.cs.android.activity.order;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class OrderIlimActivity extends AbstractActivity {

    private OnClickListener startOnClickListener;
    private OnClickListener registerOnClickListener;
    private OnClickListener finishOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_ilim_main);
        onSetContentViewFinish();

        Button startButton = (Button) findViewById(R.id.startButton);
        Button registerButton = (Button) findViewById(R.id.registerButton);
        Button finishButton = (Button) findViewById(R.id.finishButton);

        startOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(OrderIlimStartActivity.class);

            }
        };
        registerOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(OrderIlimRegisterActivity.class);

            }
        };
        finishOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(OrderIlimFinishActivity.class);

            }
        };

        startButton.setOnClickListener(startOnClickListener);
        registerButton.setOnClickListener(registerOnClickListener);
        finishButton.setOnClickListener(finishOnClickListener);
    }

}