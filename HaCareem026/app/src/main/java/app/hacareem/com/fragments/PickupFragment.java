package app.hacareem.com.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import app.hacareem.com.R;
import app.hacareem.com.utils.GPSTracker;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Numair Qadir on 4/30/2017.
 */

public class PickupFragment extends android.support.v4.app.Fragment implements GoogleApiClient.ConnectionCallbacks {

    // A request to connect to Location Services
    MapFragment mapFragment;
    GoogleMap mGoogleMap;
    View mapView;
    PlaceAutocompleteFragment autocompleteFragment;
    AutocompleteFilter typeFilter;

    // Views
    @BindView(R.id.btnNext)
    Button btnNext;

    // Stores the current instantiation of the location client in this object
    double latitude;
    double longitude;
    private GPSTracker gpsTracker;
    private LatLng currentPoint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_pickup_location, container, false);

        // Init Dependency injection
        ButterKnife.bind(this, view);

        autocompleteFragment = (PlaceAutocompleteFragment) getActivity().
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        typeFilter = new AutocompleteFilter.Builder()
                .setTypeFilter(Place.TYPE_COUNTRY).setCountry("PK")
                .build();

        autocompleteFragment.setFilter(typeFilter);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                LatLng latLng = place.getLatLng();

                latitude = latLng.latitude;
                longitude = latLng.longitude;

                //Set & animate to Location
                setCurrentLocation(latitude, longitude);
            }

            @Override
            public void onError(Status status) {
                Log.i("", "An error occurred: " + status);
            }
        });

        setupMap();

        return view;
    }

    private void setupMap() {
        try {
            mapFragment = (MapFragment) getActivity().getFragmentManager()
                    .findFragmentById(R.id.map);
            mapView = mapFragment.getView();

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;
                    // Enabling MyLocation in Google Map
                    mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mGoogleMap.getUiSettings().setCompassEnabled(true);
                    mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
                    mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

                    gpsTracker = new GPSTracker(getActivity());

                    // If GPS location is fetched
                    if (gpsTracker.canGetLocation()) {
                        latitude = gpsTracker.getLatitude();
                        longitude = gpsTracker.getLongitude();
                        currentPoint = new LatLng(latitude, longitude);

                        //Set & animate to Location
                        setCurrentLocation(latitude, longitude);

                        // Adjust current location button
                        adjustCurrentLocationButton();
                    } else {
                        gpsTracker.showSettingsAlert();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle arg0) {
        setupMap();
    }

    @Override
    public void onConnectionSuspended(int arg0) {
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapFragment != null) {
            mapFragment = null;
        }
        if (autocompleteFragment != null) {
            autocompleteFragment = null;
        }
    }

    @OnClick(R.id.btnNext)
    public void setPickUp() {
//        Intent intent = new Intent(PickUpActivity.this, DropOffActivity.class);
//        startActivity(intent);
//        viewPager.setCurrentItem(1);
//        ((DashboardFragment) getFragmentManager()).viewPager.setCurrentItem(1);
//        DashboardFragment favoritesFragment = (DashboardFragment) getFragmentManager().getFragments().set(1);
    }

    /**
     * This method is used to adjust the Current location icon
     */
    private void adjustCurrentLocationButton() {
        if (mGoogleMap != null &&
                mapView.findViewById(Integer.parseInt("1")) != null) {
            // Get the button view
            View locationButton = ((View) mapView.findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // and next place it, on bottom right (as Google Maps app)
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)
                    locationButton.getLayoutParams();
            // position on right bottom
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
            layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
            layoutParams.setMargins(0, 0, 30, 30);
        }
    }

    /**
     * Pass latitude and longitude to set and animate on Google Map
     *
     * @param latitude
     * @param longitude
     */
    private void setCurrentLocation(double latitude, double longitude) {
        currentPoint = new LatLng(latitude, longitude);
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(currentPoint).zoom(19f).build();

        mGoogleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        // Clears all the existing markers
        mGoogleMap.clear();

        // Set user's current location marker
        mGoogleMap.addMarker(new MarkerOptions().position(currentPoint).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
    }
}