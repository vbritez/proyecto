package com.tigo.cs.android.activity.meta;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractActivity;
import com.tigo.cs.android.helper.domain.IconEntity;
import com.tigo.cs.android.helper.domain.MetaDataEntity;
import com.tigo.cs.android.helper.domain.MetaEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.util.CSTigoNotificationID;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.api.entities.MetadataCrudService;
import com.tigo.cs.api.entities.PermissionService;

public class MetadataCreateActivity extends AbstractActivity {

    private CheckBox autogenerate = null;
    private CheckBox getGpsPointCheck = null;
    private EditText value = null;
    private EditText code = null;
    private Spinner iconSpinner = null;
    private List<IconEntity> iconList = null;

    Map<Integer, Drawable> mapImage;

    @Override
    public Integer getServicecod() {
        return 99;
    }

    @Override
    public String getServiceEventCod() {
        return "metadata.name.create";
    }

    private OnClickListener metadataCreateOnClickListener;
    private MetaEntity metaSelected;
    private IconEntity iconSelected;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.metadata_create_main);
        onSetContentViewFinish();

        mapImage = new HashMap<Integer, Drawable>();

        iconList = CsTigoApplication.getIconHelper().findAll();
        if (iconList == null || iconList.isEmpty()) {
            iconList = new ArrayList<IconEntity>();
            IconEntity icon = new IconEntity();
            icon.setCode("null");
            icon.setDescription("No tiene iconos cargados");
            icon.setId(0L);
            icon.setImage(null);
            iconList.add(icon);
        }

        iconSpinner = (Spinner) findViewById(R.id.spinner);
        iconSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (iconList != null && !iconList.isEmpty()) {
                    iconSelected = iconList.get(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                iconSelected = null;

            }
        });

        iconSpinner.setEnabled(false);
        if (iconList != null && !iconList.isEmpty()) {
            iconSpinner.setAdapter(new MyCustomAdapter(MetadataCreateActivity.this, R.layout.row, iconList));
        }

        List<MetaEntity> metaList = CsTigoApplication.getMetaHelper().findAllCreatable(
                true);

        Spinner metaSpinner = (Spinner) findViewById(R.id.metaSelector);
        Button metadataUpdate = (Button) findViewById(R.id.metadataUpdateButton);
        code = (EditText) findViewById(R.id.metadataCreateCode);
        autogenerate = (CheckBox) findViewById(R.id.generateCode);
        getGpsPointCheck = (CheckBox) findViewById(R.id.metadataCreateGetGps);
        value = (EditText) findViewById(R.id.metadataCreateValue);
        Button metadataCreate = (Button) findViewById(R.id.metadataCreateButton);

        getGpsPointCheck.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    iconSpinner.setEnabled(true);
                } else {
                    iconSpinner.setEnabled(false);
                }

            }
        });

        /*
         * realizamos el populate de los metadatos habilitados
         */

        if (metaList != null) {
            String[] metaArray = new String[metaList.size()];
            int i = 0;
            for (MetaEntity entity : metaList) {
                metaArray[i++] = CsTigoApplication.i18n(entity.getMetaname());
            }

            MetaAdapter adapter = new MetaAdapter(this, android.R.layout.simple_spinner_item, metaList);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            metaSpinner.setAdapter(adapter);
            metaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                @Override
                public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                    metaSelected = CsTigoApplication.getMetaHelper().find(id);

                }

                @Override
                public void onNothingSelected(AdapterView<?> parentView) {
                    metaSelected = null;

                }
            });
        } else {
            metaSpinner.setVisibility(Spinner.INVISIBLE);
        }

        autogenerate.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    code.setEnabled(false);
                } else {
                    code.setEnabled(true);
                }

            }
        });

        metadataCreateOnClickListener = new OnClickListener() {
            @Override
            public void onClick(View v) {

                entity = new MetadataCrudService();

                if (!startUserMark()) {
                    return;
                }

                /*
                 * creamos el mensaje para enviar a la plataforma
                 */

                /*
                 * determinamos el discriminator para realizar la busqueda para
                 * crear el el mismo tomamos el codigo del meta y seguido del
                 * tipo de busqueda
                 */

                String msgPattern = serviceEvent.getMessagePattern();

                String msg = MessageFormat.format(
                        msgPattern,
                        service.getServicecod(),
                        metaSelected.getMetaCod(),
                        serviceEvent.getServiceEventCod(),
                        value.getText(),
                        !autogenerate.isChecked() ? code.getText().toString() : null,
                        iconSpinner.isEnabled() && iconSelected != null
                            && iconSelected.getImage() != null ? iconSelected.getCode() : null);
                /*
                 * construimos el mensaje a ser enviado a la plataforma
                 */

                /*
                 * una vez preparada la seccion de localizacion, recuperamos el
                 * mensaje de la base de datos del dispositivo
                 */

                ((MetadataCrudService) entity).setEvent(serviceEvent.getServiceEventCod());
                ((MetadataCrudService) entity).setMetaCode(metaSelected.getMetaCod().intValue());
                ((MetadataCrudService) entity).setCode(!autogenerate.isChecked() ? code.getText().toString() : null);
                ((MetadataCrudService) entity).setValue(value.getText().toString());
                ((MetadataCrudService) entity).setIconCodeSelected(iconSpinner.isEnabled()
                    && iconSelected != null && iconSelected.getImage() != null ? iconSelected.getCode() : null);
                ((MetadataCrudService) entity).setRequiresLocation(getGpsPointCheck.isChecked());
                serviceEvent.setForceGps(getGpsPointCheck.isChecked());

                Notifier.info(getClass(), "Se creo la entidad a ser enviada");

                endUserMark(msg);
            }
        };
        metadataCreate.setOnClickListener(metadataCreateOnClickListener);
        metadataUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                CsTigoApplication.getMetaHelper().disableAllCreatableMeta();
                CsTigoApplication.getMetaHelper().disableAllReadableMeta();
                ServiceEventEntity eventUpdate = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        0, "CR");

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
        entity.setRequiresLocation(serviceEvent.getRequiresLocation());

        if (metaSelected == null) {
            Toast.makeText(
                    this,
                    CsTigoApplication.getContext().getString(
                            R.string.metadata_select), Toast.LENGTH_LONG).show();
            return false;
        }

        /*
         * verficamos previamente que tipo de busqueda realizara el usuario
         */
        if (!autogenerate.isChecked()) {
            if (TextUtils.isEmpty(code.getText())) {
                Toast.makeText(
                        this,
                        CsTigoApplication.getContext().getString(
                                R.string.metadata_create_insert_code),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        if (getGpsPointCheck.isChecked()) {
            if (!locationManager.isProviderEnabled((LocationManager.GPS_PROVIDER))) {

                CsTigoApplication.showNotification(
                        CSTigoNotificationID.LOCATION,
                        CsTigoApplication.getContext().getString(
                                R.string.no_location_title),
                        CsTigoApplication.getContext().getString(
                                R.string.no_location_notif),
                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);

                return false;

            }
        }

        /*
         * verificamos que el usuario haya ingresao algun valor para realizar la
         * busqueda
         */
        if (TextUtils.isEmpty(value.getText())) {
            Toast.makeText(
                    this,
                    CsTigoApplication.getContext().getString(
                            R.string.metadata_create_insert_value),
                    Toast.LENGTH_LONG).show();
            return false;
        }

        return true;
    }

    public class MetaAdapter extends ArrayAdapter<MetaEntity> {

        public MetaAdapter(MetadataCreateActivity context,
                int textViewResourceId, List<MetaEntity> metaList) {
            super(context, textViewResourceId, metaList);
            this.metaList = metaList;
        }

        private final List<MetaEntity> metaList;

        @Override
        public final int getCount() {
            return metaList.size();
        }

        @Override
        public final MetaEntity getItem(int position) {

            return metaList.get(position);
        }

        @Override
        public final long getItemId(int position) {

            return metaList.get(position).getId();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                View layout = null;
                final MetaEntity metaEntity = metaList.get(position);

                LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                layout = li.inflate(android.R.layout.simple_spinner_item, null);

                TextView metaName = (TextView) layout.findViewById(android.R.id.text1);

                metaName.setText(CsTigoApplication.i18n(metaEntity.getMetaname()));

                return layout;
            }
            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                View layout = null;
                final MetaEntity metaEntity = metaList.get(position);

                LayoutInflater li = (LayoutInflater) getApplicationContext().getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                layout = li.inflate(
                        android.R.layout.simple_spinner_dropdown_item, null);

                TextView metaName = (TextView) layout.findViewById(android.R.id.text1);

                metaName.setText(CsTigoApplication.i18n(metaEntity.getMetaname()));

                return layout;
            }
            return convertView;
        }

    }

    class MyCustomAdapter extends ArrayAdapter<IconEntity> {

        private final List<IconEntity> iconList;

        public MyCustomAdapter(Context context, int textViewResourceId,
                List<IconEntity> objects) {
            super(context, textViewResourceId, objects);
            this.iconList = objects;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (!mapImage.containsKey(position)
                || mapImage.get(position) == null)
                return getCustomView(position, convertView, parent);
            else
                return getCustomView(mapImage.get(position), position,
                        convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (!mapImage.containsKey(position))
                return getCustomView(position, convertView, parent);
            else
                return getCustomView(mapImage.get(position), position,
                        convertView, parent);
        }

        public View getCustomView(Drawable draw, int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);

            IconEntity iconUrl = iconList.get(position);
            MetaDataEntity metadatDescription = null;

            ImageView icon = (ImageView) row.findViewById(R.id.icon);

            if (iconUrl != null) {
                TextView label = (TextView) row.findViewById(R.id.weekofday);
                label.setText(metadatDescription != null ? metadatDescription.getDataValue() : iconUrl.getDescription());

                if (draw != null) {
                    icon.setImageDrawable(draw);
                }
            }
            return row;
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater = getLayoutInflater();
            View row = inflater.inflate(R.layout.row, parent, false);

            IconEntity iconUrl = iconList.get(position);

            if (iconUrl != null) {

                TextView label = (TextView) row.findViewById(R.id.weekofday);
                label.setText(iconUrl.getDescription());

                ImageView icon = (ImageView) row.findViewById(R.id.icon);
                Drawable draw = null;
                try {
                    draw = new BitmapDrawable(BitmapFactory.decodeByteArray(
                            iconUrl.getImage(), 0, iconUrl.getImage().length));
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (draw != null) {
                    mapImage.put(position, draw);
                    icon.setImageDrawable(draw);
                }

            }
            return row;
        }
    }

}
