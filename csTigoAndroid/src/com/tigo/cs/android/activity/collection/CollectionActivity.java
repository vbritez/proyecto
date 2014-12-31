package com.tigo.cs.android.activity.collection;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class CollectionActivity extends AbstractActivity {

    private OnClickListener startOnClickListener;
    private OnClickListener registerInvoiceOnClickListener;
    private OnClickListener registerPaymentCashOnClickListener;
    private OnClickListener registerPaymentCheckOnClickListener;
    private OnClickListener finishOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_main);
        onSetContentViewFinish();

        Button startButton = (Button) findViewById(R.id.startButton);
        Button registerInvoiceButton = (Button) findViewById(R.id.registerInvoiceButton);
        Button registerPaymentCashButton = (Button) findViewById(R.id.registerPaymentCashButton);
        Button registerPaymentCheckButton = (Button) findViewById(R.id.registerPaymentCheckButton);
        Button finishButton = (Button) findViewById(R.id.finishButton);

        startOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(CollectionStartActivity.class);

            }
        };

        registerInvoiceOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(CollectionRegisterInvoiceActivity.class);

            }
        };

        registerPaymentCashOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(CollectionRegisterPaymentCashActivity.class);

            }
        };

        registerPaymentCheckOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(CollectionRegisterPaymentCheckActivity.class);

            }
        };

        finishOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(CollectionEndActivity.class);

            }
        };

        startButton.setOnClickListener(startOnClickListener);
        registerInvoiceButton.setOnClickListener(registerInvoiceOnClickListener);
        registerPaymentCashButton.setOnClickListener(registerPaymentCashOnClickListener);
        registerPaymentCheckButton.setOnClickListener(registerPaymentCheckOnClickListener);
        finishButton.setOnClickListener(finishOnClickListener);
    }

}