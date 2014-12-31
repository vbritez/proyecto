package com.tigo.cs.android.activity.attendance;

import java.text.MessageFormat;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.api.entities.AttendanceService;

public class AttendanceObservationActivity extends AbstractActivity {

    private OnClickListener registerOnClickListener;
    private EditText obser;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_finish_observation_main);
        onSetContentViewFinish();
        Button registerButton = (Button) findViewById(R.id.registerAttendanceButton);
        obser = (EditText) findViewById(R.id.attendanceObservation);

        registerOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String codEmployee = getIntent().getExtras().getString(
                        "cod_employee");
                final String event = getIntent().getExtras().getString("event");

                entity = new AttendanceService();

                serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        getServicecod(), event);
                if (!startUserMark()) {
                    return;
                }

                serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        getServicecod(), event);

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(), codEmployee, event,
                        obser.getText().toString());
                ((AttendanceService) entity).setEvent(event);
                ((AttendanceService) entity).setEmployeeCode(codEmployee);
                ((AttendanceService) entity).setObservation(obser.getText().toString());

                endUserMark(msg);

            }
        };
        registerButton.setOnClickListener(registerOnClickListener);

    }

    @Override
    public Integer getServicecod() {
        return 11;
    }

    @Override
    public String getServiceEventCod() {
        return "";
    }

    @Override
    protected boolean validateUserInput() {
        return true;
    }

}