package com.tigo.cs.android.activity.messagehistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.MessageEntity;

public class MessageHistoryActivity extends AbstractActivity {

    private void reCreateViews() {
        onSetContentViewFinish();

        ListView listView = (ListView) findViewById(R.id.messages);

        final List<MessageEntity> messageList = CsTigoApplication.getMessageHelper().findAll(
                "message is not null", null, true);

        listView.setAdapter(new MesageAdapter(messageList));

        /*
         * habilitamos el componente para realizar la busqueda de mensajes
         * dentro de la pantalla
         */
        listView.setTextFilterEnabled(true);

        /*
         * agregamos un listener para poder visualizar los mensajes de la
         * plataforma
         */
        listView.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /*
                 * abrimos la nueva actividad para visualizar el mensaje
                 * seleccionado
                 */

                Intent messageViewerIntent = new Intent(MessageHistoryActivity.this, MessageViewerActivity.class);
                messageViewerIntent.putExtra("message_id", id);
                startActivity(messageViewerIntent);

            }
        });

        ImageView delete = (ImageView) findViewById(R.id.deleteImage);
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                CsTigoApplication.vibrate(false);
                CsTigoApplication.getMessageHelper().deleteAll();
                finish();
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.history_main);
        reCreateViews();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setContentView(R.layout.history_main);
        reCreateViews();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setContentView(R.layout.history_main);
        reCreateViews();
    }

    public class MesageAdapter extends BaseAdapter {

        private final List<MessageEntity> messageList;
        private SimpleDateFormat simpleDateFormat;

        public MesageAdapter(List<MessageEntity> messageList) {

            this.messageList = new ArrayList<MessageEntity>();
            this.messageList.add(new MessageEntity());
            this.messageList.addAll(messageList);
        }

        @Override
        public int getCount() {
            if (messageList != null && messageList.size() > 0) {
                return messageList.size();
            }
            return 0;
        }

        @Override
        public synchronized Object getItem(int position) {
            if (messageList != null && messageList.size() > 0) {
                return messageList.get(position);
            }
            return null;
        }

        @Override
        public long getItemId(int position) {
            try {
                if (messageList != null && messageList.size() > 0) {
                    if (messageList.get(position) == null) {
                        return 0L;
                    }
                    return messageList.get(position).getId();
                }
            } catch (Exception ex) {
                return 0L;
            }
            return 0L;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (simpleDateFormat == null) {
                simpleDateFormat = new SimpleDateFormat(CsTigoApplication.getGlobalParameterHelper().getDateTimePattern());
            }
            if (convertView == null) {
                View layout = null;
                final MessageEntity messageEntity = messageList.get(position);
                if (messageEntity.getService() != null) {
                    LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                            Context.LAYOUT_INFLATER_SERVICE);
                    layout = li.inflate(R.layout.history_detail, null);

                    TextView mensaje = (TextView) layout.findViewById(R.id.mensaje);

                    TextView servicio = (TextView) layout.findViewById(R.id.service);
                    TextView hora = (TextView) layout.findViewById(R.id.hora);

                    Integer charLimit = CsTigoApplication.getGlobalParameterHelper().getSmsShowCharLimit();

                    if (!CsTigoApplication.getGlobalParameterHelper().getSmsShowAllHistory()) {
                        if (messageEntity.getMessage().length() >= charLimit) {
                            mensaje.setText(messageEntity.getMessage().substring(
                                    0, charLimit).concat(" ... "));
                        } else {
                            mensaje.setText(messageEntity.getMessage());
                        }
                    } else {
                        mensaje.setText(messageEntity.getMessage());
                    }

                    servicio.setText(CsTigoApplication.i18n(messageEntity.getService().getServicename()));

                    hora.setText(messageEntity.getEventDate() != null ? simpleDateFormat.format(messageEntity.getEventDate()) : null);

                } else {
                    TextView text = new TextView(parent.getContext());
                    text.setPadding(10, 6, 10, 6);

                    if (messageList.size() == 1) {
                        text.setText(CsTigoApplication.getContext().getString(
                                R.string.sms_no_messages));
                    }

                    return text;
                }
                return layout;
            }
            return convertView;
        }
    }

}
