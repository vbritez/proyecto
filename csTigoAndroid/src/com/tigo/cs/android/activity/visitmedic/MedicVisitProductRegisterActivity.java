package com.tigo.cs.android.activity.visitmedic;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
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
import com.tigo.cs.api.entities.OrderDetailService;
import com.tigo.cs.api.entities.OrderService;
import com.tigo.cs.api.entities.VisitMedicService;

public class MedicVisitProductRegisterActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 17;
    }

    @Override
    public String getServiceEventCod() {
        return "PR";
    }

    private OrderService order;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.medic_visit_product_register_main);
        onSetContentViewFinish();

        order = new OrderService();
        final List<OrderDetailService> details = new ArrayList<OrderDetailService>();
        order.setDetail(details);
        OrderDetailService firstDetail = new OrderDetailService();
        details.add(firstDetail);
        firstDetail.setId(details.size());

        recreateDetails(false);

        ImageView addImage = (ImageView) findViewById(R.id.productAdd);
        addImage.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                if (validateUserInput() && validateDetails()) {

                    OrderDetailService firstDetail = new OrderDetailService();
                    order.getDetail().add(firstDetail);
                    firstDetail.setId(order.getDetail().size());

                    recreateDetails(true);
                }
            }
        });

        Button register = (Button) findViewById(R.id.orderRegisterButton);
        register.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                entity = new VisitMedicService();

                if (!startUserMark()) {
                    return;
                }

                ServiceEventEntity detailServiceEvent = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        getServicecod(), "DETAIL");

                String detailPattern = detailServiceEvent.getMessagePattern();

                String detailPart = "";
                for (OrderDetailService detail : order.getDetail()) {
                    detailPart = detailPart.concat(MessageFormat.format(
                            detailPattern, detail.getProductCode(),
                            detail.getQuantity()));
                }

                String msg = MessageFormat.format(
                        serviceEvent.getMessagePattern(),
                        service.getServicecod(),
                        serviceEvent.getServiceEventCod(), detailPart);

                ((VisitMedicService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((VisitMedicService) entity).setDetail(order.getDetail());
                entity.setRequiresLocation(false);
                endUserMark(msg);
            }
        });

    }

    private void recreateDetails(boolean requestFocus) {
        LinearLayout productLayout = (LinearLayout) findViewById(R.id.orderProductlayout);
        productLayout.removeAllViews();

        for (int i = 0; i < order.getDetail().size(); i++) {
            final OrderDetailService detail = order.getDetail().get(i);
            LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout layout = (RelativeLayout) li.inflate(
                    R.layout.medic_visit_product_register_subcontent, null);

            detail.setId(i + 1);
            TextView id = (TextView) layout.findViewById(R.id.orderProductCount);
            id.setText(detail.getId().toString());
            productLayout.addView(layout);

            if (requestFocus && i == order.getDetail().size() - 1) {
                EditText productCode = (EditText) layout.findViewById(R.id.terportRegisterContainerEdit);
                productCode.requestFocus();
            }
            ImageView deleteImage = (ImageView) layout.findViewById(R.id.productRemove);
            deleteImage.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    order.getDetail().remove(detail);
                    recreateDetails(false);
                }
            });

            EditText productCode = (EditText) layout.findViewById(R.id.terportRegisterContainerEdit);
            EditText quantity = (EditText) layout.findViewById(R.id.terportRegisterUbicationEdit);

            if (CsTigoApplication.getGlobalParameterHelper().getOrderPricelpha()) {
                quantity.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
            }

            if (detail.getProductCode() != null) {
                productCode.setText(detail.getProductCode());
            }
            if (detail.getQuantity() != null) {
                quantity.setText(detail.getQuantity().toString());
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

            quantity.addTextChangedListener(new TextWatcher() {

                @Override
                public void afterTextChanged(Editable s) {
                    detail.setQuantity(s.toString());
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
    protected boolean validateUserInput() {

        if (!validateDetails()) {
            return false;
        }

        return true;
    }

    private boolean validateDetails() {

        /*
         * validamos que el usuario haya ingresado todos los campos requeridos
         * de los detalles
         */

        if (order.getDetail().size() == 0) {
            Toast.makeText(
                    MedicVisitProductRegisterActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.order_details_not_empty),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        for (OrderDetailService detail : order.getDetail()) {

            if (TextUtils.isEmpty(detail.getProductCode())) {
                Toast.makeText(
                        MedicVisitProductRegisterActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.order_product_code_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }
            if (TextUtils.isEmpty(detail.getQuantity() != null ? detail.getQuantity().toString() : "")) {
                Toast.makeText(
                        MedicVisitProductRegisterActivity.this,
                        CsTigoApplication.getContext().getString(
                                R.string.order_quantity_not_empty),
                        Toast.LENGTH_LONG).show();
                return false;
            }

        }
        return true;

    }

}
