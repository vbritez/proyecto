package com.tigo.cs.android.activity.serviceoperation;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.ServiceEntity;
import com.tigo.cs.android.helper.domain.ServiceOperationDataEntity;
import com.tigo.cs.android.helper.domain.ServiceOperationEntity;

public class ServiceOperationDeleteActivity extends AbstractActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_operation_delete_main);
        reCreateViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.service_operation_delete_main);
        reCreateViews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.service_operation_delete_main);
        reCreateViews();
    }

    private void reCreateViews() {
        onSetContentViewFinish();

        TextView titulo = (TextView) findViewById(R.id.serviceOperationDeleteTitle);
        ListView listView = (ListView) findViewById(R.id.messages);

        ServiceOperationEntity serviceOperationEntity = CsTigoApplication.getServiceOperationHelper().findLast();
        final List<ServiceOperationDataEntity> serviceOperationDataList = CsTigoApplication.getServiceOperationDataHelper().findAll(
                serviceOperationEntity);

        ServiceEntity service = CsTigoApplication.getServiceHelper().findByServiceCod(
                serviceOperationEntity.getServicecod());

        titulo.setText(CsTigoApplication.i18n(service.getServicename()));

        listView.setAdapter(new ServiceOperationDataAdapter(serviceOperationDataList));

        /*
         * habilitamos el componente para realizar la busqueda de mensajes
         * dentro de la pantalla
         */
        listView.setTextFilterEnabled(true);

        /*
         * agregamos un listener para poder visualizar los mensajes de la
         * plataforma
         */
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * abrimos la nueva actividad para visualizar el mensaje
                 * seleccionado
                 */

                Intent serviceOperationDeleteConfirmIntent = new Intent(ServiceOperationDeleteActivity.this, ServiceOperationDeleteConfirmActivity.class);
                serviceOperationDeleteConfirmIntent.putExtra("id", id);
                startActivity(serviceOperationDeleteConfirmIntent);

            }
        });

    }

    public class ServiceOperationDataAdapter extends BaseAdapter {

        private final List<ServiceOperationDataEntity> serviceOperationDataList;

        public ServiceOperationDataAdapter(
                List<ServiceOperationDataEntity> serviceList) {

            this.serviceOperationDataList = serviceList;
        }

        @Override
        public int getCount() {
            if (serviceOperationDataList != null
                && serviceOperationDataList.size() > 0) {
                return serviceOperationDataList.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            if (serviceOperationDataList != null
                && serviceOperationDataList.size() > 0) {
                return serviceOperationDataList.get(position);
            }
            return 0;

        }

        @Override
        public long getItemId(int position) {
            if (serviceOperationDataList != null
                && serviceOperationDataList.size() > 0) {
                return serviceOperationDataList.get(position).getId();
            }
            return 0;

        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                View layout = null;
                final ServiceOperationDataEntity messageEntity = serviceOperationDataList.get(position);
                if (messageEntity.getServicemsg() != null) {
                    LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE);
                    layout = li.inflate(R.layout.history_detail, null);

                    TextView mensaje = (TextView) layout.findViewById(R.id.mensaje);
                    mensaje.setText(messageEntity.getServicemsg());

                } else {
                    TextView text = new TextView(parent.getContext());
                    text.setPadding(10, 6, 10, 6);

                    if (serviceOperationDataList.size() == 1) {
                        text.setText(CsTigoApplication.getContext().getString(
                                R.string.sms_no_messages));
                    }

                    return text;
                }
                return layout;
            }
            return convertView;
        }
    }

}