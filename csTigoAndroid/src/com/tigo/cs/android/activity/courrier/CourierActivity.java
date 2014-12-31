package com.tigo.cs.android.activity.courrier;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class CourierActivity extends AbstractActivity {

    private OnClickListener deliveredOnClickListener;
    private OnClickListener notDeliveredOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courier_main);
        onSetContentViewFinish();

        Button deliveredButton = (Button) findViewById(R.id.deliveredButton);
        Button notDeliveredButton = (Button) findViewById(R.id.notDeliveredButton);

        deliveredOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(CourierDeliveredActivity.class);

            }
        };
        notDeliveredOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(CourierNotDeliveredActivity.class);

            }
        };

        deliveredButton.setOnClickListener(deliveredOnClickListener);
        notDeliveredButton.setOnClickListener(notDeliveredOnClickListener);
    }

}