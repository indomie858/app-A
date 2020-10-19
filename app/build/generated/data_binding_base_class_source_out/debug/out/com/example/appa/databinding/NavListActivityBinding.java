// Generated by view binder compiler. Do not edit!
package com.example.appa.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;
import com.example.appa.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.MaterialToolbar;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class NavListActivityBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final AppBarLayout appBarLayout;

  @NonNull
  public final LinearLayout linearLayout2;

  @NonNull
  public final Spinner locationCatSpinner;

  @NonNull
  public final RecyclerView placeList;

  @NonNull
  public final SearchView placeSearch;

  @NonNull
  public final MaterialToolbar topAppBar;

  private NavListActivityBinding(@NonNull ConstraintLayout rootView,
      @NonNull AppBarLayout appBarLayout, @NonNull LinearLayout linearLayout2,
      @NonNull Spinner locationCatSpinner, @NonNull RecyclerView placeList,
      @NonNull SearchView placeSearch, @NonNull MaterialToolbar topAppBar) {
    this.rootView = rootView;
    this.appBarLayout = appBarLayout;
    this.linearLayout2 = linearLayout2;
    this.locationCatSpinner = locationCatSpinner;
    this.placeList = placeList;
    this.placeSearch = placeSearch;
    this.topAppBar = topAppBar;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static NavListActivityBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static NavListActivityBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.nav_list_activity, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static NavListActivityBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.appBarLayout;
      AppBarLayout appBarLayout = rootView.findViewById(id);
      if (appBarLayout == null) {
        break missingId;
      }

      id = R.id.linearLayout2;
      LinearLayout linearLayout2 = rootView.findViewById(id);
      if (linearLayout2 == null) {
        break missingId;
      }

      id = R.id.location_cat_spinner;
      Spinner locationCatSpinner = rootView.findViewById(id);
      if (locationCatSpinner == null) {
        break missingId;
      }

      id = R.id.place_list;
      RecyclerView placeList = rootView.findViewById(id);
      if (placeList == null) {
        break missingId;
      }

      id = R.id.place_search;
      SearchView placeSearch = rootView.findViewById(id);
      if (placeSearch == null) {
        break missingId;
      }

      id = R.id.topAppBar;
      MaterialToolbar topAppBar = rootView.findViewById(id);
      if (topAppBar == null) {
        break missingId;
      }

      return new NavListActivityBinding((ConstraintLayout) rootView, appBarLayout, linearLayout2,
          locationCatSpinner, placeList, placeSearch, topAppBar);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
