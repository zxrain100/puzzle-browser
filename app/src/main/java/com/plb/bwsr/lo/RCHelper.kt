package com.plb.bwsr.lo

import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.plb.bwsr.R

class RCHelper {
    companion object {
        val instance: RCHelper by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { RCHelper() }
    }

    private lateinit var config: FirebaseRemoteConfig

    fun getPlbCfg() = config.getString("plb_cfg")

    fun initAndFetchAndActivate() {
        config = FirebaseRemoteConfig.getInstance()
        config.setDefaultsAsync(R.xml.remote_config_default)
        config.fetchAndActivate()
    }

}