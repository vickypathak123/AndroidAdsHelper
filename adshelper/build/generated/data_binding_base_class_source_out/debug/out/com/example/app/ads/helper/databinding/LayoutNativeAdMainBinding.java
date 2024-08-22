// Generated by view binder compiler. Do not edit!
package com.example.app.ads.helper.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.app.ads.helper.R;
import com.google.android.material.card.MaterialCardView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class LayoutNativeAdMainBinding implements ViewBinding {
  @NonNull
  private final MaterialCardView rootView;

  @NonNull
  public final MaterialCardView cvAdContainer;

  @NonNull
  public final FrameLayout heightInfoContainer;

  @NonNull
  public final FrameLayout nativeAdContainer;

  private LayoutNativeAdMainBinding(@NonNull MaterialCardView rootView,
      @NonNull MaterialCardView cvAdContainer, @NonNull FrameLayout heightInfoContainer,
      @NonNull FrameLayout nativeAdContainer) {
    this.rootView = rootView;
    this.cvAdContainer = cvAdContainer;
    this.heightInfoContainer = heightInfoContainer;
    this.nativeAdContainer = nativeAdContainer;
  }

  @Override
  @NonNull
  public MaterialCardView getRoot() {
    return rootView;
  }

  @NonNull
  public static LayoutNativeAdMainBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static LayoutNativeAdMainBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.layout_native_ad_main, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static LayoutNativeAdMainBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      MaterialCardView cvAdContainer = (MaterialCardView) rootView;

      id = R.id.height_info_container;
      FrameLayout heightInfoContainer = ViewBindings.findChildViewById(rootView, id);
      if (heightInfoContainer == null) {
        break missingId;
      }

      id = R.id.native_ad_container;
      FrameLayout nativeAdContainer = ViewBindings.findChildViewById(rootView, id);
      if (nativeAdContainer == null) {
        break missingId;
      }

      return new LayoutNativeAdMainBinding((MaterialCardView) rootView, cvAdContainer,
          heightInfoContainer, nativeAdContainer);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}