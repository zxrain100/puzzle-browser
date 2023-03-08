package com.plb.bwsr.co

import android.util.Base64
import com.plb.bwsr.AppConfig
import com.plb.bwsr.RCHelper
import org.json.JSONObject


object PLBap {
    const val INDEX = "inter_l"
    const val HOME = "inter_h"
    const val NATIVE = "native"

    val cacheList = HashMap<String, PLBa>()
    fun hasCache(key: String): Boolean {
        synchronized(cacheList) {
            if (cacheList.isEmpty()) return false
            val cache = cacheList[key] ?: return false
            return if (cache.isAva()) {
                true
            } else {
                cache.onDestroy()
                cacheList.remove(key)
                false
            }
        }
    }

    fun getCache(key: String): PLBa? {
        synchronized(cacheList) {
            if (cacheList.isEmpty()) return null
            val cache = cacheList[key] ?: return null
            return if (!cache.isAva()) {
                cache.onDestroy()
                null
            } else {
                cache
            }
        }
    }

    fun getRequestLists(sk: String): List<PLBau> {
        try {
            val s = RCHelper.instance.getPlbCfg()
            val json = JSONObject(String(Base64.decode(s.toByteArray(), Base64.DEFAULT)))
//            val mode = json.getBoolean("plb_mode")
//            AppConfig.instance.setSafeMode(mode)
            val jsonArray = json.getJSONArray("plb_config")

            val adReqList = mutableListOf<PLBau>()
            for (i in 0 until jsonArray.length()) {
                val obj: JSONObject = jsonArray.getJSONObject(i)
                val key = obj.getString("plb_key")
                val jsonArray2 = obj.getJSONArray("plb_ids")
                val au: MutableList<PLBau> = mutableListOf()
                for (j in 0 until jsonArray2.length()) {
                    val obj2: JSONObject = jsonArray2.getJSONObject(j)
                    val id = obj2.getString("plb_id")
                    val priority = obj2.getInt("plb_priority")
                    val t = when (obj2.optString("plb_type")) {
                        "nav" -> 0
                        "inter" -> 1
                        else -> 1
                    }
                    au.add(PLBau(id, priority, t))
                }
                au.sortBy { -it.priority }
                if (key == sk) {
                    adReqList.addAll(au)
                }
            }
            return adReqList
        } catch (e: Exception) {
            e.printStackTrace()
            return mutableListOf()
        }
    }

}
