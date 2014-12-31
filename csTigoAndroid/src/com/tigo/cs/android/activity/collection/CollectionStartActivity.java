package com.tigo.cs.android.activity.collection;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.api.entities.CollectionInvoiceService;
import com.tigo.cs.api.entities.CollectionPaymentService;
import com.tigo.cs.api.entities.CollectionService;

public class CollectionStartActivity extends AbstractActivity {

    private EditText clientCode;
    private EditText receiptNumber;

    private CollectionService collection;
    private List<CollectionInvoiceService> invoices;
    private List<CollectionPaymentService> payments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_start_main);
        onSetContentViewFinish();

        collection = new CollectionService();
        invoices = new ArrayList<CollectionInvoiceService>();
        collection.setInvoices(invoices);
        payments = new ArrayList<CollectionPaymentService>();
        collection.setPayments(payments);

        clientCode = (EditText) findViewById(R.id.collectionClientCodeEdit);
        receiptNumber = (EditText) findViewById(R.id.collectionReceiptNumEdit);
        Button register = (Button) findViewById(R.id.collectionRegisterButton);

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new CollectionService();

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(), clientCode.getText(),
                        receiptNumber.getText(), "", "");

                ((CollectionService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((CollectionService) entity).setClientCode(clientCode.getText().toString());
                ((CollectionService) entity).setReceiptNumber(receiptNumber.getText().toString());
                ((CollectionService) entity).setInvoices(invoices);
                ((CollectionService) entity).setPayments(payments);

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
                    CollectionStartActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.collection_client_code_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(receiptNumber.getText())) {
            Toast.makeText(
                    CollectionStartActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.collection_receipt_number_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    @Override
    public Integer getServicecod() {
        return 5;
    }

    @Override
    public String getServiceEventCod() {
        return "OPEN";
    }

}
