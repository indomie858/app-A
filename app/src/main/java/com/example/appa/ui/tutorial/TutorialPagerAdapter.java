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
    private static final int[] TAB_TITLES = new int[]{R.string.app_manual, R.string.cane_setup};
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
        switch(position) {
            case 0:
                return new TutorialTextFragment();
            case 1:

                return new TutorialTextFragment();
            default:
                return null;
        }

    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mContext.getResources().getString(TAB_TITLES[position]);
    }

    @Override
    public int getCount() {
        // Total of 3 tabs.
        return 2;
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
