package com.plb.bwsr

import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.plb.bwsr.d.Info
import com.plb.bwsr.d.PLBDBManager
import com.plb.bwsr.databinding.ActMarkBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PLBBActivity : BaseActivity() {
    private lateinit var binding: ActMarkBinding

    private val adapter: MHAdapter = MHAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActMarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.statusBar.setStatusBar()

        binding.markClose.setOnClickListener { finish() }

        binding.markRecycler.layoutManager = LinearLayoutManager(this)
        binding.markRecycler.adapter = adapter

        adapter.setOnDeleteListener { _, info ->
            deleteData(info)
            loadData()
        }

        loadData()
    }


    private fun loadData() {
        launch {
            val list = mutableListOf<Info>()
            val data = PLBDBManager.instance.pageMarkDao().getAll()
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
        val page = PLBDBManager.instance.pageMarkDao().getByUrl(item.url)
        if (page != null) {
            PLBDBManager.instance.pageMarkDao().deletePage(page)
        }
    }
}