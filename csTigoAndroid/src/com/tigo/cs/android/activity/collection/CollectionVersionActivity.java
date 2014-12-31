package com.tigo.cs.android.activity.collection;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class CollectionVersionActivity extends AbstractActivity {

    private OnClickListener collectionOnClickListener;
    private OnClickListener averageCollectionInvoiceOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_version_main);
        onSetContentViewFinish();

        Button collectionButton = (Button) findViewById(R.id.collectionButton);
        Button averageCollectionButton = (Button) findViewById(R.id.averageCollectionButton);

        collectionOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(CollectionActivity.class);

            }
        };

        averageCollectionInvoiceOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(AverageCollectionActivity.class);
            }
        };

        collectionButton.setOnClickListener(collectionOnClickListener);
        averageCollectionButton.setOnClickListener(averageCollectionInvoiceOnClickListener);
    }

}