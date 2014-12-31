package com.tigo.cs.android.activity;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.helper.domain.ServiceEntity;

public class MainActivity extends AbstractActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        GridView mGrid = (GridView) findViewById(R.id.service_grid);
        mGrid.setAdapter(new ServiceAdapter());
        onSetContentViewFinish();
        CsTigoApplication.notificatApn = true;
        // APNUtil.initApplication();
        CsTigoApplication.notificatApn = false;

        // Calendar calendar = Calendar.getInstance();
        // calendar.set(Calendar.HOUR, 0);
        // calendar.set(Calendar.MINUTE, 0);
        // calendar.set(Calendar.SECOND, 0);
        // calendar.set(Calendar.MILLISECOND, 0);
        //
        // calendar.set(Calendar.MONTH, 10);
        // calendar.set(Calendar.DAY_OF_MONTH, 25);
        // calendar.set(Calendar.YEAR, 2014);
        //
        // long baseTime = calendar.getTime().getTime();
        // long currentTime = new Date().getTime();
        //
        // if (baseTime <= currentTime) {
        // throw new RuntimeException();
        // }

    }

    @Override
    protected void onResume() {
        super.onResume();
        GridView mGrid = (GridView) findViewById(R.id.service_grid);
        mGrid.setAdapter(new ServiceAdapter());
        onSetContentViewFinish();
        // APNUtil.initApplication();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        GridView mGrid = (GridView) findViewById(R.id.service_grid);
        mGrid.setAdapter(new ServiceAdapter());
        onSetContentViewFinish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public class ServiceAdapter extends BaseAdapter {

        private final List<ServiceEntity> serviceList;
        final Map<Integer, Class> servicesClass = CsTigoApplication.getServicios();

        public ServiceAdapter() {

            serviceList = CsTigoApplication.getServiceHelper().findAllPlatformService(
                    CsTigoApplication.getGlobalParameterHelper().getServiceShowDisabled());
        }

        /**
         * creamos el layout con la descripcion y la imagen
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                View layout = null;
                ImageView i = null;
                final ServiceEntity serviceEntity = serviceList.get(position);

                LayoutInflater li = (LayoutInflater) MainActivity.this.getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                layout = li.inflate(R.layout.service_item, null);

                TextView tv = (TextView) layout.findViewById(R.id.grid_item_text);
                tv.setText(CsTigoApplication.i18n(serviceEntity.getServicename()));

                i = (ImageView) layout.findViewById(R.id.grid_item_image);
                // countColor = 0 + (int) (Math.random() * ((colors.length - 1 -
                // 0) + 1));
                // layout.setBackgroundResource(colors[countColor]);
                switch (serviceEntity.getServicecod()) {
                case 1:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.visit);
                    } else {
                        i.setImageResource(R.drawable.visit_disabled);
                    }
                    break;
                case 2:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.order);
                    } else {
                        i.setImageResource(R.drawable.order_disabled);
                    }
                    break;

                case 5:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.cobranza);
                    } else {
                        i.setImageResource(R.drawable.cobranza_disabled);
                    }
                    break;
                case 10:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.inventario);
                    } else {
                        i.setImageResource(R.drawable.inventario_disabled);
                    }
                    break;
                case 11:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.attendance);
                    } else {
                        i.setImageResource(R.drawable.attendance_disabled);
                    }
                    break;
                case 15:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.shift_guard);
                    } else {
                        i.setImageResource(R.drawable.shift_guard_disabled);
                    }
                    break;
                case 17:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.medic_visit);
                    } else {
                        i.setImageResource(R.drawable.medic_visit_disabled);
                    }
                    break;
                case 18:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.courier);
                    } else {
                        i.setImageResource(R.drawable.courier_disabled);
                    }
                    break;
                case 20:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.dynamicform);
                    } else {
                        i.setImageResource(R.drawable.dynamicform_disabled);
                    }
                    break;
                case 24:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.terport);
                    } else {
                        i.setImageResource(R.drawable.terport_disabled);
                    }
                    break;
                case 25:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.supervisor);
                    } else {
                        i.setImageResource(R.drawable.supervisor_disabled);
                    }
                    break;
                case -2:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.interfisa);
                    } else {
                        i.setImageResource(R.drawable.interfisa_disabled);
                    }
                    break;
                case -3:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.tigo_money);
                    } else {
                        i.setImageResource(R.drawable.tigo_money_disabled);
                    }
                    break;
                default:
                    if (serviceEntity.getEnabled()) {
                        i.setImageResource(R.drawable.shift_guard);
                    } else {
                        i.setImageResource(R.drawable.shift_guard_disabled);
                    }
                    break;
                }

                i.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (serviceEntity.getEnabled()) {
                            CsTigoApplication.vibrate(false);
                            startActivity(new Intent(MainActivity.this, servicesClass.get(serviceEntity.getServicecod())));

                        }
                    }
                });
                return layout;
            }
            return convertView;

        }

        @Override
        public final int getCount() {
            return serviceList.size();
        }

        @Override
        public final Object getItem(int position) {

            return serviceList.get(position);
        }

        @Override
        public final long getItemId(int position) {
            return position;
        }
    }

}
