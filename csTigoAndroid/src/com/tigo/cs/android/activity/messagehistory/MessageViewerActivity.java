package com.tigo.cs.android.activity.messagehistory;

import java.text.SimpleDateFormat;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.MessageEntity;

public class MessageViewerActivity extends AbstractActivity {
    private SimpleDateFormat simpleDateFormat;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (simpleDateFormat == null) {
            simpleDateFormat = new SimpleDateFormat(CsTigoApplication.getGlobalParameterHelper().getDateTimePattern());
        }
        setContentView(R.layout.history_content);
        onSetContentViewFinish();

        Bundle extras = getIntent().getExtras();
        Long id = (Long) extras.get("message_id");

        final MessageEntity messageEntity = CsTigoApplication.getMessageHelper().find(
                id);

        TextView hora = (TextView) findViewById(R.id.dateTime);
        hora.setText(messageEntity.getEventDate() != null ? simpleDateFormat.format(messageEntity.getEventDate()) : null);

        TextView serviceName = (TextView) findViewById(R.id.serviceName);
        serviceName.setText(CsTigoApplication.i18n(messageEntity.getService().getServicename()));

        TextView message = (TextView) findViewById(R.id.message);
        message.setText(messageEntity.getMessage());

        ImageView delete = (ImageView) findViewById(R.id.deleteImage);
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CsTigoApplication.vibrate(false);
                CsTigoApplication.getMessageHelper().delete(messageEntity);
                finish();
            }
        });

    }

}
