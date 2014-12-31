package com.tigo.cs.android.activity.tigomoney;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.EditText;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.GlobalParameterEntity;
import com.tigo.cs.android.helper.domain.SessionEntity;
import com.tigo.cs.api.entities.TigoMoneyService;

public class TigoMoneyConsultIdActivity extends AbstractActivity {

    private Button consultIdButton;
    private EditText idEdit;

    private EditText birthDate;

    private int birthDay;
    private int birthMonth;
    private int birthYear;
    private DatePicker dpResult;

    @Override
    public Integer getServicecod() {
        return -3;
    }

    @Override
    public String getServiceEventCod() {
        return "CONSULTID";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tigo_money_consult_id_main);
        onSetContentViewFinish();

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */

        GlobalParameterEntity globalParameterEntity = CsTigoApplication.getGlobalParameterHelper().getCiAlphaNumericEntity();
        idEdit = (EditText) findViewById(R.id.identificationEdit);

        if (globalParameterEntity.getParameterValue().equals("1")) {
            idEdit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }

        consultIdButton = (Button) findViewById(R.id.tigomoneyConsultIdButton);

        setCurrentDateOnView();

        consultIdButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new TigoMoneyService();

                if (!startUserMark()) {
                    return;
                }

                SessionEntity session = CsTigoApplication.getSessionHelper().findLast();

                GregorianCalendar cal = new GregorianCalendar();
                cal.set(Calendar.DAY_OF_MONTH, birthDay);
                cal.set(Calendar.MONTH, birthMonth);
                cal.set(Calendar.YEAR, birthYear);
                // cal.set(Calendar.DAY_OF_MONTH, 27);
                // cal.set(Calendar.MONTH, 11);
                // cal.set(Calendar.YEAR, 1990);

                TigoMoneyTmpEntity currentEntity = new TigoMoneyTmpEntity();
                currentEntity.setBirthDate(cal.getTime());
                CsTigoApplication.setCurrentTigoMoneyEntity(currentEntity);

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(), idEdit.getText().toString(),
                        String.valueOf(cal.getTime().getTime()),
                        session.getCellphoneNumber());

                ((TigoMoneyService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((TigoMoneyService) entity).setIdentification(idEdit.getText().toString());
                ((TigoMoneyService) entity).setBirthDate(cal.getTime().getTime());
                ((TigoMoneyService) entity).setSource(session.getCellphoneNumber());
                ((TigoMoneyService) entity).setRequiresLocation(false);

                endUserMark(msg);
            }
        });

    }

    public void setCurrentDateOnView() {

        // tvDisplayDate = (TextView) findViewById(R.id.viewDate);
        dpResult = (DatePicker) findViewById(R.id.picker);

        Calendar c = Calendar.getInstance();
        birthYear = c.get(Calendar.YEAR);
        birthMonth = c.get(Calendar.MONTH);
        birthDay = c.get(Calendar.DAY_OF_MONTH);

        MyOnDateChangeListener onDateChangeListener = new MyOnDateChangeListener();
        dpResult.init(birthYear, birthMonth, birthDay, onDateChangeListener);

    }

    public class MyOnDateChangeListener implements OnDateChangedListener {
        @Override
        public void onDateChanged(DatePicker birthDayDatePicker, int currentYear, int currentMonth, int currentDay) {
            birthYear = currentYear;
            birthMonth = currentMonth;
            birthDay = currentDay;
        }
    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(idEdit.getText())) {
            Toast.makeText(
                    TigoMoneyConsultIdActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.tigomoney_id_not_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        GlobalParameterEntity globalParameterEntity = CsTigoApplication.getGlobalParameterHelper().getCiAlphaNumericEntity();
        idEdit = (EditText) findViewById(R.id.identificationEdit);

        if (globalParameterEntity.getParameterValue().equals("1")) {
            idEdit.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        }
    };
}
