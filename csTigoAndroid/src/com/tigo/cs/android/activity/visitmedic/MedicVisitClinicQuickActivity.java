package com.tigo.cs.android.activity.visitmedic;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.api.entities.OrderDetailService;
import com.tigo.cs.api.entities.OrderService;
import com.tigo.cs.api.entities.VisitMedicService;

public class MedicVisitClinicQuickActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 17;
    }

    @Override
    public String getServiceEventCod() {
        return "CQ";
    }

    private EditText clientCode;
    private EditText medicCode;
    private EditText initialKm;
    private EditText motiveCode;
    private EditText observation;
    private Spinner notificationOption;
    private DatePicker notificationDate;
    private TimePicker notificationTime;

    private Integer optionSelected;
    private EditText notificationMsg;
    private Button finishButton;
    private Date selectedDate;

    private OrderService order;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medic_visit_clinic_quick_main);
        onSetContentViewFinish();

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        clientCode = (EditText) findViewById(R.id.clinicStartClientCode);
        medicCode = (EditText) findViewById(R.id.medicCode);
        motiveCode = (EditText) findViewById(R.id.motiveCode);
        initialKm = (EditText) findViewById(R.id.initialKm);
        notificationMsg = (EditText) findViewById(R.id.notificationMsg);
        observation = (EditText) findViewById(R.id.medicEndObservation);
        notificationDate = (DatePicker) findViewById(R.id.notificationDate);
        notificationTime = (TimePicker) findViewById(R.id.notificationTime);
        finishButton = (Button) findViewById(R.id.orderRegisterButton);

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

        order = new OrderService();
        final List<OrderDetailService> details = new ArrayList<OrderDetailService>();
        order.setDetail(details);

        recreateDetails(false);

        ImageView addImage = (ImageView) findViewById(R.id.productAdd);
        addImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (validateUserInput() && validateDetails()) {

                    OrderDetailService firstDetail = new OrderDetailService();
                    order.getDetail().add(firstDetail);
                    firstDetail.setId(order.getDetail().size());

                    recreateDetails(true);
                }
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new VisitMedicService();

                if (!startUserMark()) {
                    return;
                }

                ServiceEventEntity detailServiceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        getServicecod(), "DETAIL");

                String detailPattern = detailServiceEvent.getMessagePattern();

                String detailPart = "";
                if (order.getDetail().size() > 0
                    && !TextUtils.isEmpty(order.getDetail().get(0).getProductCode())) {
                    for (OrderDetailService detail : order.getDetail()) {
                        detailPart = detailPart.concat(MessageFormat.format(
                                detailPattern, detail.getProductCode(),
                                detail.getQuantity()));
                    }
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(),
                        serviceEvent.getServiceEventCod(),
                        clientCode.getText(), medicCode.getText(),
                        initialKm.getText(), motiveCode.getText(),
                        selectedDate != null ? selectedDate.getTime() : 0,
                        optionSelected, notificationMsg.getText(),
                        observation.getText(), detailPart);

                ((VisitMedicService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((VisitMedicService) entity).setPlaceCode(clientCode.getText().toString()); // clientCode
                                                                                            // es
                                                                                            // local
                ((VisitMedicService) entity).setMedicCode(medicCode.getText().toString());
                if (!TextUtils.isEmpty(initialKm.getText())) {
                    ((VisitMedicService) entity).setInitialKm(initialKm.getText().toString());
                }
                ((VisitMedicService) entity).setMotiveCode(motiveCode.getText().toString());
                if (selectedDate != null) {
                    ((VisitMedicService) entity).setNextVisit((String.valueOf(selectedDate.getTime())));
                }
                ((VisitMedicService) entity).setNotificate(optionSelected.toString());
                if (!TextUtils.isEmpty(notificationMsg.getText())) {
                    ((VisitMedicService) entity).setNotificationDesc(notificationMsg.getText().toString());
                }
                if (!TextUtils.isEmpty(observation.getText())) {
                    ((VisitMedicService) entity).setObservation(observation.getText().toString());
                }
                ((VisitMedicService) entity).setDetail(order.getDetail());
                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(clientCode.getText())) {
            Toast.makeText(
                    MedicVisitClinicQuickActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.medic_clinic_code_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(medicCode.getText())) {
            Toast.makeText(
                    MedicVisitClinicQuickActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.medic_clinic_medic_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(motiveCode.getText())) {
            Toast.makeText(
                    MedicVisitClinicQuickActivity.this,
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

            if (datePickerCalendar.before(nowCalendar)) {
                Toast.makeText(
                        MedicVisitClinicQuickActivity.this,
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
                            MedicVisitClinicQuickActivity.this,
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
                            MedicVisitClinicQuickActivity.this,
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
                            MedicVisitClinicQuickActivity.this,
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
        }
        if (!validateDetails()) {
            return false;
        }
        return true;
    }

    public class MetaAdapter extends ArrayAdapter<String> {

        public MetaAdapter(MedicVisitClinicQuickActivity context,
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

    private boolean validateDetails() {

        /*
         * validamos que el usuario haya ingresado todos los campos requeridos
         * de los detalles
         */

        // if (order.getDetails().size() == 0) {
        // Toast.makeText(
        // MedicVisitClinicQuickActivity.this,
        // CsTigoApplication.getContext().getString(
        // R.string.order_details_not_empty),
        // Toast.LENGTH_LONG).show();
        // return false;
        // }

        for (OrderDetailService detail : order.getDetail()) {

            if (!TextUtils.isEmpty(detail.getProductCode())) {
                if (TextUtils.isEmpty(detail.getQuantity() != null ? detail.getQuantity().toString() : "")) {
                    Toast.makeText(
                            MedicVisitClinicQuickActivity.this,
                            CsTigoApplication.getContext().getString(
                                    R.string.order_quantity_not_empty),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            }

        }

        return true;

    }

    private void recreateDetails(boolean requestFocus) {
        LinearLayout productLayout = (LinearLayout) findViewById(R.id.orderProductlayout);
        productLayout.removeAllViews();

        for (int i = 0; i < order.getDetail().size(); i++) {
            final OrderDetailService detail = order.getDetail().get(i);
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout layout = (RelativeLayout) li.inflate(
                    R.layout.medic_visit_product_register_subcontent, null);

            detail.setId(i + 1);
            TextView id = (TextView) layout.findViewById(R.id.orderProductCount);
            id.setText(detail.getId().toString());
            productLayout.addView(layout);

            if (requestFocus && i == order.getDetail().size() - 1) {
                EditText productCode = (EditText) layout.findViewById(R.id.terportRegisterContainerEdit);
                productCode.requestFocus();
            }
            ImageView deleteImage = (ImageView) layout.findViewById(R.id.productRemove);
            deleteImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    order.getDetail().remove(detail);
                    recreateDetails(false);
                }
            });

            EditText productCode = (EditText) layout.findViewById(R.id.terportRegisterContainerEdit);
            EditText quantity = (EditText) layout.findViewById(R.id.terportRegisterUbicationEdit);

            if (detail.getProductCode() != null) {
                productCode.setText(detail.getProductCode());
            }
            if (detail.getQuantity() != null) {
                quantity.setText(detail.getQuantity().toString());
            }

            productCode.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setProductCode(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            quantity.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setQuantity(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

        }
    }
}
