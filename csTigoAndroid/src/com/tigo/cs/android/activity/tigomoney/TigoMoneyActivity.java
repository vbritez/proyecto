package com.tigo.cs.android.activity.tigomoney;

import java.util.Date;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.SessionEntity;

public class TigoMoneyActivity extends AbstractActivity {

    private OnClickListener manageSessionOnClickListener;
    private OnClickListener consultIdClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tigo_money_main);
        onSetContentViewFinish();

        Button manageSessionButton = (Button) findViewById(R.id.manageSessionButton);
        Button consultIdButton = (Button) findViewById(R.id.consultIdButton);

        manageSessionOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(TigoMoneySessionActivity.class);
            }
        };

        consultIdClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                SessionEntity session = CsTigoApplication.getSessionHelper().findByImsi(
                        telephonyManager.getSubscriberId());
                if (session == null
                    || new Date().after(session.getExpirationDate())) {
                    Toast.makeText(
                            TigoMoneyActivity.this,
                            CsTigoApplication.getContext().getString(
                                    R.string.tigomoney_must_init_session_for_consulting),
                            Toast.LENGTH_LONG).show();
                } else {
                    startEventActivity(TigoMoneyConsultIdActivity.class);
                }

            }
        };

        manageSessionButton.setOnClickListener(manageSessionOnClickListener);
        consultIdButton.setOnClickListener(consultIdClickListener);

    }

}
