package com.institute.ims

import android.app.Application
import com.institute.ims.utils.AppStartupTracker

/** Application entry; triggers optional startup identifier ping only. */
class ImsApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppStartupTracker.reportIfConfigured()
    }
}
