package com.tigo.cs.android.activity.meta;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.MetaEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.api.entities.MetadataCrudService;
import com.tigo.cs.api.entities.PermissionService;

public class MetadataReadActivity extends AbstractActivity {

    @Override
    public Integer getServicecod() {
        return 99;
    }

    @Override
    public String getServiceEventCod() {
        return "metadata.name.read";
    }

    private OnClickListener metadataQueryOnClickListener;
    private static List<MetaEntity> metaList = null;
    private Spinner metaSpinner = null;

    private RadioButton byCode = null;
    private RadioButton byName = null;
    private EditText value = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metadata_read_main);
        onSetContentViewFinish();

        /*
         * realizamos el populate de los metadatos habilitados
         */

        metaList = CsTigoApplication.getMetaHelper().findAllReadable(true);

        metaSpinner = (Spinner) findViewById(R.id.metaSelector);

        byCode = (RadioButton) findViewById(R.id.metadataReadByCode);
        byName = (RadioButton) findViewById(R.id.metadataReadByName);
        value = (EditText) findViewById(R.id.metadataReadValue);

        Button metadataUpdate = (Button) findViewById(R.id.metadataUpdateButton);
        Button metadataQuery = (Button) findViewById(R.id.metadataReadButton);

        if (metaList != null) {
            String[] metaArray = new String[metaList.size()];
            int i = 0;
            for (MetaEntity entity : metaList) {
                metaArray[i++] = CsTigoApplication.i18n(entity.getMetaname());
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, metaArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            metaSpinner.setAdapter(adapter);
        } else {
            metaSpinner.setVisibility(Spinner.INVISIBLE);
        }

        metadataQueryOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                Notifier.info(getClass(),
                        "Validando informacion antes de enviar mensaje.");

                if (!startUserMark()) {
                    return;
                }

                Notifier.info(getClass(),
                        "Se obtiene patron de mensajes y se crea el mensaje de texto");

                /*
                 * creamos el mensaje para enviar a la plataforma
                 */

                /*
                 * determinamos el discriminator para realizar la busqueda para
                 * crear el el mismo tomamos el codigo del meta y seguido del
                 * tipo de busqueda
                 */

                String byCodeOrName = byCode.isChecked() ? "C" : "N";

                String msgPattern = serviceEvent.getMessagePattern();

                Integer metaSelected = metaList.get(
                        (int) metaSpinner.getSelectedItemId()).getMetaCod().intValue();

                Locale.setDefault(Locale.US);
                String msg = MessageFormat.format(msgPattern,
                        serviceEvent.getService().getServicecod(),
                        metaSelected, serviceEvent.getServiceEventCod(),
                        value.getText(), byCodeOrName);
                /*
                 * construimos el mensaje a ser enviado a la plataforma
                 */

                /*
                 * una vez preparada la seccion de localizacion, recuperamos el
                 * mensaje de la base de datos del dispositivo
                 */

                ((MetadataCrudService) entity).setServiceCod(serviceEvent.getService().getServicecod());
                ((MetadataCrudService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((MetadataCrudService) entity).setCode(byCode.isChecked() ? "C" : "N");

                ((MetadataCrudService) entity).setMetaCode(metaSelected);
                ((MetadataCrudService) entity).setValue(value.getText().toString());

                Notifier.info(getClass(), "Se creo la entidad a ser enviada");

                endUserMark(msg);

            }
        };
        metadataQuery.setOnClickListener(metadataQueryOnClickListener);

        metadataUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                CsTigoApplication.getMetaHelper().disableAllReadableMeta();
                ServiceEventEntity eventUpdate = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        0, "CM");

                Notifier.info(getClass(),
                        "Validando informacion antes de enviar mensaje.");

                Notifier.info(getClass(),
                        "Se obtiene patron de mensajes y se crea el mensaje de texto");
                String msg = MessageFormat.format(
                        eventUpdate.getMessagePattern(),
                        eventUpdate.getService().getServicecod());

                Notifier.info(getClass(),
                        "Se crean los datos de la entidad a ser enviada.");
                entity = new PermissionService();
                entity.setServiceCod(eventUpdate.getService().getServicecod());
                entity.setEvent(eventUpdate.getServiceEventCod());
                entity.setRequiresLocation(eventUpdate.getRequiresLocation());

                Notifier.info(getClass(), "Se creo la entidad a ser enviada");

                endUserMark(msg);

            }
        });

    }

    @Override
    protected boolean startUserMark() {
        entity = new MetadataCrudService();
        entity.setRequiresLocation(serviceEvent.getRequiresLocation());
        if (metaSpinner.getSelectedItem() == null) {
            Toast.makeText(
                    this,
                    CsTigoApplication.getContext().getString(
                            R.string.metadata_select), Toast.LENGTH_LONG).show();
            return false;
        }

        /*
         * verficamos previamente que tipo de busqueda realizara el usuario
         */
        if (!(byCode.isChecked() || byName.isChecked())) {
            Toast.makeText(
                    this,
                    CsTigoApplication.getContext().getString(
                            R.string.metadata_read_select_option),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        /*
         * verificamos que el usuario haya ingresao algun valor para realizar la
         * busqueda
         */
        if (TextUtils.isEmpty(value.getText())) {
            Toast.makeText(
                    this,
                    CsTigoApplication.getContext().getString(
                            R.string.metadata_read_insert_value),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

}
