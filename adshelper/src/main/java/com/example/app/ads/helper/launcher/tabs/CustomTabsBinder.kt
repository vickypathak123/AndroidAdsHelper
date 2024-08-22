package com.example.app.ads.helper.launcher.tabs

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class CustomTabsBinder : DefaultLifecycleObserver {
    override fun onStart(owner: LifecycleOwner) {
        CustomTabsHelper.bind()
    }

    override fun onStop(owner: LifecycleOwner) {
        CustomTabsHelper.unbind()
    }
}
