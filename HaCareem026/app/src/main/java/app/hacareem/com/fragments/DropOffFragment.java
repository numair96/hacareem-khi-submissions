package app.hacareem.com.fragments;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.List;

import app.hacareem.com.R;
import app.hacareem.com.adapters.DestinationSuggestionAdapter;
import app.hacareem.com.bean.Places;
import app.hacareem.com.bean.WebResponse;
import app.hacareem.com.bll.Urls;
import app.hacareem.com.bll.WebAPI;
import app.hacareem.com.utils.Common;
import app.hacareem.com.utils.GPSTracker;
import app.hacareem.com.utils.RecyclerItemClickListener;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by Numair Qadir on 4/30/2017.
 */

public class DropOffFragment extends android.support.v4.app.Fragment implements GoogleApiClient.ConnectionCallbacks {

    // A request to connect to Location Services
    GoogleMap mGoogleMap;
    View mapView;
    PlaceAutocompleteFragment autocompleteFragment;
    MapFragment mapFragment;
    AutocompleteFilter typeFilter;

    // Views
    @BindView(R.id.btnNext)
    Button btnNext;
    @BindView(R.id.imgSuggestLocation)
    ImageView imgView;
    AlertDialog.Builder alertDialogBuilder;
    AlertDialog alertDialog;

    // Stores the current instantiation of the location client in this object
    double latitude;
    double longitude;
    private GPSTracker gpsTracker;
    private LatLng currentPoint;

    // Lists and Adapters
    List<Places> placesList;
    DestinationSuggestionAdapter historyAdapter;
    RecyclerView recyclerViewSuggestions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_drop_off_location, container, false);

        // Init Dependency injection
        ButterKnife.bind(this, view);

        autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment_drop_off);

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
            }
        });

        // Setup Map and its configuration
        setupMap();

        return view;
    }

    /**
     * Setting up Google Map
     */
    private void setupMap() {
        try {
            mapFragment = (MapFragment) getActivity().getFragmentManager().findFragmentById(R.id.map_drop_off);
            mapView = mapFragment.getView();

            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    mGoogleMap = googleMap;

                    // Google Map UI Settigns
                    mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
                    mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
                    mGoogleMap.getUiSettings().setCompassEnabled(true);
                    mGoogleMap.getUiSettings().setRotateGesturesEnabled(true);
                    mGoogleMap.getUiSettings().setZoomGesturesEnabled(true);

                    // GPS Tracker
                    gpsTracker = new GPSTracker(getActivity());

                    // If GPS location is fetched
                    if (gpsTracker.canGetLocation()) {
                        latitude = gpsTracker.getLatitude();
                        longitude = gpsTracker.getLongitude();

                        //Set & animate to Location
                        setCurrentLocation(latitude, longitude);

                        // Adjust current location button
                        adjustCurrentLocationButton();
                    } else {
                        gpsTracker.showSettingsAlert();
                    }

                    // Populating suggestions list
                    populateHistory();
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

    @OnClick(R.id.imgSuggestLocation)
    public void suggestionBox() {
        if (placesList != null && placesList.size() > 0) {
            buildDialog();
        }
    }

    /**
     * Populate suggestions dialog
     */
    private void buildDialog() {
        alertDialogBuilder = new AlertDialog.Builder(getActivity());
        final LayoutInflater eulaInflater = LayoutInflater.from(getActivity());
        View view = eulaInflater.inflate(R.layout.dialog_suggested_location, null);

        recyclerViewSuggestions = (RecyclerView) view.findViewById(R.id.lvSuggestLocation);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerViewSuggestions.setLayoutManager(mLayoutManager);
        recyclerViewSuggestions.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSuggestions.setAdapter(historyAdapter);

        recyclerViewSuggestions.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), recyclerViewSuggestions, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                alertDialog.dismiss();

                int itemPosition = recyclerViewSuggestions.getChildLayoutPosition(view);
                Places item = placesList.get(itemPosition);

                // Set location bu latitude and longitude
                setCurrentLocation(item.getLatitude(), item.getLongitude());
            }
        }));

        Button btnConfirm = (Button) view.findViewById(R.id.btnConfirmSuggestedLocation);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialogBuilder.setView(view);
        alertDialog = alertDialogBuilder.create();
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        alertDialog.getWindow().setLayout(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        alertDialog.getWindow().setGravity(Gravity.CENTER);
        alertDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        alertDialog.show();
    }

    /**
     * Populate User's location suggestions list
     */
    public void populateHistory() {
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient httpClient = new OkHttpClient();
        httpClient.networkInterceptors().add(logging);

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.enableComplexMapKeySerialization().setPrettyPrinting().create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Urls.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(httpClient)
                .build();

        WebAPI service = retrofit.create(WebAPI.class);

        Call<WebResponse> call = service.getDestinationList(Common.USER_ID, Common.getSystemCurrentDateTime());
        call.enqueue(new Callback<WebResponse>() {
            @Override
            public void onResponse(Response<WebResponse> response, Retrofit retrofit) {

                Log.e("body: ", "" + response.body());
                Log.e("body: ", "" + response.body().getPlaces());

                placesList = response.body().getPlaces();

                historyAdapter = new DestinationSuggestionAdapter(getActivity(), placesList);
                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Throwable t) {
            }
        });
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