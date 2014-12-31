package com.tigo.cs.android.activity.serviceoperation;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.GlobalParameterEntity;

public class ServiceOptionActivity extends AbstractActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.service_option_main);
        onSetContentViewFinish();

        CheckBox priceAlphaOption = (CheckBox) findViewById(R.id.priceAlphaOption);

        CheckBox ciAlphaOption = (CheckBox) findViewById(R.id.ciAlphaOption);

        priceAlphaOption.setChecked(CsTigoApplication.getGlobalParameterHelper().getOrderPricelpha());
        ciAlphaOption.setChecked(CsTigoApplication.getGlobalParameterHelper().getCiAlphaNumeric());

        /*
         * cada vez que exista un cambio en la opcion de visualizacion de
         * servicios deshabilitados lo persistimos a la base de datos
         * directamente para al recargar la lista de servicios
         */
        priceAlphaOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (verifyDeviceEnabled()) {

                    GlobalParameterEntity globalParameterEntity = CsTigoApplication.getGlobalParameterHelper().getOrderPricelphaEntity();
                    updateBoolean(globalParameterEntity, isChecked);
                } else {
                    buttonView.setChecked(!isChecked);
                }

            }
        });

        /*
         * cada vez que exista un cambio en la opcion de visualizacion de
         * servicios deshabilitados lo persistimos a la base de datos
         * directamente para al recargar la lista de servicios
         */
        ciAlphaOption.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (verifyDeviceEnabled()) {

                    GlobalParameterEntity globalParameterEntity = CsTigoApplication.getGlobalParameterHelper().getCiAlphaNumericEntity();
                    updateBoolean(globalParameterEntity, isChecked);
                } else {
                    buttonView.setChecked(!isChecked);
                }

            }
        });
    }

    private boolean verifyDeviceEnabled() {
        /*
         * verificamos si el dispositivo se encuentra habilitado antes de
         * solicitar la actualizacion de servicios
         */

        if (!CsTigoApplication.getGlobalParameterHelper().getDeviceEnabled()) {

            /*
             * notificamos que el dispositivo no esta habilitado
             */
            Toast.makeText(
                    ServiceOptionActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.no_enabled_device), Toast.LENGTH_LONG).show();
            return false;

        }
        return true;
    }

    private void updateBoolean(GlobalParameterEntity globalParameterEntity, boolean isChecked) {
        globalParameterEntity.setParameterValue(isChecked ? "1" : "0");
        CsTigoApplication.getGlobalParameterHelper().update(
                globalParameterEntity);
    }

}
