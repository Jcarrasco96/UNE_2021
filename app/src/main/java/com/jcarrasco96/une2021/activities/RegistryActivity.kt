package com.jcarrasco96.une2021.activities

import android.content.DialogInterface
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jcarrasco96.une2021.R
import com.jcarrasco96.une2021.adapter.RegistryAdapter
import com.jcarrasco96.une2021.database.AppDatabase
import com.jcarrasco96.une2021.database.Registry
import com.jcarrasco96.une2021.database.RegistryDao
import com.jcarrasco96.une2021.databinding.ActivityRegistryBinding
import com.jcarrasco96.une2021.interfaces.IRegistryAdapterIterface

class RegistryActivity : AppCompatActivity(), IRegistryAdapterIterface {

    private lateinit var binding: ActivityRegistryBinding

    private lateinit var adapter: RegistryAdapter

    private lateinit var registryDao: RegistryDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registryDao = AppDatabase.appDatabase(this).registryDao()

        binding.recyclerview.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false
        )

        adapter = RegistryAdapter(this)
        binding.recyclerview.adapter = adapter

        init()
        binding.swipRefresh.setOnRefreshListener {
            init()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun init() {
        binding.swipRefresh.isRefreshing = true

        val arr = registryDao.list() as ArrayList<Registry>
        adapter.updateList(arr, 0, arr.size)

        verifyNoItems()

        binding.swipRefresh.isRefreshing = false
    }

    override fun buttonPressedDelete(registry: Registry) {
        if (registry.oficial) {
            showDialogDelete(registry)
        } else {
            deleteRegistry(registry)
        }
    }

    override fun buttonPressedOficial(registry: Registry) {
        val position = adapter.listItems.indexOf(registry)

        registry.oficial = !registry.oficial

        registryDao.setOficial(registry.id, registry.oficial)

        adapter.notifyItemChanged(position)
    }

    private fun showDialogDelete(registry: Registry) {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(R.string.delete_reg)
        builder.setMessage(R.string.delete_qestion)
        builder.setPositiveButton(R.string.delete) { _: DialogInterface, _: Int ->
            deleteRegistry(registry)
        }
        builder.setNegativeButton(R.string.cancel) { _: DialogInterface, _: Int -> }
        builder.show()
    }

    private fun deleteRegistry(registry: Registry) {
        registryDao.delete(registry)

        val position = adapter.listItems.indexOf(registry)
        adapter.listItems.removeAt(position)
        adapter.notifyItemRemoved(position)

        verifyNoItems()
    }

    private fun verifyNoItems() {
        if (adapter.listItems.size > 0) {
            binding.tvNoItems.visibility = View.GONE
        } else {
            binding.tvNoItems.visibility = View.VISIBLE
        }
    }

}