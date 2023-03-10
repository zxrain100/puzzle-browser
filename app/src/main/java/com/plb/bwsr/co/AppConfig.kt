package com.plb.bwsr.co

import android.content.Context
import com.plb.bwsr.PLB

class AppConfig{

    companion object {
        val instance: AppConfig by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { AppConfig() }
    }

    private val mConfig = (PLB.context.applicationContext ?: PLB.context).getSharedPreferences("plb_config", Context.MODE_PRIVATE)!!

//    fun isSafe() = mConfig.getBoolean("is_safe_mode", false)
//    fun setSafeMode(bool: Boolean) {
//        mConfig.edit().putBoolean("is_safe_mode", bool).apply()
//    }

    fun setRefUrl(string: String) {
        mConfig.edit().putString("plb_ref_url", string).apply()
    }

    fun getRefUrl() = mConfig.getString("plb_ref_url", "") ?: ""


    fun isM(): Boolean {
        val ref = getRefUrl()
        val isM = ref.contains("fb4a") || ref.contains("gclid") || ref.contains("not%20set") || ref.contains(
            "youtubeads"
        )
        return isM
    }


}