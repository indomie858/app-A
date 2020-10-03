package com.example.appa.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appa.R;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.DirectionsResponse;
import com.mapbox.api.directions.v5.models.DirectionsRoute;
import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.services.android.navigation.v5.navigation.NavigationRoute;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DirectionsActivity extends AppCompatActivity {

    // variables for calculating a route
    private DirectionsRoute currentRoute;
    private static final String TAG = "DirectionsActivity";

    //UI element for testing and displaying JSON responses
    private TextView geocodeResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, getString(R.string.mapbox_access_token));
        setContentView(R.layout.activity_directions);
        geocodeResultTextView = (TextView)findViewById(R.id.textView);
    }

    //how to create a route from origin to destination
    private void getRoute(Point origin, Point destination) {
        NavigationRoute.builder(this)
                .accessToken(Mapbox.getAccessToken())
                .origin(origin)
                .destination(destination)
                .profile(DirectionsCriteria.PROFILE_WALKING)    //selects walking navigation profiles
                .build()
                .getRoute(new Callback<DirectionsResponse>() {
                    @Override
                    public void onResponse(Call<DirectionsResponse> call, Response<DirectionsResponse> response) {
                        // You can get the generic HTTP info about the response
                        Log.d(TAG, "Response code: " + response.code());
                        if (response.body() == null) {
                            Log.e(TAG, "No routes found, make sure you set the right user and access token.");
                            return;
                        } else if (response.body().routes().size() < 1) {
                            Log.e(TAG, "No routes found");
                            return;
                        }
                        //currentRoute contains JSON response with route details
                        currentRoute = response.body().routes().get(0);
                    }

                    @Override
                    public void onFailure(Call<DirectionsResponse> call, Throwable throwable) {
                        Log.e(TAG, "Error: " + throwable.getMessage());
                    }
                });
    }

    //Converts location address string into geographic coordinates
    private void geocodeForwardSearch(String address) {
        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(address)
                .build();

        mapboxGeocoding.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();
                if (results.size() > 0) {
                    //feature contains full JSON response including address
                    CarmenFeature feature = results.get(0);

                    //firstResultPoint contains smaller JSON response
                    Point firstResultPoint = results.get(0).center();
                    geocodeResultTextView.setText(firstResultPoint.toString()); //UI element...commment this out later
                    // Log the first results Point.
                    Log.d(TAG, "onResponse: " + firstResultPoint.toString());

                } else {
                    // No result for your request were found.
                    Log.d(TAG, "onResponse: No result found");
                }
            }
            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    //Converts geographic coordinates into location address string
    private void geocodeReverseSearch(double longitude, double latitude) {
        MapboxGeocoding reverseGeocode = MapboxGeocoding.builder()
                .accessToken(getString(R.string.mapbox_access_token))
                .query(Point.fromLngLat(longitude, latitude))
                .geocodingTypes(GeocodingCriteria.TYPE_ADDRESS)
                .build();

        reverseGeocode.enqueueCall(new Callback<GeocodingResponse>() {
            @Override
            public void onResponse(Call<GeocodingResponse> call, Response<GeocodingResponse> response) {
                List<CarmenFeature> results = response.body().features();
                if (results.size() > 0) {
                    // Get the first Feature from the successful geocoding response
                    CarmenFeature feature = results.get(0);
                    geocodeResultTextView.setText(feature.toString()); //UI element...commment this out later
                    Log.d(TAG, "onResponse: " + feature.toString());
                } else {
                    // No result for your request were found.
                    Log.d(TAG, "onResponse: No result found");
                }
            }
            @Override
            public void onFailure(Call<GeocodingResponse> call, Throwable throwable) {
                throwable.printStackTrace();
            }
        });
    }

    public void geoForwardButtonClick(View v)
    {
        String testAddress = "18111 Nordhoff St CA";
        geocodeForwardSearch(testAddress);
    }

    public void geoReverseButtonClick(View v)
    {
        double testLongitude = -118.527642;
        double testLatitude = 34.241099;
        geocodeReverseSearch(testLongitude, testLatitude);
    }
}