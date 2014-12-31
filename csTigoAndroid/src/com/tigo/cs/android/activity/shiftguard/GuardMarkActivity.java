package com.tigo.cs.android.activity.shiftguard;

import java.text.MessageFormat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.api.entities.ShiftGuardService;

public class GuardMarkActivity extends AbstractActivity {

    @Override
    public String getServiceEventCod() {
        return "REGISTER";
    }

    @Override
    public Integer getServicecod() {
        return 15;
    }

    private EditText guardCode;
    private EditText observation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.shiftguard_mark_main);
        onSetContentViewFinish();

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        guardCode = (EditText) findViewById(R.id.guardMarkGuardCode);
        observation = (EditText) findViewById(R.id.guardMarkObservation);

        Button guardSendMarkButton = (Button) findViewById(R.id.guardSendMarkButton);
        guardSendMarkButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new ShiftGuardService();

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(), guardCode.getText(),
                        observation.getText());

                ((ShiftGuardService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((ShiftGuardService) entity).setGuardCode(guardCode.getText().toString());
                if (!TextUtils.isEmpty(observation.getText())) {
                    ((ShiftGuardService) entity).setObservation(observation.getText().toString());
                }

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        /*
         * validamos que el usuario haya completado el campo codigo de guardia
         */
        if (TextUtils.isEmpty(guardCode.getText())) {
            Toast.makeText(
                    GuardMarkActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.guard_mark_code_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
