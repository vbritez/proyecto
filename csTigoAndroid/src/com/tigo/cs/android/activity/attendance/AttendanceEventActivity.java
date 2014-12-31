package com.tigo.cs.android.activity.attendance;

import java.text.MessageFormat;
import java.util.HashMap;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.api.entities.AttendanceService;

public class AttendanceEventActivity extends AbstractActivity {

    private OnClickListener initOnClickListener;
    private OnClickListener finishOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.attendance_event_main);
        onSetContentViewFinish();
        Button initButton = (Button) findViewById(R.id.attendanceInitButton);
        Button finishButton = (Button) findViewById(R.id.attendanceFinishButton);

        initOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                entity = new AttendanceService();

                final String codEmployee = getIntent().getExtras().getString(
                        "cod_employee");
                final String event = getIntent().getExtras().getString("event");
                String evento = event + "I";

                serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        getServicecod(), evento);

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(), codEmployee, evento);

                ((AttendanceService) entity).setEvent(evento);
                ((AttendanceService) entity).setEmployeeCode(codEmployee);

                endUserMark(msg);

            }
        };

        finishOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                final String codEmployee = getIntent().getExtras().getString(
                        "cod_employee");
                final String event = getIntent().getExtras().getString("event");

                final HashMap<String, String> extras = new HashMap<String, String>();
                extras.put("cod_employee", codEmployee);

                entity = new AttendanceService();

                if (event.equals("P")) {
                    extras.put("event", event.concat("F"));
                    startEventActivity(AttendanceObservationActivity.class,
                            extras);
                } else {
                    if (!startUserMark()) {
                        return;
                    }
                    String evento = event + "F";

                    serviceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                            getServicecod(), evento);

                    if (!startUserMark()) {
                        return;
                    }

                    String msg = MessageFormat.format(
                            serviceEvent.getMessagePattern(),
                            service.getServicecod(), codEmployee, evento);

                    entity = new AttendanceService();
                    ((AttendanceService) entity).setEvent(evento);
                    ((AttendanceService) entity).setEmployeeCode(codEmployee);

                    endUserMark(msg);
                }

            }
        };

        initButton.setOnClickListener(initOnClickListener);
        finishButton.setOnClickListener(finishOnClickListener);

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