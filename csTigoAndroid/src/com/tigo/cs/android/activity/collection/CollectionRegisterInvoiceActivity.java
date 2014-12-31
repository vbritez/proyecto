package com.tigo.cs.android.activity.collection;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.api.entities.CollectionInvoiceService;
import com.tigo.cs.api.entities.CollectionPaymentService;
import com.tigo.cs.api.entities.CollectionService;

public class CollectionRegisterInvoiceActivity extends AbstractActivity {

    private CollectionService collection;
    private List<CollectionInvoiceService> invoices;
    private List<CollectionPaymentService> payments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_register_invoice_main);
        onSetContentViewFinish();

        collection = new CollectionService();
        invoices = new ArrayList<CollectionInvoiceService>();
        collection.setInvoices(invoices);
        payments = new ArrayList<CollectionPaymentService>();
        collection.setPayments(payments);

        ImageView addImage = (ImageView) findViewById(R.id.invoiceAdd);
        addImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!reachedMaxNumberInvoices() && validateDetails()) {
                    CollectionInvoiceService firstDetail = new CollectionInvoiceService();
                    collection.getInvoices().add(firstDetail);
                    firstDetail.setId(collection.getInvoices().size());
                    recreateInvoices(true);
                }
            }
        });

        Button register = (Button) findViewById(R.id.collectionRegisterInvoiceButton);

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new CollectionService();

                if (!startUserMark()) {
                    return;
                }

                String invoice = "";
                ServiceEventEntity evt = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        getServicecod(), "DETAIL_INVOICE");
                invoice = "%+";
                for (CollectionInvoiceService data : collection.getInvoices()) {
                    invoice += MessageFormat.format(evt.getMessagePattern(),
                            data.getInvoiceNumber(), data.getInvoiceAmmount());
                    invoice += "%-";

                }
                invoice = invoice.substring(0, invoice.length() - 2);

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(), "", "", invoice, "");

                ((CollectionService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((CollectionService) entity).setInvoices(invoices);
                ((CollectionService) entity).setPayments(payments);

                endUserMark(msg);
            }
        });

    }

    private boolean reachedMaxNumberInvoices() {
        if (collection.getInvoices() == null
            || collection.getInvoices().isEmpty())
            return false;

        Integer maxValue = CsTigoApplication.getGlobalParameterHelper().getCollectionMaxDetail();
        if (collection.getInvoices().size() == maxValue) {
            Toast.makeText(
                    CollectionRegisterInvoiceActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.collection_detail_reached_max_number),
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    @Override
    protected boolean validateUserInput() {
        if (collection.getInvoices() == null
            || collection.getInvoices().isEmpty()) {
            Toast.makeText(
                    CollectionRegisterInvoiceActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.collection_registration_cant_be_empty),
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
        return "REG";
    }

    private boolean validateDetails() {
        /*
         * validamos que el usuario haya ingresado todos los campos requeridos
         * de los detalles
         */
        for (CollectionInvoiceService detail : collection.getInvoices()) {

            if (TextUtils.isEmpty(detail.getInvoiceNumber())) {
                Toast.makeText(
                        CollectionRegisterInvoiceActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.collection_invoice_number_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if (TextUtils.isEmpty(detail.getInvoiceAmmount())) {
                Toast.makeText(
                        CollectionRegisterInvoiceActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.collection_invoice_amount_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;

    }

    private void recreateInvoices(boolean requestFocus) {

        LinearLayout dynamicLayout = (LinearLayout) findViewById(R.id.collectionDynamiclayout);
        dynamicLayout.removeAllViews();

        for (int i = 0; i < collection.getInvoices().size(); i++) {
            final CollectionInvoiceService detail = collection.getInvoices().get(
                    i);
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout layout = (RelativeLayout) li.inflate(
                    R.layout.collection_invoice_subcontent, null);

            detail.setId(i + 1);
            TextView id = (TextView) layout.findViewById(R.id.invoiceCount);
            id.setText(detail.getId().toString());
            dynamicLayout.addView(layout);

            if (requestFocus && i == collection.getInvoices().size() - 1) {
                EditText invoiceNumber = (EditText) layout.findViewById(R.id.invoiceNumberEdit);
                invoiceNumber.requestFocus();
            }
            ImageView deleteImage = (ImageView) layout.findViewById(R.id.invoicetRemove);
            deleteImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    collection.getInvoices().remove(detail);
                    recreateInvoices(false);
                }
            });

            EditText invoiceNumber = (EditText) layout.findViewById(R.id.invoiceNumberEdit);
            EditText invoiceAmount = (EditText) layout.findViewById(R.id.invoiceAmountEdit);

            if (detail.getInvoiceNumber() != null) {
                invoiceNumber.setText(detail.getInvoiceNumber());
            }
            if (detail.getInvoiceAmmount() != null) {
                invoiceAmount.setText(detail.getInvoiceAmmount().toString());
            }

            invoiceNumber.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setInvoiceNumber(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            invoiceAmount.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setInvoiceAmmount(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

        }
    }
}
