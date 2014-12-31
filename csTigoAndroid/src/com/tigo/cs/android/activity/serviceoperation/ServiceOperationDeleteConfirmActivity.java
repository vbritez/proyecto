package com.tigo.cs.android.activity.serviceoperation;

import java.text.MessageFormat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.ServiceEntity;
import com.tigo.cs.android.helper.domain.ServiceOperationDataEntity;
import com.tigo.cs.android.helper.domain.ServiceOperationEntity;
import com.tigo.cs.api.entities.ServiceOperationService;

public class ServiceOperationDeleteConfirmActivity extends AbstractActivity {

    private ServiceEntity serviceSelected = null;
    private ServiceOperationDataEntity serviceOperationDataEntity = null;

    @Override
    public Integer getServicecod() {
        return 101;
    }

    @Override
    public String getServiceEventCod() {
        return "serviceoperation.name.DeleteService";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_content);
        onSetContentViewFinish();

        Bundle extras = getIntent().getExtras();
        Long id = (Long) extras.get("id");

        serviceOperationDataEntity = CsTigoApplication.getServiceOperationDataHelper().find(
                id);

        ServiceOperationEntity serviceOperationEntity = CsTigoApplication.getServiceOperationHelper().find(
                serviceOperationDataEntity.getCodServiceOperation());

        serviceSelected = CsTigoApplication.getServiceHelper().findByServiceCod(
                serviceOperationEntity.getServicecod());

        TextView message = (TextView) findViewById(R.id.message);
        message.setText(serviceOperationDataEntity.getServicemsg());

        ImageView delete = (ImageView) findViewById(R.id.deleteImage);
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Integer pos = serviceOperationDataEntity.getPos();

                String msgPattern = serviceEvent.getMessagePattern();

                String msg = MessageFormat.format(msgPattern,
                        serviceEvent.getService().getServicecod(),
                        serviceEvent.getServiceEventCod(),
                        serviceSelected.getServicecod(), pos);

                entity = new ServiceOperationService();
                entity.setRequiresLocation(serviceEvent.getRequiresLocation());
                entity.setServiceCod(serviceEvent.getService().getServicecod());
                entity.setEvent(serviceEvent.getServiceEventCod());
                ((ServiceOperationService) entity).setServiceToOperate(serviceSelected.getServicecod().longValue());
                ((ServiceOperationService) entity).setPosition(pos);

                endUserMark(msg);
            }
        });

    }

}
