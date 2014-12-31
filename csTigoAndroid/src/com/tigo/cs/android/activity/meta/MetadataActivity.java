package com.tigo.cs.android.activity.meta;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class MetadataActivity extends AbstractActivity {

    private OnClickListener metadataReadOnClickListener;
    private OnClickListener metadataCreateOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metadata_operation_main);
        onSetContentViewFinish();
        Button metadataReadButton = (Button) findViewById(R.id.metadataQueryButton);
        Button metadataCreateButton = (Button) findViewById(R.id.metadataCreatebutton);

        metadataReadOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(MetadataReadActivity.class);

            }
        };

        metadataCreateOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(MetadataCreateActivity.class);

            }
        };

        metadataReadButton.setOnClickListener(metadataReadOnClickListener);
        metadataCreateButton.setOnClickListener(metadataCreateOnClickListener);

    }

}