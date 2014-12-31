package com.tigo.cs.android.activity.courrier;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
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

import com.google.zxing.IntentIntegrator;
import com.google.zxing.IntentResult;
import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.api.entities.CourrierService;
import com.tigo.cs.api.entities.OrderDetailService;

public class CourierDeliveredActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 18;
    }

    @Override
    public String getServiceEventCod() {
        return "PR";
    }

    private EditText receiptName;
    private EditText observation;
    private Button registerButton;

    private static CourrierService courrier = new CourrierService();

    static {
        List<OrderDetailService> details = new ArrayList<OrderDetailService>();
        courrier.setDetail(details);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.courier_delivered_main);
        onSetContentViewFinish();

        /*
         * obtenemos el los componentes de la pantalla para validar sus valores
         */
        receiptName = (EditText) findViewById(R.id.recipientName);
        observation = (EditText) findViewById(R.id.observation);
        registerButton = (Button) findViewById(R.id.registerButton);

        recreateDetails(false);

        ImageView addImage = (ImageView) findViewById(R.id.itemAdd);
        addImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (validateUserInput() && validateDetails(false)) {

                    IntentIntegrator integrator = new IntentIntegrator(CourierDeliveredActivity.this);
                    integrator.initiateScan();
                }
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new CourrierService();

                if (!startUserMark()) {
                    return;
                }

                if (!validateDetails(true)) {
                    return;
                }

                ServiceEventEntity detailServiceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        getServicecod(), "DETAIL");

                String detailPattern = detailServiceEvent.getMessagePattern();

                String detailPart = "";
                for (OrderDetailService detail : courrier.getDetail()) {
                    detailPart = detailPart.concat(MessageFormat.format(
                            detailPattern, detail.getProductCode(),
                            detail.getQuantity()));
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(), receiptName.getText(),
                        observation.getText(), "", detailPart);

                ((CourrierService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((CourrierService) entity).setReceiverName(receiptName.getText().toString());
                if (!TextUtils.isEmpty(observation.getText())) {
                    ((CourrierService) entity).setObservation(observation.getText().toString());
                }
                ((CourrierService) entity).setDetail(courrier.getDetail());

                endUserMark(msg);
                courrier = new CourrierService();

                List<OrderDetailService> details = new ArrayList<OrderDetailService>();
                courrier.setDetail(details);
            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(
                requestCode, resultCode, intent);
        if (scanResult != null) {
            String scannedCode = scanResult.getContents();

            OrderDetailService currentDetail = new OrderDetailService();
            courrier.getDetail().add(currentDetail);
            currentDetail.setId(courrier.getDetail().size());
            currentDetail.setProductCode(scannedCode);

        }
        recreateDetails(true);
    }

    @Override
    protected boolean validateUserInput() {
        if (TextUtils.isEmpty(receiptName.getText())) {
            Toast.makeText(
                    CourierDeliveredActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.courier_receipt_name_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean validateDetails(boolean validateSize) {

        /*
         * validamos que el usuario haya ingresado todos los campos requeridos
         * de los detalles
         */

        if (validateSize && courrier.getDetail().size() == 0) {
            Toast.makeText(
                    CourierDeliveredActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.courier_item_not_empty), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;

    }

    private void recreateDetails(boolean requestFocus) {
        LinearLayout productLayout = (LinearLayout) findViewById(R.id.orderItemlayout);
        productLayout.removeAllViews();

        for (int i = 0; i < courrier.getDetail().size(); i++) {
            final OrderDetailService detail = courrier.getDetail().get(i);
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout layout = (RelativeLayout) li.inflate(
                    R.layout.courier_item_register_subcontent, null);

            detail.setId(i + 1);
            TextView id = (TextView) layout.findViewById(R.id.orderProductCount);
            id.setText(detail.getId().toString());
            productLayout.addView(layout);

            if (requestFocus && i == courrier.getDetail().size() - 1) {
                TextView productCode = (TextView) layout.findViewById(R.id.terportRegisterContainerEdit);
                productCode.requestFocus();
            }
            ImageView deleteImage = (ImageView) layout.findViewById(R.id.productRemove);
            deleteImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    courrier.getDetail().remove(detail);
                    recreateDetails(false);
                }
            });

            TextView productCode = (TextView) layout.findViewById(R.id.terportRegisterContainerEdit);

            if (detail.getProductCode() != null) {
                productCode.setText(detail.getProductCode());
            }

            productCode.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setProductCode(s.toString());
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        courrier = new CourrierService();
        List<OrderDetailService> details = new ArrayList<OrderDetailService>();
        courrier.setDetail(details);
    }
}
