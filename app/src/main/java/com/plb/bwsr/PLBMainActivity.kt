package com.plb.bwsr

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.plb.bwsr.co.PLBa
import com.plb.bwsr.co.PLBam
import com.plb.bwsr.co.PLBap
import com.plb.bwsr.databinding.ActHomeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PLBMainActivity : BaseActivity() {
    private lateinit var binding: ActHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainBrowser.setOnClickListener {
            val plba = getHomeInter()
            if (plba != null && AppConfig.instance.isM()) {
                plba.onClose {
                    startActivity(Intent(this, PLBBroActivity::class.java))
                }.show(this)
            } else {
                startActivity(Intent(this, PLBBroActivity::class.java))
            }
        }
        binding.mainHistory.setOnClickListener {
            val plba = getHomeInter()
            if (plba != null && AppConfig.instance.isM()) {
                plba.onClose {
                    startActivity(Intent(this, PLBHActivity::class.java))
                }.show(this)
            } else {
                startActivity(Intent(this, PLBHActivity::class.java))
            }

        }
        binding.mainBook.setOnClickListener {
            val plba = getHomeInter()
            if (plba != null && AppConfig.instance.isM()) {
                plba.onClose {
                    startActivity(Intent(this, PLBBActivity::class.java))
                }.show(this)
            } else {
                startActivity(Intent(this, PLBBActivity::class.java))
            }

        }
    }

    override fun onResume() {
        super.onResume()
        loadAd()
        loadInter()
    }


    private fun getHomeInter(): PLBa? {
        return PLBam.instance.get(PLBap.HOME)
    }

    private fun loadInter() {
        launch {
            if (PLBam.instance.get(PLBap.HOME) == null) {
                PLBam.instance.create(PLBap.HOME)
            }
        }
    }


    private fun loadAd() {
        launch {
            val spba = PLBam.instance.create(PLBap.NATIVE)
            binding.adDef.isVisible = false
            if (spba != null) {
                withContext(Dispatchers.Main) {
                    val adViewBind = binding.adView
                    adViewBind.adViewRoot.isVisible = true
                    adViewBind.adViewRoot.onGlobalLayout {
                        val adView = adViewBind.adView
                        spba.showNav {
                            if (this == null) {
                                adView.visibility = View.GONE
                            } else {
                                adViewBind.adAction.text = this.callToAction
                                adViewBind.adTitle.text = this.headline
                                adViewBind.adDes.text = this.body
                                adView.adChoicesView = adViewBind.adChoices
                                adView.callToActionView = adViewBind.adAction
                                adView.imageView = adViewBind.adImage
                                adView.mediaView = adViewBind.adMedia
                                adView.iconView = adViewBind.adIcon
                                adView.headlineView = adViewBind.adTitle
                                adView.bodyView = adViewBind.adDes
                                Glide.with(this@PLBMainActivity).load(this.icon?.uri).into(adViewBind.adIcon)
                                adView.setNativeAd(this)
                                adView.visibility = View.VISIBLE
                            }
                        }
                    }
                }
            }
        }
    }

}