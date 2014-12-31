package com.tigo.cs.android.activity.order;

import java.text.MessageFormat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.api.entities.OrderService;

public class OrderIlimFinishActivity extends AbstractActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_ilim_finish_main);
        onSetContentViewFinish();

        Button register = (Button) findViewById(R.id.finishButton);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new OrderService();

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(),
                        serviceEvent.getServiceEventCod());

                ((OrderService) entity).setEvent(serviceEvent.getServiceEventCod());

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        return true;
    }

    @Override
    public Integer getServicecod() {
        return 2;
    }

    @Override
    public String getServiceEventCod() {
        return "END";
    }

}
