package com.tigo.cs.android.activity.attendance;

import java.util.HashMap;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class AttendanceActivity extends AbstractActivity {

    private OnClickListener continueOnClickListener;
    private EditText codEmployee;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_main);
        onSetContentViewFinish();
        Button continueButton = (Button) findViewById(R.id.continueButton);
        codEmployee = (EditText) findViewById(R.id.attendanceEmployeeCode);

        continueOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                final HashMap<String, String> extras = new HashMap<String, String>();
                extras.put("cod_employee", codEmployee.getText().toString());
                if (validateUserInput())
                    startEventActivity(AttendanceMenuActivity.class, extras);

            }
        };

        continueButton.setOnClickListener(continueOnClickListener);
    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(codEmployee.getText())) {
            Toast.makeText(
                    AttendanceActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.attendance_code_employee_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}