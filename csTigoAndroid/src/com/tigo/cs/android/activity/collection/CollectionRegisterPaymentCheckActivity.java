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

public class CollectionRegisterPaymentCheckActivity extends AbstractActivity {

    private CollectionService collection;
    private List<CollectionInvoiceService> invoices;
    private List<CollectionPaymentService> payments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.collection_register_payment_check_main);
        onSetContentViewFinish();

        collection = new CollectionService();
        invoices = new ArrayList<CollectionInvoiceService>();
        collection.setInvoices(invoices);
        payments = new ArrayList<CollectionPaymentService>();
        collection.setPayments(payments);

        ImageView addImage = (ImageView) findViewById(R.id.paymentCheckAdd);
        addImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (!reachedMaxNumberPayments() && validateDetails()) {
                    CollectionPaymentService firstDetail = new CollectionPaymentService();
                    collection.getPayments().add(firstDetail);
                    firstDetail.setId(collection.getPayments().size());
                    firstDetail.setCollectionType("2");
                    recreatePayments(true);
                }
            }
        });

        Button register = (Button) findViewById(R.id.collectionRegisterCheckButton);

        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new CollectionService();

                if (!startUserMark()) {
                    return;
                }

                String payment = "";
                ServiceEventEntity evtCash = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        getServicecod(), "DETAIL_CHECK");
                payment = "%+";
                String checkDate = "";
                String checkExpirationDate = "";

                for (CollectionPaymentService data : collection.getPayments()) {
                    checkDate = data.getCheckDate() != null ? data.getCheckDate() : "";
                    checkExpirationDate = data.getCheckExpirationDate() != null ? data.getCheckExpirationDate() : "";

                    payment += MessageFormat.format(
                            evtCash.getMessagePattern(),
                            data.getCollectionTypeAmmount(),
                            data.getBankCode(), data.getCheckNumber(),
                            checkDate, checkExpirationDate);
                    payment += "%-";
                }
                payment = payment.substring(0, payment.length() - 2);

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(), "", "", "", payment);

                ((CollectionService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((CollectionService) entity).setInvoices(invoices);
                ((CollectionService) entity).setPayments(payments);

                endUserMark(msg);
            }
        });

    }

    private boolean reachedMaxNumberPayments() {
        if (collection.getPayments() == null
            || collection.getPayments().isEmpty())
            return false;

        Integer maxValue = CsTigoApplication.getGlobalParameterHelper().getCollectionMaxDetail();
        if (collection.getPayments().size() == maxValue) {
            Toast.makeText(
                    CollectionRegisterPaymentCheckActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.collection_detail_reached_max_number),
                    Toast.LENGTH_LONG).show();
            return true;
        }
        return false;
    }

    @Override
    protected boolean validateUserInput() {
        if (collection.getPayments() == null
            || collection.getPayments().isEmpty()) {
            Toast.makeText(
                    CollectionRegisterPaymentCheckActivity.this,
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

        for (CollectionPaymentService detail : collection.getPayments()) {
            if (TextUtils.isEmpty(detail.getCollectionTypeAmmount())) {
                Toast.makeText(
                        CollectionRegisterPaymentCheckActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.collection_payment_amount_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;

    }

    private void recreatePayments(boolean requestFocus) {
        LinearLayout dynamicLayout = (LinearLayout) findViewById(R.id.collectionCheckDynamiclayout);
        dynamicLayout.removeAllViews();

        for (int i = 0; i < collection.getPayments().size(); i++) {
            final CollectionPaymentService detail = collection.getPayments().get(
                    i);
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout layout = (RelativeLayout) li.inflate(
                    R.layout.collection_payment_check_subcontent, null);

            detail.setId(i + 1);
            TextView id = (TextView) layout.findViewById(R.id.paymentCheckCount);
            id.setText(detail.getId().toString());
            dynamicLayout.addView(layout);

            if (requestFocus && i == collection.getPayments().size() - 1) {
                EditText paymentAmount = (EditText) layout.findViewById(R.id.paymentCheckAmountEdit);
                paymentAmount.requestFocus();
            }
            ImageView deleteImage = (ImageView) layout.findViewById(R.id.paymentCheckRemove);
            deleteImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    collection.getPayments().remove(detail);
                    recreatePayments(false);
                }
            });

            EditText paymentAmount = (EditText) layout.findViewById(R.id.paymentCheckAmountEdit);
            EditText bankEdit = (EditText) layout.findViewById(R.id.bankEdit);
            EditText checkNumber = (EditText) layout.findViewById(R.id.checkNumberEdit);
            EditText checkDate = (EditText) layout.findViewById(R.id.checkDateEdit);
            EditText checkExpirationDate = (EditText) layout.findViewById(R.id.checkExpirationDateEdit);

            if (detail.getCollectionTypeAmmount() != null) {
                paymentAmount.setText(detail.getCollectionTypeAmmount());
            }

            if (detail.getBankCode() != null) {
                bankEdit.setText(detail.getBankCode());
            }

            if (detail.getCheckNumber() != null) {
                checkNumber.setText(detail.getCheckNumber());
            }

            if (detail.getCheckDate() != null) {
                checkDate.setText(detail.getCheckDate());
            }

            if (detail.getCheckExpirationDate() != null) {
                checkExpirationDate.setText(detail.getCheckExpirationDate());
            }

            paymentAmount.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setCollectionTypeAmmount(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            bankEdit.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setBankCode(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            checkNumber.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setCheckNumber(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            checkDate.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setCheckDate(s.toString());
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                }
            });

            checkExpirationDate.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setCheckExpirationDate(s.toString());
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
