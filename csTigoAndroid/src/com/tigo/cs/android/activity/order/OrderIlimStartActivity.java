package com.tigo.cs.android.activity.order;

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
import com.tigo.cs.api.entities.OrderService;

public class OrderIlimStartActivity extends AbstractActivity {

    private EditText clientCode;
    private EditText priceList;
    private EditText saleCondition;
    private EditText observation;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_ilim_start_main);
        onSetContentViewFinish();

        clientCode = (EditText) findViewById(R.id.orderClientCodeEdit);
        priceList = (EditText) findViewById(R.id.orderPriceListEdit);
        saleCondition = (EditText) findViewById(R.id.orderSaleConditionEdit);
        observation = (EditText) findViewById(R.id.orderObservationCodeEdit);

        Button register = (Button) findViewById(R.id.orderRegisterButton);
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
                        serviceEvent.getServiceEventCod(),
                        clientCode.getText(), saleCondition.getText(),
                        priceList.getText(), observation.getText());

                ((OrderService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((OrderService) entity).setClientCode(clientCode.getText().toString());
                ((OrderService) entity).setPriceList(priceList.getText().toString());
                ((OrderService) entity).setInvoiceType(saleCondition.getText().toString());
                if (!TextUtils.isEmpty(observation.getText())) {
                    ((OrderService) entity).setObservation(observation.getText().toString());
                }

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {

        /*
         * validamos que el usuario haya completado los campos de la cabecera
         */
        if (TextUtils.isEmpty(clientCode.getText())) {
            Toast.makeText(
                    OrderIlimStartActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.order_client_code_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(priceList.getText())) {
            Toast.makeText(
                    OrderIlimStartActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.order_price_list_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(saleCondition.getText())) {
            Toast.makeText(
                    OrderIlimStartActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.order_sale_condition_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public Integer getServicecod() {
        return 2;
    }

    @Override
    public String getServiceEventCod() {
        return "INI";
    }

}
