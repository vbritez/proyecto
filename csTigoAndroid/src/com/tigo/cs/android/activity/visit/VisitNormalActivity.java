package com.tigo.cs.android.activity.visit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class VisitNormalActivity extends AbstractActivity {

    private OnClickListener startVisitOnClickListener;
    private OnClickListener endVisitOnClickListener;
    private OnClickListener quickVisitOnClickListener;
    private String clientCod;
    private Bundle extras;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_normal_main);
        onSetContentViewFinish();

        extras = getIntent().getExtras();
        if (extras != null) {
            clientCod = (String) extras.get("client_cod");
        }

        Button startVisitButton = (Button) findViewById(R.id.startButton);
        Button endVisitButton = (Button) findViewById(R.id.endButton);
        Button quickVisitButton = (Button) findViewById(R.id.quickVisitButton);

        startVisitOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extras != null && clientCod != null
                    && !clientCod.equals("")) {
                    Intent visitViewerIntent = new Intent(VisitNormalActivity.this, VisitStartVisitActivity.class);
                    visitViewerIntent.putExtra("client_cod", clientCod);
                    startActivity(visitViewerIntent);
                } else {
                    startEventActivity(VisitStartVisitActivity.class);
                }

            }
        };
        endVisitOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(VisitEndVisitActivity.class);

            }
        };
        quickVisitOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (extras != null && clientCod != null
                    && !clientCod.equals("")) {
                    Intent visitViewerIntent = new Intent(VisitNormalActivity.this, VisitQuickVisitActivity.class);
                    visitViewerIntent.putExtra("client_cod", clientCod);
                    startActivity(visitViewerIntent);
                } else {
                    startEventActivity(VisitQuickVisitActivity.class);
                }

            }
        };

        startVisitButton.setOnClickListener(startVisitOnClickListener);
        endVisitButton.setOnClickListener(endVisitOnClickListener);
        quickVisitButton.setOnClickListener(quickVisitOnClickListener);

    }

}