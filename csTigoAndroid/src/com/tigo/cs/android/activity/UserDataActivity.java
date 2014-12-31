package com.tigo.cs.android.activity;

import com.tigo.cs.android.R;
import com.tigo.cs.android.R.id;
import com.tigo.cs.android.R.layout;
import com.tigo.cs.android.activity.meta.MetadataActivity;
import com.tigo.cs.android.activity.serviceoperation.ServiceOperationActivity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class UserDataActivity extends AbstractActivity {

    private OnClickListener metadataOperationOnClickListener;
    private OnClickListener serviceOperationOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_data_main);
        onSetContentViewFinish();
        Button metadataQueryButton = (Button) findViewById(R.id.metadataQueryButton);
        Button metadataCreateButton = (Button) findViewById(R.id.metadataCreatebutton);

        metadataOperationOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(MetadataActivity.class);

            }
        };

        serviceOperationOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(ServiceOperationActivity.class);

            }
        };

        metadataQueryButton.setOnClickListener(metadataOperationOnClickListener);
        metadataCreateButton.setOnClickListener(serviceOperationOnClickListener);

    }

}