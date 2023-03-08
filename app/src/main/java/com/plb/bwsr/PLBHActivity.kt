package com.plb.bwsr

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.plb.bwsr.d.Info
import com.plb.bwsr.d.PLBDBManager
import com.plb.bwsr.databinding.ActHistoryBinding
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