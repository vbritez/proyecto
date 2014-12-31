package com.tigo.cs.android.activity.supervisor;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractFragmentActivity;
import com.tigo.cs.android.helper.domain.MessageEntity;
import com.tigo.cs.android.helper.domain.ServiceEventEntity;
import com.tigo.cs.android.helper.domain.UserphoneEntity;
import com.tigo.cs.android.util.GeoUtil;
import com.tigo.cs.android.util.Notifier;
import com.tigo.cs.api.entities.PermissionService;
import com.tigo.cs.api.entities.SupervisorService;

public class SupervisorActivity extends AbstractFragmentActivity implements LocationListener {

    // Google Map
    private static GoogleMap googleMap;
    private static Gson gson;
    private static HashMap<String, String> hashMapGlobalMessageTracking;
    private Integer sectorAreaBgColor = 2027119962;
    private Double sectorAreaAngle = 120d;
    private Double sectorAreaDistance = 300d;
    private Double areaRadius = 10000d;

    @Override
    public Integer getServicecod() {
        return 25;
    }

    /*
     * Creamos un nuevo service_event en la BD con este nombre y que solo puede
     * ser ejecutado mediante GPRS
     */
    @Override
    public String getServiceEventCod() {
        return "userphone.locate";
    }

    private OnClickListener trackUserphoneOnClickListener;
    private static List<UserphoneEntity> userphoneList = null;
    private Spinner userphoneSpinner = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.supervisor_main);
        onSetContentViewFinish();

        /*
         * realizamos el populate de los userphones habilitados
         */

        userphoneList = CsTigoApplication.getUserphoneHelper().findAllEnabled(
                true);

        userphoneSpinner = (Spinner) findViewById(R.id.userphoneSelector);

        Button userphoneUpdate = (Button) findViewById(R.id.userphoneUpdateButton);
        Button trackButton = (Button) findViewById(R.id.supervisorTrackButton);
        TextView messageTracking = (TextView) findViewById(R.id.supervisorTrackingMessage);

        if (userphoneList != null) {
            String[] userphoneArray = new String[userphoneList.size()];
            int i = 0;
            for (UserphoneEntity entity : userphoneList) {
                userphoneArray[i++] = entity.getName().concat(" (Cod.: ").concat(
                        entity.getUserphoneCod().toString().concat(")"));
            }

            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, userphoneArray);

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            userphoneSpinner.setAdapter(adapter);
        } else {
            userphoneSpinner.setVisibility(Spinner.INVISIBLE);

        }

        trackUserphoneOnClickListener = new OnClickListener() {
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

                String msgPattern = serviceEvent.getMessagePattern();

                Integer userphoneSelected = userphoneList.get(
                        (int) userphoneSpinner.getSelectedItemId()).getUserphoneCod().intValue();

                String msg = MessageFormat.format(msgPattern,
                        serviceEvent.getService().getServicecod(),
                        serviceEvent.getServiceEventCod(),
                        String.valueOf(userphoneSelected));
                /*
                 * construimos el mensaje a ser enviado a la plataforma
                 */

                /*
                 * una vez preparada la seccion de localizacion, recuperamos el
                 * mensaje de la base de datos del dispositivo
                 */

                entity.setServiceCod(serviceEvent.getService().getServicecod());
                entity.setEvent(serviceEvent.getServiceEventCod());

                ((SupervisorService) entity).setUserphoneCod(userphoneSelected.longValue());
                entity.setRequiresLocation(serviceEvent.getRequiresLocation());

                endUserMark(msg,
                        (GsmCellLocation) telephonyManager.getCellLocation());

            }
        };
        trackButton.setOnClickListener(trackUserphoneOnClickListener);

        userphoneUpdate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                /* Validamos que la opcion de internet este disponible */
                if (!validateInternetOn()) {
                    return;
                }

                CsTigoApplication.getUserphoneHelper().deleteAll();
                ServiceEventEntity eventUpdate = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                        0, "userphone.update");

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

                endUserMark(msg,
                        (GsmCellLocation) telephonyManager.getCellLocation());

            }
        });

        /*
         * Recuperamos los extras del intent, en caso de tener algun extra
         * recuperamos cuyo nombre sea supervisor_service
         */
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Serializable serializable = extras.getSerializable("supervisor_service");
            Long id = ((SupervisorService) serializable).getMessageId();
            final MessageEntity messageEntity = CsTigoApplication.getMessageHelper().find(
                    id);

            /*
             * Si el messageId es distinto a nulo convertimos a entity el json
             * de respuesta almacenado en la base de datos Esto fue guardado en
             * la clase SupervisorResponseAsyncTask en el metodo persistMessage
             */
            if (messageEntity != null && messageEntity.getJsonData() != null
                && messageEntity.getEntityClass() != null) {
                SupervisorService supervisor = (SupervisorService) getGson().fromJson(
                        messageEntity.getJsonData(),
                        messageEntity.getEntityClass());
                Double latitude = supervisor.getLatitudeNum();
                Double longitude = supervisor.getLongitudeNum();
                Integer azimuth = supervisor.getAzimuth();
                String messageTrack = getHashMapGlobalMessageTracking().get(
                        supervisor.getReturnMessage()) != null ? getHashMapGlobalMessageTracking().get(
                        supervisor.getReturnMessage()) : "";
                UserphoneEntity userphone = CsTigoApplication.getUserphoneHelper().findByUserphoneCod(
                        supervisor.getUserphoneCod());
                String userphoneName = userphone != null ? userphone.getName().concat(
                        "|").concat(userphone.getCellphoneNumber()) : "";
                if (userphone != null) {
                    int i = 0;
                    for (UserphoneEntity ue : userphoneList) {
                        if (ue.getUserphoneCod().equals(
                                userphone.getUserphoneCod())) {
                            userphoneSpinner.setSelection(i);
                            break;
                        }
                        i += 1;
                    }
                }

                // Obtain global parameters
                sectorAreaBgColor = Integer.valueOf(CsTigoApplication.getGlobalParameterHelper().findByParameterCode(
                        "map.cell.sector.area.bg.color").getParameterValue());
                sectorAreaAngle = Double.valueOf(CsTigoApplication.getGlobalParameterHelper().findByParameterCode(
                        "map.cell.sector.area.angle").getParameterValue());
                sectorAreaDistance = Double.valueOf(CsTigoApplication.getGlobalParameterHelper().findByParameterCode(
                        "map.cell.sector.area.distance").getParameterValue());
                areaRadius = Double.valueOf(CsTigoApplication.getGlobalParameterHelper().findByParameterCode(
                        "map.cell.omni.area.radius").getParameterValue());

                messageTracking.setText(messageTrack);

                LinearLayout dynamicLayout = (LinearLayout) findViewById(R.id.mapSubcontentlayout);
                dynamicLayout.removeAllViews();

                RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(
                        R.layout.map_subcontent, null);
                dynamicLayout.addView(layout);

                android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
                SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
                googleMap = mapFragment.getMap();

                /*
                 * Si el azimuth es distinto a nulo significa que la
                 * localizacion fue realizada por celda de lo contrario fue
                 * realizada por GPS
                 */
                if (googleMap != null) {
                    if (azimuth != null) {
                        if (supervisor.getSite() != null) {
                            if (!supervisor.getSite().toUpperCase().endsWith(
                                    "O")) {
                                getCellAreaPolygon(latitude, longitude, azimuth);
                                getCellAreaMarker(latitude, longitude, azimuth,
                                        messageTrack, userphoneName);

                            } else {
                                getOmniCellAreaPolygon(latitude, longitude);
                                getOmniCellAreaMarker(latitude, longitude,
                                        messageTrack, userphoneName);
                            }
                        }
                    } else {
                        getGPSCellAreaMarker(latitude, longitude, messageTrack,
                                userphoneName);
                    }
                }
            }
        }

    }

    private void getCellAreaPolygon(double latitude, double longitude, double azimuth) {

        List<LatLng> points = new ArrayList<LatLng>();

        // Prepare polygon
        points.add(new LatLng(latitude, longitude));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                points.get(0), 15));

        double angleFromAzimuth = sectorAreaAngle / 2d; // Degrees
        int startingAzimuth = (int) Math.floor(azimuth - angleFromAzimuth); // Degrees
        int endingAzimuth = (int) Math.ceil(azimuth + angleFromAzimuth); // Degrees
        GeoUtil.GeoPoint geoPoint;

        for (int i = startingAzimuth; i <= endingAzimuth; i++) {
            // Calculate area point
            int calculatedAzimuth = i;
            calculatedAzimuth += calculatedAzimuth < 0 ? 360 : 0;
            calculatedAzimuth -= calculatedAzimuth > 360 ? 360 : 0;
            geoPoint = GeoUtil.moveGeoPoint(latitude, longitude,
                    calculatedAzimuth, sectorAreaDistance);

            // Add area point to polygon
            points.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
        }

        googleMap.addPolygon(new PolygonOptions().addAll(points).strokeColor(
                Color.TRANSPARENT).fillColor(sectorAreaBgColor));
    }

    private void getOmniCellAreaPolygon(double latitude, double longitude) {

        // Prepare common data
        GeoUtil.GeoPoint geoPoint;

        List<LatLng> points = new ArrayList<LatLng>();

        // Prepare polygon
        points.add(new LatLng(latitude, longitude));

        // Circle area
        for (int i = 0; i < 360; i++) {
            // Calculate point
            geoPoint = GeoUtil.moveGeoPoint(latitude, longitude, i, areaRadius);

            // Add area point to polygon
            points.add(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()));
        }

        googleMap.addPolygon(new PolygonOptions().addAll(points).fillColor(
                sectorAreaBgColor));
    }

    private void getCellAreaMarker(Double latitude, Double longitude, Integer azimuth, String messageTrack, String userphoneName) {

        if (latitude != null && longitude != null && userphoneName != null) {
            // Obtain global parameters
            double sectorAreaDistance = 300d;
            // Calculate marker point
            GeoUtil.GeoPoint geoPoint = GeoUtil.moveGeoPoint(latitude,
                    longitude, azimuth, sectorAreaDistance / 2d);
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);
            LatLng myLocation = new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude());
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    myLocation, 15));
            // create marker
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title(userphoneName);
            // adding marker
            googleMap.addMarker(marker);
        } else {
            // Getting LocationManager object from System Service
            // LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }

    }

    private void getOmniCellAreaMarker(Double latitude, Double longitude, String messageTrack, String userphoneName) {
        if (latitude != null && longitude != null && userphoneName != null) {
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);
            LatLng myLocation = new LatLng(latitude, longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    myLocation, 15));
            // create marker
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title(userphoneName);
            // adding marker
            googleMap.addMarker(marker);
        } else {
            // Getting LocationManager object from System Service
            // LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
    }

    private void getGPSCellAreaMarker(Double latitude, Double longitude, String messageTrack, String userphoneName) {
        if (latitude != null && longitude != null && userphoneName != null) {
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);
            LatLng myLocation = new LatLng(latitude, longitude);
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    myLocation, 15));
            // create marker
            MarkerOptions marker = new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title(userphoneName);
            // adding marker
            googleMap.addMarker(marker);
        } else {
            // Getting LocationManager object from System Service
            // LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

            // Creating a criteria object to retrieve provider
            Criteria criteria = new Criteria();

            // Getting the name of the best provider
            String provider = locationManager.getBestProvider(criteria, true);

            // Getting Current Location
            Location location = locationManager.getLastKnownLocation(provider);

            if (location != null) {
                onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
    }

    @Override
    protected boolean startUserMark() {
        entity = new SupervisorService();
        entity.setRequiresLocation(serviceEvent.getRequiresLocation());
        if (userphoneSpinner.getSelectedItem() == null) {
            Toast.makeText(
                    this,
                    CsTigoApplication.getContext().getString(
                            R.string.supervisor_userphone_select),
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean validateInternetOn() {
        ServiceEventEntity eventUpdate = CsTigoApplication.getServiceEventHelper().findByServiceCodServiceEventCod(
                0, "userphone.update");
        if (eventUpdate.getForceInternet()) {
            if (!CsTigoApplication.getGlobalParameterHelper().getInternetEnabled()) {
                Toast.makeText(
                        this,
                        CsTigoApplication.getContext().getString(
                                R.string.permission_internet_not_enabled_for_update),
                        Toast.LENGTH_LONG).show();
                return false;
            }
        }

        return true;
    }

    public HashMap<String, String> getHashMapGlobalMessageTracking() {
        if (hashMapGlobalMessageTracking == null) {
            hashMapGlobalMessageTracking = new HashMap<String, String>();
            hashMapGlobalMessageTracking.put(
                    "tracking.status.NoStatusMessage",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_NoStatusMessage));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.OTA",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_OTA));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.LBS",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_LBS));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.AndroidCellInfoGpsUnknowState",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_AndroidCellInfoGpsUnknowState));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.AndroidCellInfoGpsOn",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_AndroidCellInfoGpsOn));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.AndroidCellInfoGpsOff",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_AndroidCellInfoGpsOff));

            hashMapGlobalMessageTracking.put(
                    "tracking.status.AndroidGeoPoint",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_AndroidGeoPoint));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.AndroidNoApp",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_AndroidNoApp));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.NoLocation",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_NoLocation));
            hashMapGlobalMessageTracking.put(
                    "tracking.status.NoLocationNoCellInfo",
                    CsTigoApplication.getContext().getString(
                            R.string.tracking_status_NoLocationNoCellInfo));
        }
        return hashMapGlobalMessageTracking;
    }

    public void setHashMapGlobalMessageTracking(HashMap<String, String> hashMapGlobalMessageTracking) {
        this.hashMapGlobalMessageTracking = hashMapGlobalMessageTracking;
    }

    public static Gson getGson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    @Override
    public void onLocationChanged(Location location) {
        // Getting latitude of the current location
        double latitude = location.getLatitude();

        // Getting longitude of the current location
        double longitude = location.getLongitude();

        // Creating a LatLng object for the current location
        LatLng latLng = new LatLng(latitude, longitude);

        // Showing the current location in Google Map
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        // Zoom in the Google Map
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));

    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

}
