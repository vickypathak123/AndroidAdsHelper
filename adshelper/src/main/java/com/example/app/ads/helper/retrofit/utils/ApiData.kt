package com.example.app.ads.helper.retrofit.utils

//  API Endpoint: https://fourceupdate.vasundharaapps.com/api/ApkVersion
//  Method: POST
//  Parameters:
//  package_name: The package name of your Android application.
//  version_code: The version code of your Android application.

internal const val FORCE_UPDATE_BASE_URL = "https://fourceupdate.vasundharaapps.com/api/"
internal const val FORCE_UPDATE_API = "ApkVersion"
internal const val FORCE_UPDATE_PARAM_PACKAGE_NAME = "package_name"
internal const val FORCE_UPDATE_PARAM_VERSION_CODE = "version_code"


//  API Endpoint: https://appreview.vasundharaapps.com/api/app_review_android
//  Method: POST
//  Parameters:
//  package_name: The package name of your Android application.
//  version_code: The version code of your Android application.
//  language_key: The language code of your Android application.
//  subscription_review: The not interested option of your Android application.
//  review: The app review given by user of your Android application.
//  use_of_app: The user suggestion of your Android application.

internal const val REVIEW_BASE_URL = "https://appreview.vasundharaapps.com/api/"
internal const val REVIEW_API = "app_review_android"
internal const val REVIEW_PARAM_PACKAGE_NAME = "package_name"
internal const val REVIEW_PARAM_VERSION_CODE = "version_code"
internal const val REVIEW_PARAM_LANGUAGE_KEY = "language_key"
internal const val REVIEW_PARAM_SUBSCRIPTION_REVIEW = "subscription_review"
internal const val REVIEW_PARAM_REVIEW = "review"
internal const val REVIEW_PARAM_USE_OF_APP = "use_of_app"
