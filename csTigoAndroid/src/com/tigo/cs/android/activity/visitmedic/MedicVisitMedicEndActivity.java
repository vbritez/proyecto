package com.tigo.cs.android.activity.visitmedic;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.api.entities.VisitMedicService;

public class MedicVisitMedicEndActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 17;
    }

    @Override
    public String getServiceEventCod() {
        return "ME";
    }

    private EditText motiveCode;
    private EditText observation;
    private Spinner notificationOption;
    private DatePicker notificationDate;
    private TimePicker notificationTime;

    private Integer optionSelected;
    private EditText notificationMsg;
    private Button finishButton;
    private Date selectedDate;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medic_visit_medic_end_main);
        onSetContentViewFinish();

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        motiveCode = (EditText) findViewById(R.id.motiveCode);
        notificationMsg = (EditText) findViewById(R.id.notificationMsg);
        observation = (EditText) findViewById(R.id.medicEndObservation);
        notificationDate = (DatePicker) findViewById(R.id.notificationDate);
        notificationTime = (TimePicker) findViewById(R.id.notificationTime);
        finishButton = (Button) findViewById(R.id.medicEndVisitButton);

        final List<String> opciones = new ArrayList<String>();
        opciones.add("no_notification");
        opciones.add("one_day_before");
        opciones.add("three_hour_before");
        opciones.add("one_hour_before");

        notificationOption = (Spinner) findViewById(R.id.notificationOption);
        MetaAdapter adapter = new MetaAdapter(this, android.R.layout.simple_spinner_item, opciones);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        notificationOption.setAdapter(adapter);
        notificationOption.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                optionSelected = ((int) id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                optionSelected = null;

            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new VisitMedicService();

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(),
                        serviceEvent.getServiceEventCod(),
                        motiveCode.getText(),
                        selectedDate != null ? selectedDate.getTime() : 0,
                        optionSelected, notificationMsg.getText(),
                        observation.getText());

                ((VisitMedicService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((VisitMedicService) entity).setMotiveCode(motiveCode.getText().toString());
                if (selectedDate != null) {
                    ((VisitMedicService) entity).setNextVisit((String.valueOf(selectedDate.getTime())));
                }
                if (!TextUtils.isEmpty(observation.getText())) {
                    ((VisitMedicService) entity).setObservation(observation.getText().toString());
                }
                entity.setRequiresLocation(false);

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(motiveCode.getText())) {
            Toast.makeText(
                    MedicVisitMedicEndActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.medic_clinic_motive_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (optionSelected > 0) {
            /*
             * validamos la fecha introducidas por el usuario
             */
            Calendar datePickerCalendar = Calendar.getInstance();
            Calendar nowCalendar = Calendar.getInstance();

            datePickerCalendar.set(Calendar.YEAR, notificationDate.getYear());
            datePickerCalendar.set(Calendar.MONTH, notificationDate.getMonth());
            datePickerCalendar.set(Calendar.DAY_OF_MONTH,
                    notificationDate.getDayOfMonth());
            datePickerCalendar.set(Calendar.HOUR, 0);
            datePickerCalendar.set(Calendar.MINUTE, 0);
            datePickerCalendar.set(Calendar.MILLISECOND, 0);

            nowCalendar.setTime(new Date());
            nowCalendar.set(Calendar.HOUR, 0);
            nowCalendar.set(Calendar.MINUTE, 0);
            nowCalendar.set(Calendar.MILLISECOND, 0);

            if (datePickerCalendar.getTime().getTime() <= nowCalendar.getTime().getTime()) {
                Toast.makeText(
                        MedicVisitMedicEndActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.medic_notif_date_restiction),
                        Toast.LENGTH_LONG).show();
                return false;
            }

            /*
             * validamos la horas introducidas por el usuario
             */
            nowCalendar.setTime(new Date());

            switch (optionSelected) {
            case 1:
                /*
                 * un dia antes
                 */
                if (!datePickerCalendar.after(nowCalendar)) {
                    /*
                     * notificamos que no puede notificar al pasado
                     */
                    Toast.makeText(
                            MedicVisitMedicEndActivity.this,
                            CsTigoApplication.getContext().getString(
                                    R.string.medic_notif_date_restiction),
                            Toast.LENGTH_LONG).show();
                    return false;

                }
                break;
            case 2:

                /*
                 * una hora antes
                 */
                if (notificationTime.getCurrentHour() - 3 >= nowCalendar.get(Calendar.HOUR)) {
                    /*
                     * notificamos que no puede notificar al pasado
                     */
                    Toast.makeText(
                            MedicVisitMedicEndActivity.this,
                            CsTigoApplication.getContext().getString(
                                    R.string.medic_notif_date_restiction),
                            Toast.LENGTH_LONG).show();
                    return false;
                }

                break;

            case 3:

                /*
                 * una hora antes
                 */
                if (notificationTime.getCurrentHour() - 1 >= nowCalendar.get(Calendar.HOUR)) {
                    /*
                     * notificamos que no puede notificar al pasado
                     */
                    Toast.makeText(
                            MedicVisitMedicEndActivity.this,
                            CsTigoApplication.getContext().getString(
                                    R.string.medic_notif_date_restiction),
                            Toast.LENGTH_LONG).show();
                    return false;
                }

                break;
            }

            datePickerCalendar.set(Calendar.YEAR, notificationDate.getYear());
            datePickerCalendar.set(Calendar.MONTH, notificationDate.getMonth());
            datePickerCalendar.set(Calendar.DAY_OF_MONTH,
                    notificationDate.getDayOfMonth());
            datePickerCalendar.set(Calendar.HOUR,
                    notificationTime.getCurrentHour());
            datePickerCalendar.set(Calendar.MINUTE,
                    notificationTime.getCurrentMinute());

            selectedDate = datePickerCalendar.getTime();

            if (TextUtils.isEmpty(notificationMsg.getText())) {
                Toast.makeText(
                        MedicVisitMedicEndActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.medic_visit_notification_msg_restriction),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }
        return true;
    }

    public class MetaAdapter extends ArrayAdapter<String> {

        public MetaAdapter(MedicVisitMedicEndActivity context,
                int textViewResourceId, List<String> opciones) {
            super(context, textViewResourceId, opciones);
            this.opciones = opciones;
        }

        private final List<String> opciones;

        @Override
        public final int getCount() {
            return opciones.size();
        }

        @Override
        public final String getItem(int position) {

            return opciones.get(position);
        }

        @Override
        public final long getItemId(int position) {

            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                View layout = null;
                final String metaEntity = opciones.get(position);

                LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                layout = li.inflate(android.R.layout.simple_spinner_item, null);

                TextView metaName = (TextView) layout.findViewById(android.R.id.text1);

                metaName.setText(CsTigoApplication.i18n(metaEntity));

                return layout;
            }
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                View layout = null;
                final String metaEntity = opciones.get(position);

                LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                layout = li.inflate(
                        android.R.layout.simple_spinner_dropdown_item, null);

                TextView metaName = (TextView) layout.findViewById(android.R.id.text1);

                metaName.setText(CsTigoApplication.i18n(metaEntity));

                return layout;
            }
            return convertView;
        }

    }

}
