package com.tigo.cs.android.activity.interfisa;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;

public class InterfisaActivity extends AbstractActivity {

    private OnClickListener registerInterfisaInformconfOnClickListener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.interfisa_main);
        onSetContentViewFinish();
        Button registerInterfisaInformconfButton = (Button) findViewById(R.id.interfisaInformconfButton);

        registerInterfisaInformconfOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                startEventActivity(InterfisaInformconfActivity.class);

            }
        };

        registerInterfisaInformconfButton.setOnClickListener(registerInterfisaInformconfOnClickListener);

    }

}