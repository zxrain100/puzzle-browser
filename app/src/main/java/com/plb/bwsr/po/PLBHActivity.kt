package com.plb.bwsr.po

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.plb.bwsr.dw.BaseActivity
import com.plb.bwsr.MHAdapter
import com.plb.bwsr.databinding.ActHistoryBinding
import com.plb.bwsr.dw.Info
import com.plb.bwsr.ho.PLBDBManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PLBHActivity : BaseActivity() {
    private lateinit var binding: ActHistoryBinding
    private val adapter: MHAdapter = MHAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.statusBar.setStatusBar()

        binding.back.setOnClickListener { finish() }
        binding.hisRecycle.layoutManager = LinearLayoutManager(this)
        binding.hisRecycle.adapter = adapter

        adapter.setOnDeleteListener { _, info ->
            deleteData(info)
            loadData()
        }

        adapter.setOnItemClickListener { _, info ->
            val intent = Intent()
            intent.putExtra("url", info.url)
            setResult(RESULT_OK, intent)
            finish()
        }
        loadData()
    }

    private fun loadData() {
        launch {
            val list = mutableListOf<Info>()
            val data = PLBDBManager.instance.pageHistoryDao().getAll()
            data.forEach {
                list.add(Info(it.url, it.title, it.time))
            }

            list.sortBy { -it.time }

            withContext(Dispatchers.Main) {
                adapter.submitList(list)
            }
        }

    }

    private fun deleteData(item: Info) {
        val page = PLBDBManager.instance.pageHistoryDao().getByUrl(item.url)
        if (page != null) {
            PLBDBManager.instance.pageHistoryDao().deletePage(page)
        }
    }
}