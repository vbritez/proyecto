package com.tigo.cs.android.activity.shiftguard;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class ShiftguardActivity extends AbstractActivity {

    private OnClickListener guardMarkOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shiftguard_main);
        onSetContentViewFinish();

        Button markButton = (Button) findViewById(R.id.guardMarkButton);

        guardMarkOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(GuardMarkActivity.class);

            }
        };

        markButton.setOnClickListener(guardMarkOnClickListener);

    }

}