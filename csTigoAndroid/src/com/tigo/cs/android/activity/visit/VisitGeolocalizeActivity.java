package com.tigo.cs.android.activity.visit;

import java.io.Serializable;
import java.util.List;

import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tigo.cs.android.CsTigoApplication;
import com.tigo.cs.android.R;
import com.tigo.cs.android.activity.AbstractFragmentActivity;
import com.tigo.cs.api.entities.MetadataCrudService;

public class VisitGeolocalizeActivity extends AbstractFragmentActivity implements LocationListener {

    // Google Map
    private static GoogleMap googleMap;

    @Override
    public Integer getServicecod() {
        return 1;
    }

    private Bundle extras;
    private List<MetadataCrudService> list = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visit_geolocalize_main);
        onSetContentViewFinish();
        /*
         * Recuperamos los extras del intent, en caso de ser distinto a nulo
         * recuperamos el extra con nombre metadata_crud_service
         */
        extras = getIntent().getExtras();
        if (extras != null) {
            Serializable serializable = extras.getSerializable("metadata_crud_service");
            list = ((MetadataCrudService) serializable).getResponseList();
        }
        initilizeMap(list);
    }

    private void initilizeMap(final List<MetadataCrudService> list) {
        LinearLayout dynamicLayout = (LinearLayout) findViewById(R.id.mapSubcontentlayout);
        dynamicLayout.removeAllViews();

        RelativeLayout layout = (RelativeLayout) getLayoutInflater().inflate(
                R.layout.map_subcontent, null);
        dynamicLayout.addView(layout);

        android.support.v4.app.FragmentManager fragmentManager = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager.findFragmentById(R.id.map);
        googleMap = mapFragment.getMap();
        if (list != null) {
            // Enabling MyLocation Layer of Google Map
            googleMap.setMyLocationEnabled(true);
            int i = 0;
            for (MetadataCrudService md : list) {
                if (md != null && md.getLatitude() != null
                    && md.getLongitude() != null) {
                    LatLng myLocation = new LatLng(md.getLatitude(), md.getLongitude());
                    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                            myLocation, 13));
                    // create marker
                    MarkerOptions marker = new MarkerOptions().position(
                            new LatLng(myLocation.latitude, myLocation.longitude)).title(
                            "Cod: "
                                + md.getCode().concat(" | Descripción: ").concat(
                                        md.getValue())).snippet("" + i++);
                    // adding marker
                    googleMap.addMarker(marker);

                }
            }

            /*
             * Cuando se toca la ventana de informacion se pasa el codigo del
             * meta como extra a la pagina VisitNormal para que sea cargado en
             * el campo Codigo del Activity VisitStartActivity y
             * VisitQuickActivity
             */
            googleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    MetadataCrudService md = list.get(Integer.parseInt(marker.getSnippet()));
                    Intent visitViewerIntent = new Intent(VisitGeolocalizeActivity.this, VisitNormalActivity.class);
                    visitViewerIntent.putExtra("client_cod", md.getCode());
                    startActivity(visitViewerIntent);
                }
            });

            /*
             * Creamos el contenido de la ventana de informacion de manera
             * dinamica a partir de un layout
             */
            googleMap.setInfoWindowAdapter(new InfoWindowAdapter() {

                // Use default InfoWindow frame
                @Override
                public View getInfoWindow(Marker marker) {
                    return null;
                }

                // Defines the contents of the InfoWindow
                @Override
                public View getInfoContents(Marker marker) {
                    // Getting view from the layout file info_window_layout
                    View v = getLayoutInflater().inflate(
                            R.layout.marker_window_info_content, null);

                    // // Getting reference to the TextView to set latitude
                    TextView description = (TextView) v.findViewById(R.id.metadataLabel);
                    //
                    // // Getting reference to the TextView to set longitude
                    TextView staticDescription = (TextView) v.findViewById(R.id.staticMetadataDescription);
                    //
                    // // Setting the latitude
                    description.setText(marker.getTitle());

                    staticDescription.setText(CsTigoApplication.getContext().getString(
                            R.string.geolocalize_message_info_windows));
                    ;
                    // Returning the view containing InfoWindow contents
                    return v;

                }
            });

            googleMap.setOnMarkerClickListener(new OnMarkerClickListener() {

                @Override
                public boolean onMarkerClick(Marker marker) {
                    marker.showInfoWindow();
                    return true;
                }

            });
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

            Toast.makeText(
                    VisitGeolocalizeActivity.this,
                    CsTigoApplication.getContext().getString(
                            R.string.metadatacrud_geolocation_message_error),
                    Toast.LENGTH_LONG).show();
        }

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
