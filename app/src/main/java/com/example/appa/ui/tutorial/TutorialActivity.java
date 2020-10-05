package com.example.appa.ui.tutorial;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.view.View;

import com.example.appa.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;

public class TutorialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);

        // The adapter will get attached to a viewpager
        TutorialPagerAdapter adapter = new TutorialPagerAdapter(this, getSupportFragmentManager());
        ViewPager viewPager = findViewById(R.id.view_pager);
        viewPager.setAdapter(adapter);
        TabLayout tabs = findViewById(R.id.tutorial_tabs);
        tabs.setupWithViewPager(viewPager);



        MaterialToolbar actionbar = (MaterialToolbar) findViewById(R.id.topAppBar);
        if (null != actionbar) {
            actionbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
            actionbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    NavUtils.navigateUpFromSameTask(TutorialActivity.this);
                }
            });
        }
    }
}