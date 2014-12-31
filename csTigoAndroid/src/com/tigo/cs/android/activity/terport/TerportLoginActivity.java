package com.tigo.cs.android.activity.terport;

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
import com.tigo.cs.api.entities.TerportService;

public class TerportLoginActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 24;
    }

    @Override
    public String getServiceEventCod() {
        return "LOGIN";
    }

    private EditText username;
    private EditText password;
    private Button initSessionButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terport_login_main);
        onSetContentViewFinish();

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        username = (EditText) findViewById(R.id.terportUsernameEdit);
        password = (EditText) findViewById(R.id.terportPasswordEdit);

        initSessionButton = (Button) findViewById(R.id.terportLoginButton);
        initSessionButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new TerportService();

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(),
                        serviceEvent.getServiceEventCod(), username.getText(),
                        password.getText());

                ((TerportService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((TerportService) entity).setUsername(username.getText().toString());
                ((TerportService) entity).setPassword(password.getText().toString());
                ((TerportService) entity).setRequiresLocation(false);

                endUserMark(null);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(username.getText())) {
            Toast.makeText(
                    TerportLoginActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.terport_username_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(password.getText())) {
            Toast.makeText(
                    TerportLoginActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.terport_password_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
