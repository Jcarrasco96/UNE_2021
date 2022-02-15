package com.jcarrasco96.une2021.activities

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.jcarrasco96.une2021.R
import com.jcarrasco96.une2021.adapter.ContadorAdapter
import com.jcarrasco96.une2021.database.AppDatabase
import com.jcarrasco96.une2021.database.Contador
import com.jcarrasco96.une2021.database.ContadorDao
import com.jcarrasco96.une2021.databinding.ActivityContadorBinding
import com.jcarrasco96.une2021.databinding.DialogAddContadorBinding
import com.jcarrasco96.une2021.interfaces.IContadorAdapterInterface
import com.jcarrasco96.utils.UIUtils

class ContadorActivity : AppCompatActivity(), IContadorAdapterInterface {

    private var isFabHide: Boolean = false

    private lateinit var binding: ActivityContadorBinding

    private lateinit var adapter: ContadorAdapter

    private lateinit var contadorDao: ContadorDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityContadorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        contadorDao = AppDatabase.appDatabase(this).contadorDao()

        binding.fabAdd.setOnClickListener {
            showAddContadorDialog()
        }

        binding.recyclerview.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.VERTICAL, false
        )

        adapter = ContadorAdapter(this)
        binding.recyclerview.adapter = adapter

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            binding.recyclerview.setOnScrollChangeListener { _, _, scrollY, _, oldScrollY ->
                if (scrollY < oldScrollY) {
                    animateFab(false)
                }
                if (scrollY > oldScrollY) {
                    animateFab(true)
                }
            }
        }

        init()
        binding.swipRefresh.setOnRefreshListener {
            init()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_contador, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            R.id.mnu_pay -> {
                call("*444*41#", 35328)
                true
            }
            R.id.mnu_auth_bpa -> {
                call("*444*40*01#", 272)
                true
            }
            R.id.mnu_auth_bandec -> {
                call("*444*40*02#", 226332)
                true
            }
            R.id.mnu_auth_bm -> {
                call("*444*40*03#", 26)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            when (requestCode) {
                35328 -> {
                    call("*444*41#")
                }
                272 -> {
                    call("*444*40*01#")
                }
                226332 -> {
                    call("*444*40*02#")
                }
                26 -> {
                    call("*444*40*03#")
                }
                else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        } else {
            Toast.makeText(this, R.string.permission_accept, Toast.LENGTH_LONG).show()
        }
    }

    private fun init() {
        binding.swipRefresh.isRefreshing = true

        val arr = contadorDao.list() as ArrayList<Contador>
        adapter.updateList(arr, 0, arr.size)

        verifyNoItems()

        binding.swipRefresh.isRefreshing = false
    }

    override fun buttonPressedDelete(contador: Contador) {
        contadorDao.delete(contador)

        val position = adapter.listItems.indexOf(contador)
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

    private fun animateFab(hide: Boolean) {
        if (isFabHide && hide || !isFabHide && !hide) return
        isFabHide = hide
        val moveY = if (hide) 2 * binding.fabAdd.height else 0
        binding.fabAdd.animate().translationY(moveY.toFloat()).setStartDelay(100).setDuration(500)
            .start()
    }

    private fun showAddContadorDialog() {
        val dialogBinding = DialogAddContadorBinding.inflate(layoutInflater)
        val dialog = UIUtils.showDialog(this, dialogBinding.root)

        dialogBinding.btCancel.setOnClickListener { dialog.dismiss() }
        dialogBinding.btSubmit.setOnClickListener {
            val cID = dialogBinding.itemContadorId.text.toString().trim()
            val cName = dialogBinding.itemContadorName.text.toString().trim()

            if (cID.isEmpty() || cName.isEmpty()) {
                Toast.makeText(this, "Verifique los datos", Toast.LENGTH_SHORT).show()
            } else {
                try {
                    val contador = Contador(cID.toLong(), cName)
                    contadorDao.insert(contador)

                    val arr = contadorDao.list() as ArrayList<Contador>
                    adapter.updateList(arr, 0, arr.size)

                    verifyNoItems()

                    dialog.dismiss()
                } catch (ex: SQLiteConstraintException) {
                    Toast.makeText(this, "Registro duplicado", Toast.LENGTH_SHORT).show()
                }
            }
        }

        dialog.show()
    }

    private fun call(phoneNumber: String, requestCode: Int = 0) {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CALL_PHONE
            ) != PackageManager.PERMISSION_GRANTED && requestCode != 0
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CALL_PHONE),
                requestCode
            )
        } else {
            startActivity(
                Intent(
                    "android.intent.action.CALL",
                    Uri.parse("tel:" + Uri.encode(phoneNumber))
                )
            )
        }
    }

    /**
     * autenticar bpa    *444*40*01#
     * autenticar bandec *444*40*02#
     * autenticar bm     *444*40*03#
     * electricidad      *444*41#
     */

}