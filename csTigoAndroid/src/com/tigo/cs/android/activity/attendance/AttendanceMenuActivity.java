package com.tigo.cs.android.activity.attendance;

import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class AttendanceMenuActivity extends AbstractActivity {

    private OnClickListener presenceOnClickListener;
    private OnClickListener breakOnClickListener;
    private OnClickListener lunchOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_menu_main);
        onSetContentViewFinish();
        Button presenceButton = (Button) findViewById(R.id.presenceButton);
        Button breakButton = (Button) findViewById(R.id.breakButton);
        Button lunchButton = (Button) findViewById(R.id.lunchButton);

        presenceOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String, String> extras = new HashMap<String, String>();
                extras.put("cod_employee",
                        getIntent().getExtras().getString("cod_employee"));

                extras.put("event", "P");
                startEventActivity(AttendanceEventActivity.class, extras);

            }
        };

        breakOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String, String> extras = new HashMap<String, String>();
                extras.put("cod_employee",
                        getIntent().getExtras().getString("cod_employee"));
                extras.put("event", "B");
                startEventActivity(AttendanceEventActivity.class, extras);

            }
        };

        lunchOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String, String> extras = new HashMap<String, String>();
                extras.put("cod_employee",
                        getIntent().getExtras().getString("cod_employee"));
                extras.put("event", "L");
                startEventActivity(AttendanceEventActivity.class, extras);

            }
        };

        presenceButton.setOnClickListener(presenceOnClickListener);
        breakButton.setOnClickListener(breakOnClickListener);
        lunchButton.setOnClickListener(lunchOnClickListener);

    }

}