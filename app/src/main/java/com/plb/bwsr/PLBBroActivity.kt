package com.plb.bwsr

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.plb.bwsr.co.PLBa
import com.plb.bwsr.co.PLBam
import com.plb.bwsr.co.PLBap
import com.plb.bwsr.databinding.ActBrowserBinding
import com.plb.bwsr.h.WebCallback
import com.plb.bwsr.h.WebHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PLBBroActivity : BaseActivity(), WebCallback, View.OnClickListener {
    private lateinit var binding: ActBrowserBinding
    private lateinit var webHelper: WebHelper

    private var ad: PLBa? = null
    private var loadingType = 0  //0:未加载，1：加载中

    private var actLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initView()
        initListener()

        val url = intent.getStringExtra("url") ?: return
        startLoad(url)
    }

    private fun initView() {
        binding = ActBrowserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.statusBar.setStatusBar()
        webHelper = WebHelper(this)
        webHelper.initWebView(binding.webView)
    }

    private fun initListener() {
        arrayListOf(
            binding.addMark,
            binding.search,
            binding.itemFacebook,
            binding.itemInstagram,
            binding.itemYahoo,
            binding.itemTwitter,
            binding.next,
            binding.pre,
            binding.home,
            binding.mark,
            binding.history
        ).forEach {
            it.setOnClickListener(this)
        }
        binding.inputUrl.setOnEditorActionListener(TextView.OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                //Toast.makeText(activity, "开始搜索", Toast.LENGTH_SHORT).show()
                startLoad(v.text.toString())
                return@OnEditorActionListener true
            }
            false
        })

        actLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val data = it.data
                if (data != null) {
                    val url = data.getStringExtra("url") ?: return@registerForActivityResult
                    startLoad(url)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        loadAd()
    }


    private fun startLoad(str: String) {
        if (binding.homePage.visibility == View.VISIBLE) {
            //加载网页，移除广告
            this.ad?.onDestroy()
        }
        binding.webView.visibility = View.VISIBLE
        binding.homePage.isVisible = false
        binding.itemLayout.isVisible = false
        binding.webPage.isVisible = true
        if (webHelper.isUrl(str)) {
            binding.webView.loadUrl(str)
        } else {
            binding.webView.loadUrl("https://www.google.com/search?q=$str")
        }
    }

    private fun showHome() {
        if (binding.webPage.visibility == View.VISIBLE) {
            loadAd()
        }
        binding.webView.loadUrl("")
        binding.webView.clearHistory()
        binding.webView.visibility = View.GONE
        binding.inputUrl.setText("")
        binding.webPage.isVisible = false
        binding.itemLayout.isVisible = true
        binding.homePage.isVisible = true
        binding.preImg.setImageResource(R.mipmap.pre_1)
        binding.nextImg.setImageResource(R.mipmap.next_1)
        loadingType = 0
    }


    private fun updatePreAndNextStatus() {
        if (binding.webView.canGoBack()) {
            binding.preImg.setImageResource(R.mipmap.pre)
        } else {
            binding.preImg.setImageResource(R.mipmap.pre_1)
        }

        if (binding.webView.canGoForward()) {
            binding.nextImg.setImageResource(R.mipmap.next)
        } else {
            binding.nextImg.setImageResource(R.mipmap.next_1)
        }
    }


    private fun updateFavIcon(url: String) {
        if (webHelper.isBookmarkUrl(url)) {
            binding.addMark.setImageResource(R.mipmap.fav_1)
        } else {
            binding.addMark.setImageResource(R.mipmap.fav)
        }
    }

    override fun onWebStarted(url: String) {
        binding.webProgress.isVisible = true
        binding.webProgress.progress = 0

        if (url == "about:blank") {
            binding.search.setImageResource(R.mipmap.search)
            binding.inputUrl.setText("")
        } else {
            loadingType = 1
            binding.search.setImageResource(R.mipmap.cancel)
            binding.inputUrl.setText(url)
        }

        updateFavIcon(url)

    }

    override fun onWebFinish(url: String) {
        binding.webProgress.isVisible = false
        loadingType = 0
        binding.search.setImageResource(R.mipmap.search)
        updatePreAndNextStatus()
    }

    override fun onWebProgress(progress: Int) {
        binding.webProgress.progress = progress
    }

    override fun onClick(v: View?) {

        when (v) {
            binding.addMark -> {
                val title = binding.webView.copyBackForwardList().currentItem?.title ?: ""
                val uri = binding.webView.copyBackForwardList().currentItem?.url ?: ""
                if (webHelper.isUrl(uri)) {
                    if (!webHelper.isBookmarkUrl(uri)) {
                        webHelper.addBookMar(uri, title)
                        Toast.makeText(this, "BookMarked!", Toast.LENGTH_SHORT).show()
                        updateFavIcon(uri)
                    }
                }
            }
            binding.search -> {
                //stop、搜索
                if (loadingType == 1) {
                    binding.webView.stopLoading()
                    loadingType = 0
                    binding.search.setImageResource(R.mipmap.search)
                } else {
                    startLoad(binding.inputUrl.text.toString())
                }
            }

            binding.itemFacebook -> startLoad("https://www.facebook.com/")
            binding.itemInstagram -> startLoad("https://www.instagram.com/")
            binding.itemYahoo -> startLoad("https://www.yahoo.com/")
            binding.itemTwitter -> startLoad("https://www.twitter.com/")

            binding.pre -> {
                updatePreAndNextStatus()
                if (binding.webPage.visibility == View.VISIBLE) {
                    if (binding.webView.canGoBack()) {
                        val m = binding.webView.copyBackForwardList()
                        if (m.currentIndex > 0) {
                            val url = m.getItemAtIndex(m.currentIndex - 1).url
                            if (url == "about:blank") {
                                showHome()
                            }
                        }
                        binding.webView.goBack()
                    } else {
                        showHome()
                    }
                }
            }
            binding.next -> {
                updatePreAndNextStatus()
                if (binding.webView.canGoForward()) {
                    binding.webView.goForward()
                }
            }
            binding.home -> showHome()
            binding.mark -> actLauncher?.launch(Intent(this, PLBMActivity::class.java))
            binding.history -> actLauncher?.launch(Intent(this, PLBHActivity::class.java))

        }

    }


    override fun onBackPressed() {
        if (binding.webPage.visibility == View.VISIBLE) {
            if (binding.webView.canGoBack()) {
                val m = binding.webView.copyBackForwardList()
                if (m.currentIndex > 0) {
                    val url = m.getItemAtIndex(m.currentIndex - 1).url
                    if (url == "about:blank") {
                        showHome()
                    }
                }
                binding.webView.goBack()
            } else {
                showHome()
            }
        } else {
            super.onBackPressed()
        }
    }


    private fun loadAd() {
        launch {
            val ad = PLBam.instance.create(PLBap.NATIVE)
            this@PLBBroActivity.ad = ad
            withContext(Dispatchers.Main) {
                binding.adDefault.isVisible = false
                if (ad != null) {
                    val adViewBind = binding.adView
                    adViewBind.adViewRoot.isVisible = true
                    adViewBind.adViewRoot.onGlobalLayout {
                        val adView = adViewBind.adView
                        ad.showNav {
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
                                Glide.with(this@PLBBroActivity).load(this.icon?.uri)
                                    .into(adViewBind.adIcon)
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