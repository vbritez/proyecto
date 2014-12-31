package com.tigo.cs.android.activity.interfisa;

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
import com.tigo.cs.api.entities.InterfisaService;

public class InterfisaInformconfActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return -2;
    }

    @Override
    public String getServiceEventCod() {
        return "INFORMCONF";
    }

    private EditText interfisaInformconfDocumentEdit;
    private Button interfisaInformconfRegisterButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interfisa_informconf_main);
        onSetContentViewFinish();

        interfisaInformconfDocumentEdit = (EditText) findViewById(R.id.interfisaInformconfDocumentEdit);

        interfisaInformconfRegisterButton = (Button) findViewById(R.id.interfisaInformconfRegisterButton);
        interfisaInformconfRegisterButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new InterfisaService();

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(),
                        serviceEvent.getServiceEventCod(),
                        interfisaInformconfDocumentEdit.getText());

                ((InterfisaService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((InterfisaService) entity).setDocument(interfisaInformconfDocumentEdit.getText().toString());

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(interfisaInformconfDocumentEdit.getText())) {
            Toast.makeText(
                    InterfisaInformconfActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.informconf_interfisa_document_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
