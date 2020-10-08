package com.example.appa.ui.navigation;

import android.util.Log;

import com.mapbox.api.geocoding.v5.GeocodingCriteria;
import com.mapbox.api.geocoding.v5.MapboxGeocoding;
import com.mapbox.api.geocoding.v5.models.CarmenFeature;
import com.mapbox.api.geocoding.v5.models.GeocodingResponse;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Geocoder {

    private static final String TAG = "Geocoder";

    //Converts location address string into geographic coordinates
    private void geocodeForwardSearch(String address) {
        MapboxGeocoding mapboxGeocoding = MapboxGeocoding.builder()
                .accessToken(Mapbox.getAccessToken())
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
                    //geocodeResultTextView.setText(firstResultPoint.toString()); //UI element...commment this out later
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
                .accessToken(Mapbox.getAccessToken())
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
                    //geocodeResultTextView.setText(feature.placeName()); //UI element...commment this out later/////////////////
                    Log.d(TAG, "onResponse: " + feature.toString());
                    Log.d(TAG, "onResponse: " + feature.placeName());
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

}
