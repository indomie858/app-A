package com.example.appa.ui.tutorial;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.example.appa.R;

public class TutorialPagerAdapter extends FragmentPagerAdapter {

    @StringRes
    private static final int[] TAB_TITLES = new int[]{R.string.app_manual, R.string.cane_setup, R.string.tutorialStep1,R.string.tutorialSteo2, R.string.tutorialStep3, R.string.tutorialStep4 };
    private final Context mContext;

    // Manages the views in the pager adapter
    // Updates the views when tabs are scrolled
    public TutorialPagerAdapter(Context context, FragmentManager fm) {
        super(fm);
        mContext = context;

    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Fragment fragment = null;
        return TutorialTextFragment.newInstance(position);

    }
    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Total of 2 tabs.
        return TAB_TITLES.length;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
