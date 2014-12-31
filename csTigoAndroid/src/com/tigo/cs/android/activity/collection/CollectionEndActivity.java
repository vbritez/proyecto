package com.tigo.cs.android.activity.collection;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.api.entities.CollectionInvoiceService;
import com.tigo.cs.api.entities.CollectionPaymentService;
import com.tigo.cs.api.entities.CollectionService;

public class CollectionEndActivity extends AbstractActivity {

    private CollectionService collection;
    private List<CollectionInvoiceService> invoices;
    private List<CollectionPaymentService> payments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_end_main);
        onSetContentViewFinish();

        collection = new CollectionService();
        invoices = new ArrayList<CollectionInvoiceService>();
        collection.setInvoices(invoices);
        payments = new ArrayList<CollectionPaymentService>();
        collection.setPayments(payments);

        Button register = (Button) findViewById(R.id.collectionEndButton);

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new CollectionService();

                if (!startUserMark()) {
                    return;
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(), "", "", "", "");

                ((CollectionService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((CollectionService) entity).setInvoices(invoices);
                ((CollectionService) entity).setPayments(payments);

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
        return 5;
    }

    @Override
    public String getServiceEventCod() {
        return "CLOSE";
    }

}
