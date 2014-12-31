package com.tigo.cs.android.activity.order;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class OrderVersionActivity extends AbstractActivity {

    private OnClickListener orderSimpleOnClickListener;
    private OnClickListener orderIlimitOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_version_main);
        onSetContentViewFinish();

        Button orderSimpleButton = (Button) findViewById(R.id.orderButton);
        Button orderIlimitButton = (Button) findViewById(R.id.orderIlimitButton);

        orderSimpleOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(OrderActivity.class);

            }
        };
        orderIlimitOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(OrderIlimActivity.class);

            }
        };

        orderSimpleButton.setOnClickListener(orderSimpleOnClickListener);
        orderIlimitButton.setOnClickListener(orderIlimitOnClickListener);
    }

}