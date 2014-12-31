package com.tigo.cs.android.activity.inventory;

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
import com.tigo.cs.api.entities.InventoryService;

public class InventoryActivity extends AbstractActivity {
    @Override
    public Integer getServicecod() {
        return 10;
    }

    @Override
    public String getServiceEventCod() {
        return "REGISTER";
    }

    private EditText depositCode;
    private EditText productCode;
    private EditText quantity;
    private Button register;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_main);
        onSetContentViewFinish();

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        depositCode = (EditText) findViewById(R.id.inventoryDepositCode);
        productCode = (EditText) findViewById(R.id.inventoryProductCode);
        quantity = (EditText) findViewById(R.id.inventoryQuantity);

        register = (Button) findViewById(R.id.inventoryButton);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new InventoryService();

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(), depositCode.getText(),
                        productCode.getText(), quantity.getText());

                ((InventoryService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((InventoryService) entity).setDepositCode(depositCode.getText().toString());
                ((InventoryService) entity).setProductCode(productCode.getText().toString());
                ((InventoryService) entity).setQuantity(quantity.getText().toString());

                endUserMark(msg);
            }
        });

    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(depositCode.getText())) {
            Toast.makeText(
                    InventoryActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.inventory_deposit_code_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(productCode.getText())) {
            Toast.makeText(
                    InventoryActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.inventory_product_code_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        if (TextUtils.isEmpty(quantity.getText())) {
            Toast.makeText(
                    InventoryActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.inventory_quantity_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
