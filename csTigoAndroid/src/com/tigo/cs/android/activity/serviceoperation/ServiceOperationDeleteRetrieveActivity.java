package com.tigo.cs.android.activity.serviceoperation;

import java.text.MessageFormat;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.ServiceEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.api.entities.PermissionService;
import com.tigo.cs.api.entities.ServiceOperationService;

public class ServiceOperationDeleteRetrieveActivity extends AbstractActivity {

    private OnClickListener serviceUpdateOnClickListener;
    private OnClickListener serviceRetrieveDeleteOnClickListener;
    private ServiceEntity serviceSelected;

    @Override
    public Integer getServicecod() {
        return 101;
    }

    @Override
    public String getServiceEventCod() {
        return "DS";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_operation_delete_retrieve_main);
        onSetContentViewFinish();

        Spinner serviceSpinner = (Spinner) findViewById(R.id.serviceSelector);
        Button serviceUpdateButton = (Button) findViewById(R.id.serviceUpdateButton);
        Button serviceRetrieveQuery = (Button) findViewById(R.id.serviceOperationDeleteRetrieveButton);

        List<ServiceEntity> serviceList = CsTigoApplication.getServiceHelper().findAllDeletable(
                true);

        if (serviceList != null) {
            String[] metaArray = new String[serviceList.size()];
            int i = 0;
            for (ServiceEntity entity : serviceList) {
                metaArray[i++] = CsTigoApplication.i18n(entity.getServicename());
            }

            ServiceAdapter adapter = new ServiceAdapter(this, android.R.layout.simple_spinner_item, serviceList);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            serviceSpinner.setAdapter(adapter);
            serviceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    serviceSelected = CsTigoApplication.getServiceHelper().find(
                            id);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    serviceSelected = null;
                }
            });
        } else {
            serviceSpinner.setVisibility(Spinner.INVISIBLE);
        }

        serviceUpdateOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                CsTigoApplication.getServiceHelper().disableAllServiceDeletable();
                ServiceEventEntity eventEntity = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        0, "DS");

                String msgPattern = eventEntity.getMessagePattern();

                String msg = MessageFormat.format(msgPattern,
                        eventEntity.getService().getServicecod(),
                        eventEntity.getServiceEventCod());

                entity = new PermissionService();
                entity.setServiceCod(eventEntity.getService().getServicecod());
                entity.setEvent(eventEntity.getServiceEventCod());
                entity.setRequiresLocation(eventEntity.getRequiresLocation());

                endUserMark(msg);

            }
        };

        serviceRetrieveDeleteOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                entity = new ServiceOperationService();

                if (serviceSelected == null) {
                    Toast.makeText(
                            ServiceOperationDeleteRetrieveActivity.this,
                            CsTigoApplication.getContext().getString(
                                    R.string.service_operation_delete_must_select_service),
                            Toast.LENGTH_LONG).show();
                    return;
                }

                ServiceEventEntity eventEntity = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        101, "RETDS");

                String msgPattern = eventEntity.getMessagePattern();

                String msg = MessageFormat.format(msgPattern,
                        eventEntity.getService().getServicecod(),
                        eventEntity.getServiceEventCod(),
                        serviceSelected.getServicecod().longValue());

                entity.setRequiresLocation(eventEntity.getRequiresLocation());
                entity.setServiceCod(eventEntity.getService().getServicecod());
                entity.setEvent(eventEntity.getServiceEventCod());
                ((ServiceOperationService) entity).setServiceToOperate(serviceSelected.getServicecod().longValue());

                endUserMark(msg);

            }
        };
        serviceUpdateButton.setOnClickListener(serviceUpdateOnClickListener);
        serviceRetrieveQuery.setOnClickListener(serviceRetrieveDeleteOnClickListener);

    }

    public class ServiceAdapter extends ArrayAdapter<ServiceEntity> {

        public ServiceAdapter(ServiceOperationDeleteRetrieveActivity context,
                int textViewResourceId, List<ServiceEntity> metaList) {
            super(context, textViewResourceId, metaList);
            this.serviceList = metaList;
        }

        private final List<ServiceEntity> serviceList;

        @Override
        public final int getCount() {
            return serviceList.size();
        }

        @Override
        public final ServiceEntity getItem(int position) {
            return serviceList.get(position);
        }

        @Override
        public final long getItemId(int position) {
            return serviceList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                View layout = null;
                final ServiceEntity serviceEntity = serviceList.get(position);
                LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                layout = li.inflate(android.R.layout.simple_spinner_item, null);
                TextView serviceName = (TextView) layout.findViewById(android.R.id.text1);
                serviceName.setText(CsTigoApplication.i18n(serviceEntity.getServicename()));
                return layout;
            }
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                View layout = null;
                final ServiceEntity serviceEntity = serviceList.get(position);
                LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                layout = li.inflate(
                        android.R.layout.simple_spinner_dropdown_item, null);
                TextView serviceName = (TextView) layout.findViewById(android.R.id.text1);
                serviceName.setText(CsTigoApplication.i18n(serviceEntity.getServicename()));

                return layout;
            }
            return convertView;
        }

    }

}