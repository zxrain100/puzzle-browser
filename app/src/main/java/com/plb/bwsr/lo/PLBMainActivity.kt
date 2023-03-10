package com.plb.bwsr.lo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.plb.bwsr.co.AppConfig
import com.plb.bwsr.dw.BaseActivity
import com.plb.bwsr.co.PLBBroActivity
import com.plb.bwsr.PLBMActivity
import com.plb.bwsr.ho.PLBa
import com.plb.bwsr.dw.PLBap
import com.plb.bwsr.databinding.ActHomeBinding
import com.plb.bwsr.po.PLBHActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PLBMainActivity : BaseActivity() {
    private lateinit var binding: ActHomeBinding

    private var actLauncher: ActivityResultLauncher<Intent>? = null
    private var ad: PLBa? = null

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
                    actLauncher?.launch(Intent(this, PLBHActivity::class.java))
                }.show(this)
            } else {
                actLauncher?.launch(Intent(this, PLBHActivity::class.java))
            }

        }
        binding.mainBook.setOnClickListener {
            val plba = getHomeInter()
            if (plba != null && AppConfig.instance.isM()) {
                plba.onClose {
                    actLauncher?.launch(Intent(this, PLBMActivity::class.java))
                }.show(this)
            } else {
                actLauncher?.launch(Intent(this, PLBMActivity::class.java))
            }

        }


        actLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                if (data != null) {
                    val url = data.getStringExtra("url") ?: return@registerForActivityResult
                    val intent = Intent(this, PLBBroActivity::class.java)
                    intent.putExtra("url", url)
                    startActivity(intent)
                }
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
            val plba = PLBam.instance.create(PLBap.NATIVE)
            binding.adDef.isVisible = false
            if (plba != null) {
                this@PLBMainActivity.ad = plba
                withContext(Dispatchers.Main) {
                    val adViewBind = binding.adView
                    adViewBind.adViewRoot.isVisible = true
                    adViewBind.adViewRoot.onGlobalLayout {
                        val adView = adViewBind.adView
                        plba.showNav {
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


    override fun onDestroy() {
        super.onDestroy()
        this.ad?.onDestroy()
    }

}