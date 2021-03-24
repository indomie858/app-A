package com.example.appa.ui.navigationlist;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.telephony.PhoneNumberUtils;
import android.view.View;
import android.view.accessibility.AccessibilityManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.appa.R;
import com.example.appa.db.PlaceEntity;
import com.example.appa.ui.mapbox.DirectionsActivity;
import com.example.appa.viewmodel.MapWithNavViewModel;

import java.util.Locale;

public class DirectoryInformationActivity extends AppCompatActivity {

    //viewmodel and place entity members
    private MapWithNavViewModel viewModel = null;
    private PlaceEntity currentPlace = null;
    private int currentPlaceID;

    //ui members
    private TextView nameText;
    private TextView descriptionText;
    private Button launchNavigationButton;
    private Button phoneButton;

    //accessibility members
    private TextToSpeech mTTSObject;
    private AccessibilityManager am;

    //handler thread is used to delay query from placeentity
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_directory_information);

        nameText = findViewById(R.id.nameTextView);
        descriptionText = findViewById(R.id.descriptionTextView);

        viewModel = new ViewModelProvider(this).get(MapWithNavViewModel.class);

        setPlaceFromIntent();

        launchNavigationButton = (Button)findViewById(R.id.launchNavigationButton);
        launchNavigationButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                launchNavigation();
            }
        });

        phoneButton = (Button)findViewById(R.id.phone_btn);
        phoneButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                callPhone();
            }
        });

        //delay pulling data from PlaceEntity object
        handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                populateViewsWithPlaceData();
            }
        }, 1000);

        // Set a member variable using viewgroup context
        mTTSObject = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                mTTSObject.setLanguage(Locale.US);
            }
        });

        am = (AccessibilityManager) this.getSystemService(ACCESSIBILITY_SERVICE);
    }

    private void launchNavigation() {
        if (currentPlace != null) {
            Intent intent = new Intent(DirectoryInformationActivity.this, DirectionsActivity.class);
            intent.putExtra("NewPlace", currentPlaceID);
            DirectoryInformationActivity.this.startActivity(intent);
        }
    }

    private void callPhone(){
        if (currentPlace != null) {
            phoneButton.setContentDescription("Call " + currentPlace.getName());
            String placePhoneNumber = currentPlace.getPhone_number();
            String formattedNumber = PhoneNumberUtils.formatNumber(placePhoneNumber);
            Intent callIntent = new Intent(Intent.ACTION_DIAL);
            callIntent.setData(Uri.parse("tel:" + formattedNumber));
            Context context = phoneButton.getContext();
            context.startActivity(callIntent);
        }
    }

    private void populateViewsWithPlaceData() {
        if (currentPlace != null) {
            nameText.setText(currentPlace.getName());
            descriptionText.setText(currentPlace.getDescription());
            if (am.isEnabled()) { // Read back the text if accessibility is enabled
                    mTTSObject.speak(currentPlace.getDescription(), TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    private void setPlaceFromIntent() {
        Intent intent = getIntent();
        currentPlaceID = intent.getIntExtra("NewPlace", 1);
        Observer<PlaceEntity> placeEntityObserver = new Observer<PlaceEntity>() {
            @Override
            public void onChanged(PlaceEntity placeEntity) {
                currentPlace = placeEntity;
            }
        };
        viewModel.getPlaceFromID(currentPlaceID).observeForever(placeEntityObserver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentPlace != null) {
            if (am.isEnabled()) { // Read back the text if accessibility is enabled
                mTTSObject.speak(currentPlace.getDescription(), TextToSpeech.QUEUE_FLUSH, null);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTTSObject.stop();
        handler.removeCallbacksAndMessages(null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mTTSObject.stop();
    }

}