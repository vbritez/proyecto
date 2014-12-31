package com.tigo.cs.android.activity;

import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.helper.domain.GlobalParameterEntity;

public class InternetOptionActivity extends AbstractActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.internet_option_main);
        onSetContentViewFinish();

        CheckBox internetEnabled = (CheckBox) findViewById(R.id.internetEnabledCheck);
        // CheckBox apnChange = (CheckBox) findViewById(R.id.apnChangeCheck);
        // TextView apnName = (TextView) findViewById(R.id.apnName);

        internetEnabled.setChecked(CsTigoApplication.getGlobalParameterHelper().getInternetEnabled());
        // apnChange.setChecked(CsTigoApplication.getGlobalParameterHelper().getInternetApnChange());
        // apnName.setText(CsTigoApplication.getGlobalParameterHelper().getInternetApnValue());

        internetEnabled.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (verifyDeviceEnabled()) {

                    GlobalParameterEntity globalParameterEntity = CsTigoApplication.getGlobalParameterHelper().getInternetEnabledEntity();
                    updateBoolean(globalParameterEntity, isChecked);
                } else {
                    buttonView.setChecked(!isChecked);
                }

            }
        });

        // apnChange.setOnCheckedChangeListener(new
        // CompoundButton.OnCheckedChangeListener() {
        //
        // @Override
        // public void onCheckedChanged(CompoundButton buttonView, boolean
        // isChecked) {
        //
        // if (verifyDeviceEnabled()) {
        //
        // GlobalParameterEntity globalParameterEntity =
        // CsTigoApplication.getGlobalParameterHelper().getInternetApnChangeEntity();
        // updateBoolean(globalParameterEntity, isChecked);
        // } else {
        // buttonView.setChecked(!isChecked);
        // }
        //
        // }
        // });
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
                    InternetOptionActivity.this,
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
