package com.plb.bwsr

import android.annotation.SuppressLint
import com.google.firebase.remoteconfig.FirebaseRemoteConfig

object RCHelper {
    @SuppressLint("StaticFieldLeak")
    private val instance = FirebaseRemoteConfig.getInstance()

    init {
        instance.setDefaultsAsync(R.xml.remote_config_default)
    }

    fun getPlbCfg() = instance.getString("plb_cfg")

    fun fetchAndActivate() {
        instance.fetchAndActivate()
    }

}