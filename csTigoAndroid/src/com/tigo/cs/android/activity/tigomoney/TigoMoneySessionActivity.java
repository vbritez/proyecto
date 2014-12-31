package com.tigo.cs.android.activity.tigomoney;

import java.text.MessageFormat;
import java.util.Date;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.SessionEntity;
import com.tigo.cs.api.entities.TigoMoneyService;

public class TigoMoneySessionActivity extends AbstractActivity {

    private static boolean authenticated = false;

    private EditText cellphoneNumber;
    private EditText password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tigo_money_session_main);
        onSetContentViewFinish();

        SessionEntity session = CsTigoApplication.getSessionHelper().findByImsi(
                telephonyManager.getSubscriberId());
        if (session == null) { /*
                                * abrir la pantalla de autenticacion, ya que el
                                * usuario no esta autenticado con el imsi
                                */
            authenticated = false;
        } else if (new Date().after(session.getExpirationDate())) { /*
                                                                     * usuario
                                                                     * tiene una
                                                                     * sesion
                                                                     * expirada
                                                                     */
            authenticated = false;
            CsTigoApplication.getSessionHelper().deleteAll();
        } else {/*
                 * usuario ya se encuentra autenticado, ir directamente a la
                 * pantalla de consulta
                 */
            authenticated = true;
        }

        openScreen();

    }

    private void openScreen() {
        LinearLayout tigoMoneyLayout = (LinearLayout) findViewById(R.id.tigomoneySessionlayout);
        tigoMoneyLayout.removeAllViews();

        LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout layout;

        if (!authenticated) {
            layout = (RelativeLayout) li.inflate(
                    R.layout.tigo_money_login_subcontent, null);
            cellphoneNumber = (EditText) layout.findViewById(R.id.cellphoneNumberEdit);
            password = (EditText) layout.findViewById(R.id.passwordEdit);
            cellphoneNumber.requestFocus();
            Button loginButton = (Button) layout.findViewById(R.id.tigoMoneyLoginButton);
            loginButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {

                    entity = new TigoMoneyService();

                    if (!startUserMark()) {
                        return;
                    }

                    String msg = MessageFormat.format(
                            serviceEvent.getMessagePattern(),
                            service.getServicecod(),
                            cellphoneNumber.getText().toString(),
                            password.getText().toString());

                    ((TigoMoneyService) entity).setEvent(serviceEvent.getServiceEventCod());
                    ((TigoMoneyService) entity).setCellphoneNumber(cellphoneNumber.getText().toString());
                    ((TigoMoneyService) entity).setPassword(password.getText().toString());
                    ((TigoMoneyService) entity).setRequiresLocation(false);

                    endUserMark(msg);

                }
            });

        } else {
            layout = (RelativeLayout) li.inflate(
                    R.layout.tigo_money_pdv_logged_subcontent, null);

            Button logoutButton = (Button) layout.findViewById(R.id.tigomoneyLogoutButton);
            logoutButton.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    CsTigoApplication.getSessionHelper().deleteSessions();
                    Toast.makeText(
                            TigoMoneySessionActivity.this,
                            CsTigoApplication.getContext().getString(
                                    R.string.tigomoney_logged_out_successfully),
                            Toast.LENGTH_LONG).show();
                    goToMain();
                }
            });
        }

        tigoMoneyLayout.addView(layout);
    }

    @Override
    protected boolean validateUserInput() {
        if (!validateDetails()) {
            return false;
        }

        return true;
    }

    private boolean validateDetails() {

        /*
         * validamos que el usuario haya ingresado todos los campos requeridos
         * de los detalles
         */

        if (!authenticated) {
            if (TextUtils.isEmpty(cellphoneNumber.getText())) {
                Toast.makeText(
                        TigoMoneySessionActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.tigomoney_cellphone_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }

            if (cellphoneNumber.getText().length() < 6) {
                Toast.makeText(
                        TigoMoneySessionActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.tigomoney_cellphone_too_short),
                        Toast.LENGTH_LONG).show();
                return false;
            }

            if (TextUtils.isEmpty(password.getText())) {
                Toast.makeText(
                        TigoMoneySessionActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.tigomoney_password_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;

    }

    @Override
    public Integer getServicecod() {
        return -3;
    }

    @Override
    public String getServiceEventCod() {
        return "LOGIN";
    }

}
