// Generated by view binder compiler. Do not edit!
package com.example.ads.helper.new_.demo.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.ads.helper.new_.demo.R;
import com.example.app.ads.helper.banner.BannerAdView;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class ActivityBannerTypeBinding implements ViewBinding {
  @NonNull
  private final ConstraintLayout rootView;

  @NonNull
  public final BannerAdView flAdaptiveBanner;

  @NonNull
  public final BannerAdView flBanner;

  @NonNull
  public final BannerAdView flCbBanner;

  @NonNull
  public final BannerAdView flCtBanner;

  @NonNull
  public final BannerAdView flFullBanner;

  @NonNull
  public final BannerAdView flLargeBanner;

  @NonNull
  public final BannerAdView flLeaderBoard;

  @NonNull
  public final BannerAdView flMediumBanner;

  @NonNull
  public final AllScreenHeaderBinding layoutHeader;

  @NonNull
  public final TextView txtAdaptiveBanner;

  @NonNull
  public final TextView txtBanner;

  @NonNull
  public final TextView txtCbBanner;

  @NonNull
  public final TextView txtCtBanner;

  @NonNull
  public final TextView txtFullBanner;

  @NonNull
  public final TextView txtLargeBanner;

  @NonNull
  public final TextView txtLeaderBoard;

  @NonNull
  public final TextView txtMediumBanner;

  @NonNull
  public final TextView txtSizeHint;

  private ActivityBannerTypeBinding(@NonNull ConstraintLayout rootView,
      @NonNull BannerAdView flAdaptiveBanner, @NonNull BannerAdView flBanner,
      @NonNull BannerAdView flCbBanner, @NonNull BannerAdView flCtBanner,
      @NonNull BannerAdView flFullBanner, @NonNull BannerAdView flLargeBanner,
      @NonNull BannerAdView flLeaderBoard, @NonNull BannerAdView flMediumBanner,
      @NonNull AllScreenHeaderBinding layoutHeader, @NonNull TextView txtAdaptiveBanner,
      @NonNull TextView txtBanner, @NonNull TextView txtCbBanner, @NonNull TextView txtCtBanner,
      @NonNull TextView txtFullBanner, @NonNull TextView txtLargeBanner,
      @NonNull TextView txtLeaderBoard, @NonNull TextView txtMediumBanner,
      @NonNull TextView txtSizeHint) {
    this.rootView = rootView;
    this.flAdaptiveBanner = flAdaptiveBanner;
    this.flBanner = flBanner;
    this.flCbBanner = flCbBanner;
    this.flCtBanner = flCtBanner;
    this.flFullBanner = flFullBanner;
    this.flLargeBanner = flLargeBanner;
    this.flLeaderBoard = flLeaderBoard;
    this.flMediumBanner = flMediumBanner;
    this.layoutHeader = layoutHeader;
    this.txtAdaptiveBanner = txtAdaptiveBanner;
    this.txtBanner = txtBanner;
    this.txtCbBanner = txtCbBanner;
    this.txtCtBanner = txtCtBanner;
    this.txtFullBanner = txtFullBanner;
    this.txtLargeBanner = txtLargeBanner;
    this.txtLeaderBoard = txtLeaderBoard;
    this.txtMediumBanner = txtMediumBanner;
    this.txtSizeHint = txtSizeHint;
  }

  @Override
  @NonNull
  public ConstraintLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static ActivityBannerTypeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static ActivityBannerTypeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.activity_banner_type, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static ActivityBannerTypeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.fl_adaptive_banner;
      BannerAdView flAdaptiveBanner = ViewBindings.findChildViewById(rootView, id);
      if (flAdaptiveBanner == null) {
        break missingId;
      }

      id = R.id.fl_banner;
      BannerAdView flBanner = ViewBindings.findChildViewById(rootView, id);
      if (flBanner == null) {
        break missingId;
      }

      id = R.id.fl_cb_banner;
      BannerAdView flCbBanner = ViewBindings.findChildViewById(rootView, id);
      if (flCbBanner == null) {
        break missingId;
      }

      id = R.id.fl_ct_banner;
      BannerAdView flCtBanner = ViewBindings.findChildViewById(rootView, id);
      if (flCtBanner == null) {
        break missingId;
      }

      id = R.id.fl_full_banner;
      BannerAdView flFullBanner = ViewBindings.findChildViewById(rootView, id);
      if (flFullBanner == null) {
        break missingId;
      }

      id = R.id.fl_large_banner;
      BannerAdView flLargeBanner = ViewBindings.findChildViewById(rootView, id);
      if (flLargeBanner == null) {
        break missingId;
      }

      id = R.id.fl_leader_board;
      BannerAdView flLeaderBoard = ViewBindings.findChildViewById(rootView, id);
      if (flLeaderBoard == null) {
        break missingId;
      }

      id = R.id.fl_medium_banner;
      BannerAdView flMediumBanner = ViewBindings.findChildViewById(rootView, id);
      if (flMediumBanner == null) {
        break missingId;
      }

      id = R.id.layout_header;
      View layoutHeader = ViewBindings.findChildViewById(rootView, id);
      if (layoutHeader == null) {
        break missingId;
      }
      AllScreenHeaderBinding binding_layoutHeader = AllScreenHeaderBinding.bind(layoutHeader);

      id = R.id.txt_adaptive_banner;
      TextView txtAdaptiveBanner = ViewBindings.findChildViewById(rootView, id);
      if (txtAdaptiveBanner == null) {
        break missingId;
      }

      id = R.id.txt_banner;
      TextView txtBanner = ViewBindings.findChildViewById(rootView, id);
      if (txtBanner == null) {
        break missingId;
      }

      id = R.id.txt_cb_banner;
      TextView txtCbBanner = ViewBindings.findChildViewById(rootView, id);
      if (txtCbBanner == null) {
        break missingId;
      }

      id = R.id.txt_ct_banner;
      TextView txtCtBanner = ViewBindings.findChildViewById(rootView, id);
      if (txtCtBanner == null) {
        break missingId;
      }

      id = R.id.txt_full_banner;
      TextView txtFullBanner = ViewBindings.findChildViewById(rootView, id);
      if (txtFullBanner == null) {
        break missingId;
      }

      id = R.id.txt_large_banner;
      TextView txtLargeBanner = ViewBindings.findChildViewById(rootView, id);
      if (txtLargeBanner == null) {
        break missingId;
      }

      id = R.id.txt_leader_board;
      TextView txtLeaderBoard = ViewBindings.findChildViewById(rootView, id);
      if (txtLeaderBoard == null) {
        break missingId;
      }

      id = R.id.txt_medium_banner;
      TextView txtMediumBanner = ViewBindings.findChildViewById(rootView, id);
      if (txtMediumBanner == null) {
        break missingId;
      }

      id = R.id.txt_size_hint;
      TextView txtSizeHint = ViewBindings.findChildViewById(rootView, id);
      if (txtSizeHint == null) {
        break missingId;
      }

      return new ActivityBannerTypeBinding((ConstraintLayout) rootView, flAdaptiveBanner, flBanner,
          flCbBanner, flCtBanner, flFullBanner, flLargeBanner, flLeaderBoard, flMediumBanner,
          binding_layoutHeader, txtAdaptiveBanner, txtBanner, txtCbBanner, txtCtBanner,
          txtFullBanner, txtLargeBanner, txtLeaderBoard, txtMediumBanner, txtSizeHint);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}