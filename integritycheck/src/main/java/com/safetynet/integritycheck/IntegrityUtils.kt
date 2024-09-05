package com.safetynet.integritycheck

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

internal val Context.isOnlineApp: Boolean
    get() {
        (getSystemService(ConnectivityManager::class.java)).let { connectivityManager ->
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)?.let {
                val isValidated: Boolean = it.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
                val isInternet: Boolean = it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)

                return (isValidated && isInternet)
            }
        }
        return false
    }