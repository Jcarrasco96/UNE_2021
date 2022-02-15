package com.jcarrasco96.une2021

import android.app.Application
import com.jcarrasco96.une2021.utils.Preferences

class UNE2021: Application() {

    override fun onCreate() {
        super.onCreate()
        Preferences.init(this)
    }

}