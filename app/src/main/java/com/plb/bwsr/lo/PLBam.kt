package com.plb.bwsr.lo

import android.content.Context
import com.google.android.gms.ads.*
import com.google.android.gms.ads.appopen.AppOpenAd
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.plb.bwsr.ho.PLBa
import com.plb.bwsr.dw.PLBap
import com.plb.bwsr.co.PLBau
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class PLBam {
    companion object {
        val instance: PLBam by lazy(LazyThreadSafetyMode.SYNCHRONIZED) { PLBam() }
    }

    private lateinit var context: Context
    private var isLoad = hashMapOf<String, Boolean>()

    fun initialize(context: Context): PLBam {
        this.context = context
        try {
            MobileAds.initialize(context) {
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return this
    }

    suspend fun create(key: String): PLBa? = withContext(Dispatchers.Main) {
        val lists = PLBap.getRequestLists(key)
        var ret: PLBa?
        for (req in lists) {
            if (req.id.isNotEmpty()) {
                ret = create(key, req)
                if (ret != null) {
                    return@withContext ret
                }
            }
        }
        return@withContext null
    }

    suspend fun creates(vararg key: String) = withContext(Dispatchers.Main) {
        val list = mutableListOf<PLBa>()
        for (k in key) {
            val ret = create(k)
            if (ret != null) {
                list.add(ret)
            }
        }
        return@withContext list
    }


    fun get(vararg key: String): PLBa? = getCache(*key, index = 0)


    private suspend fun create(key: String, plbau: PLBau): PLBa? =
        withContext(Dispatchers.Main) {
            if (isLoad[key] == true) {
                return@withContext null
            }
            if (PLBap.hasCache(key)) {
                val PLBA = get(key)
                if (PLBA != null) {
                    return@withContext PLBA
                }
            }
            isLoad[key] = true
            val ret = runCatching {
                when (plbau.type) {
                    0 -> crateNativeAd(key, plbau.id)
                    1 -> crateInterstitialAd(key, plbau.id)
                    2 -> crateOpenAd(key, plbau.id)
                    else -> crateInterstitialAd(key, plbau.id)
                }
            }
            isLoad[key] = false

            if (ret.isSuccess) {
                val ad = ret.getOrNull()
                if (ad != null) {
                    return@withContext ad
                }
            }
            return@withContext null
        }


    private fun getCache(vararg key: String, index: Int): PLBa? {
        val count = key.size
        val plba = PLBap.getCache(key[index])
        return plba ?: if (index < count - 1) {
            getCache(*key, index = index + 1)
        } else {
            null
        }
    }

    private suspend fun crateNativeAd(key: String, id: String): PLBa {
        return suspendCancellableCoroutine {
            //广告需要ui线程上加载
            val adLoader = AdLoader.Builder(context, id)
                .forNativeAd { plba ->
                    val nativeAd = PLBa(plba)
                    PLBap.cacheList[key]?.onDestroy()
                    PLBap.cacheList[key] = nativeAd
                    it.resume(nativeAd)
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(p0: LoadAdError) {
                        super.onAdFailedToLoad(p0)
                        it.resumeWithException(Exception(p0.code.toString()))
                    }
                })
                .withNativeAdOptions(
                    NativeAdOptions.Builder()
                        .build()
                )
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    private suspend fun crateInterstitialAd(key: String, id: String): PLBa {
        return suspendCancellableCoroutine {
            val adRequest = AdRequest.Builder().build()
            InterstitialAd.load(context, id, adRequest, object : InterstitialAdLoadCallback() {
                override fun onAdFailedToLoad(adError: LoadAdError) {
                    it.resumeWithException(Exception(adError.code.toString()))
                }

                override fun onAdLoaded(interstitialAd: InterstitialAd) {
                    val plba = PLBa(interstitialAd)
                    //之前有缓存的广告，先将之前的广告销毁
                    PLBap.cacheList[key]?.onDestroy()
                    PLBap.cacheList[key] = plba
                    it.resume(plba)
                }
            })
        }
    }


    private suspend fun crateOpenAd(key: String, id: String): PLBa {
        return suspendCancellableCoroutine {
            AppOpenAd.load(
                context,
                id,
                AdRequest.Builder().build(),
                AppOpenAd.APP_OPEN_AD_ORIENTATION_PORTRAIT,
                object : AppOpenAd.AppOpenAdLoadCallback() {
                    override fun onAdFailedToLoad(adError: LoadAdError) {
                        super.onAdFailedToLoad(adError)
                        it.resumeWithException(Exception(adError.code.toString()))
                    }

                    override fun onAdLoaded(p0: AppOpenAd) {
                        super.onAdLoaded(p0)
                        val plba = PLBa(p0)
                        //之前有缓存的广告，先将之前的广告销毁
                        PLBap.cacheList[key]?.onDestroy()
                        PLBap.cacheList[key] = plba
                        it.resume(plba)
                    }
                }
            )
        }
    }

}