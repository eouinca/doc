package moe.doc.manager.management

import android.os.Bundle
import android.util.TypedValue
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import moe.doc.manager.Helps
import moe.doc.manager.R
import moe.doc.manager.app.AppBarActivity
import moe.doc.manager.databinding.AppsActivityBinding
import moe.doc.manager.utils.CustomTabsHelper
import rikka.lifecycle.Status
import rikka.recyclerview.addEdgeSpacing
import rikka.recyclerview.fixEdgeEffect
import rikka.doc.doc
import java.util.*

class ApplicationManagementActivity : AppBarActivity() {

    private val viewModel by appsViewModel()
    private val adapter = AppsAdapter()

    private val binderDeadListener = doc.OnBinderDeadListener {
        if (!isFinishing) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!doc.pingBinder()) {
            finish()
            return
        }

        val binding = AppsActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        viewModel.packages.observe(this) {
            when (it.status) {
                Status.SUCCESS -> {
                    adapter.updateData(it.data)
                }
                Status.ERROR -> {
                    finish()
                    val tr = it.error
                    Toast.makeText(this, Objects.toString(tr, "unknown"), Toast.LENGTH_SHORT).show()
                    tr.printStackTrace()
                }
                Status.LOADING -> {

                }
            }
        }
        if (viewModel.packages.value == null) {
            viewModel.load()
        }

        val recyclerView = binding.list
        recyclerView.adapter = adapter
        recyclerView.fixEdgeEffect()
        recyclerView.addEdgeSpacing(top = 8f, bottom = 8f, unit = TypedValue.COMPLEX_UNIT_DIP)

        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                viewModel.load(true)
            }
        })

        doc.addBinderDeadListener(binderDeadListener)
    }

    override fun onDestroy() {
        super.onDestroy()

        doc.removeBinderDeadListener(binderDeadListener)
    }

    override fun onResume() {
        super.onResume()
        adapter.notifyDataSetChanged()
    }
}
