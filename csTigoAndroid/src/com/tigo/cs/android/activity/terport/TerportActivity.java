package com.tigo.cs.android.activity.terport;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.activity.MainActivity;
import com.tigo.cs.android.helper.domain.GlobalParameterEntity;

public class TerportActivity extends AbstractActivity {

    private OnClickListener terportRegisterOnClickListener;
    private OnClickListener terportLogoutOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terport_main);
        onSetContentViewFinish();
        Button terportRegisterButton = (Button) findViewById(R.id.terportRegisterButton);
        Button terportLogoutButton = (Button) findViewById(R.id.terportLogoutButton);

        terportRegisterOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(TerportRegisterActivity.class);
            }
        };
        terportLogoutOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                GlobalParameterEntity entity = CsTigoApplication.getGlobalParameterHelper().getTerportManagerEntity();
                entity.setParameterValue("");
                CsTigoApplication.getGlobalParameterHelper().update(entity);

                startEventActivity(MainActivity.class);

            }
        };

        terportRegisterButton.setOnClickListener(terportRegisterOnClickListener);
        terportLogoutButton.setOnClickListener(terportLogoutOnClickListener);

    }

}
