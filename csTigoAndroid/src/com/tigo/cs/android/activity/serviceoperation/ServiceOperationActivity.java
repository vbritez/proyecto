package com.tigo.cs.android.activity.serviceoperation;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class ServiceOperationActivity extends AbstractActivity {

    private OnClickListener serviceDeleteOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_operation_main);
        onSetContentViewFinish();
        Button serviceDeleteButton = (Button) findViewById(R.id.serviceDeleteButton);

        serviceDeleteOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(ServiceOperationDeleteRetrieveActivity.class);

            }
        };

        serviceDeleteButton.setOnClickListener(serviceDeleteOnClickListener);

    }
}
