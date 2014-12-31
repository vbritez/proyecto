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
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.api.entities.CollectionPaymentService;
import com.tigo.cs.api.entities.CollectionService;

public class AverageCollectionActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 5;
    }

    @Override
    public String getServiceEventCod() {
        return "AVERAGE_COLLECTION";
    }

    private EditText avCollectionClientCode;
    private EditText avCollectionReceiptNum;
    private Button registerButton;
    private RadioButton averageCollectionCash;
    private RadioButton averageCollectionCheck;
    private CollectionService collection;
    private List<CollectionPaymentService> payments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.average_collection_main);
        onSetContentViewFinish();

        collection = new CollectionService();
        payments = new ArrayList<CollectionPaymentService>();
        collection.setPayments(payments);

        avCollectionClientCode = (EditText) findViewById(R.id.averageCollectionClientCode);
        avCollectionReceiptNum = (EditText) findViewById(R.id.averageCollectionReceiptNum);
        averageCollectionCash = (RadioButton) findViewById(R.id.averageCollectionCash);
        averageCollectionCheck = (RadioButton) findViewById(R.id.averageCollectionCheck);

        registerButton = (Button) findViewById(R.id.averageCollectionButton);
        registerButton.setOnClickListener(new View.OnClickListener() {
            String detailPattern = "";

            @Override
            public void onClick(View v) {

                entity = new CollectionService();

                if (!startUserMark()) {
                    return;
                }

                if (validateUserInput()) {
                    /* Si esta seleccionado Efectivo */
                    if (averageCollectionCash.isChecked()) {

                        /*
                         * Recuperamos el pattern del evento
                         * AVERAGE_COLLECTION_CASH
                         */
                        ServiceEventEntity evtCash = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                                getServicecod(), "AVERAGE_COLLECTION_CASH");
                        detailPattern = evtCash.getMessagePattern();

                        /* Por ahora siempre el tamaño sera 1 */
                        for (CollectionPaymentService data : collection.getPayments()) {
                            detailPattern = MessageFormat.format(
                                    detailPattern,
                                    data.getCollectionTypeAmmount(),
                                    data.getObservation() == null ? "" : data.getObservation());
                        }
                    } else {
                        ServiceEventEntity evtCash = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                                getServicecod(), "AVERAGE_COLLECTION_CHECK");
                        detailPattern = evtCash.getMessagePattern();

                        for (CollectionPaymentService data : collection.getPayments()) {
                            detailPattern = MessageFormat.format(
                                    detailPattern,
                                    data.getCollectionTypeAmmount(),
                                    data.getBankCode() == null ? "" : data.getBankCode(),
                                    data.getCheckNumber(),
                                    data.getObservation() == null ? "" : data.getObservation());
                        }
                    }
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(),
                        avCollectionClientCode.getText(),
                        avCollectionReceiptNum.getText(), detailPattern);

                ((CollectionService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((CollectionService) entity).setClientCode(avCollectionClientCode.getText().toString());
                ((CollectionService) entity).setReceiptNumber(avCollectionReceiptNum.getText().toString());
                ((CollectionService) entity).setPayments(collection.getPayments());

                endUserMark(msg);

            }
        });
    }

    public void onRadioAverageClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch (view.getId()) {
        case R.id.averageCollectionCash:
            if (checked) {
                collection.setPayments(new ArrayList<CollectionPaymentService>());
                CollectionPaymentService collectionPaymentService = new CollectionPaymentService();
                collectionPaymentService.setCollectionType("1");
                collection.getPayments().add(collectionPaymentService);
                recreateCash(true);
            }
            break;
        case R.id.averageCollectionCheck:
            if (checked) {
                collection.setPayments(new ArrayList<CollectionPaymentService>());
                CollectionPaymentService collectionPaymentService = new CollectionPaymentService();
                collectionPaymentService.setCollectionType("2");
                collection.getPayments().add(collectionPaymentService);
                recreateCheque(true);
            }
            break;
        }
    }

    private boolean validateDetails() {

        if (averageCollectionCash.isChecked()) {
            /*
             * validamos que el usuario haya ingresado todos los campos
             * requeridos de los detalles
             */
            for (CollectionPaymentService detail : collection.getPayments()) {

                if (TextUtils.isEmpty(detail.getCollectionTypeAmmount())) {
                    Toast.makeText(
                            AverageCollectionActivity.this,
                            CsTigoApplication.getContext().getString(
                                    R.string.collection_payment_amount_not_empty),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        if (averageCollectionCheck.isChecked()) {
            for (CollectionPaymentService detail : collection.getPayments()) {

                if (TextUtils.isEmpty(detail.getCollectionTypeAmmount())) {
                    Toast.makeText(
                            AverageCollectionActivity.this,
                            CsTigoApplication.getContext().getString(
                                    R.string.collection_payment_amount_not_empty),
                            Toast.LENGTH_LONG).show();
                    return false;
                }

                if (TextUtils.isEmpty(detail.getCheckNumber())) {
                    Toast.makeText(
                            AverageCollectionActivity.this,
                            CsTigoApplication.getContext().getString(
                                    R.string.collection_payment_cheque_num_not_empty),
                            Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }

        return true;

    }

    @Override
    protected boolean validateUserInput() {

        /*
         * validamos que el usuario haya completado los campos de la cabecera
         */
        if (TextUtils.isEmpty(avCollectionClientCode.getText())) {
            Toast.makeText(
                    AverageCollectionActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.collection_client_code_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (TextUtils.isEmpty(avCollectionReceiptNum.getText())) {
            Toast.makeText(
                    AverageCollectionActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.collection_receipt_number_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        if (!averageCollectionCash.isChecked()
            && !averageCollectionCheck.isChecked()) {
            Toast.makeText(
                    AverageCollectionActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.collection_cash_cheque_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return validateDetails();
    }

    private void recreateCheque(boolean requestFocus) {
        LinearLayout dynamicLayout = (LinearLayout) findViewById(R.id.collectionSubcontentlayout);
        dynamicLayout.removeAllViews();

        for (int i = 0; i < collection.getPayments().size(); i++) {
            final CollectionPaymentService detail = collection.getPayments().get(
                    i);
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout layout = (RelativeLayout) li.inflate(
                    R.layout.collection_cheque_subcontent, null);

            detail.setId(i + 1);
            dynamicLayout.addView(layout);
            EditText paymentAmount = (EditText) layout.findViewById(R.id.collectionChequeAmountEdit);

            if (requestFocus && i == collection.getPayments().size() - 1) {
                paymentAmount.requestFocus();
            }

            EditText chequeNum = (EditText) layout.findViewById(R.id.collectionChequeNumEdit);
            EditText bankCode = (EditText) layout.findViewById(R.id.collectionChequeBankCodEdit);
            EditText obs = (EditText) layout.findViewById(R.id.collectionChequeObsEdit);

            if (detail.getCollectionTypeAmmount() != null) {
                paymentAmount.setText(detail.getCollectionTypeAmmount());
            }

            if (detail.getBankCode() != null) {
                bankCode.setText(detail.getBankCode());
            }

            if (detail.getCheckNumber() != null) {
                chequeNum.setText(detail.getCheckNumber());
            }

            if (detail.getObservation() != null) {
                obs.setText(detail.getObservation());
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

            bankCode.addTextChangedListener(new TextWatcher() {

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

            chequeNum.addTextChangedListener(new TextWatcher() {

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

            obs.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setObservation(s.toString());
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

    private void recreateCash(boolean requestFocus) {
        LinearLayout dynamicLayout = (LinearLayout) findViewById(R.id.collectionSubcontentlayout);
        dynamicLayout.removeAllViews();

        for (int i = 0; i < collection.getPayments().size(); i++) {
            final CollectionPaymentService detail = collection.getPayments().get(
                    i);
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout layout = (RelativeLayout) li.inflate(
                    R.layout.collection_cash_subcontent, null);

            detail.setId(i + 1);
            dynamicLayout.addView(layout);
            EditText paymentAmount = (EditText) layout.findViewById(R.id.collectionCashAmountEdit);

            if (requestFocus && i == collection.getPayments().size() - 1) {
                paymentAmount.requestFocus();
            }

            EditText obs = (EditText) layout.findViewById(R.id.collectionCashObsEdit);

            if (detail.getCollectionTypeAmmount() != null) {
                paymentAmount.setText(detail.getCollectionTypeAmmount());
            }

            if (detail.getObservation() != null) {
                obs.setText(detail.getObservation());
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

            obs.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setObservation(s.toString());
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
