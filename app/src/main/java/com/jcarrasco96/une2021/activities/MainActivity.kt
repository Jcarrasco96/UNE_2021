package com.jcarrasco96.une2021.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.jcarrasco96.une2021.R
import com.jcarrasco96.une2021.database.AppDatabase
import com.jcarrasco96.une2021.database.ContadorDao
import com.jcarrasco96.une2021.database.RegistryDao
import com.jcarrasco96.une2021.databinding.ActivityMainBinding
import com.jcarrasco96.une2021.databinding.DialogCanPayBinding
import com.jcarrasco96.une2021.utils.Preferences
import com.jcarrasco96.une2021.utils.Update
import com.jcarrasco96.une2021.utils.Utils
import com.jcarrasco96.utils.UIUtils
import com.jcarrasco96.utils.ui.Table
import java.text.DecimalFormat
import java.text.NumberFormat

class MainActivity : AppCompatActivity() {

    private val nf: NumberFormat = DecimalFormat("#,##0.00")

    private lateinit var binding: ActivityMainBinding

    private lateinit var registryDao: RegistryDao
    private lateinit var contadorDao: ContadorDao

    private var modeRead: Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Preferences.nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        registryDao = AppDatabase.appDatabase(this).registryDao()
        contadorDao = AppDatabase.appDatabase(this).contadorDao()

        binding.lastRead.setText(Preferences.read.toString())
        binding.consumo.setText("0")

        binding.btnCalcular.setOnClickListener {
            if (modeRead) {
                if (binding.lastRead.text!!.isNotEmpty() && binding.read.text!!.isNotEmpty()) {
                    val lastRead: Long = binding.lastRead.text.toString().toLong()
                    val read: Long = binding.read.text.toString().toLong()

                    if (lastRead < read) {
                        registryDao.insert(read, lastRead)

                        Preferences.read = read
                        calculate(read - lastRead)
                    } else {
                        Toast.makeText(this, getString(R.string.readgt), Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, getString(R.string.readall), Toast.LENGTH_SHORT).show()
                }
            } else {
                if (binding.consumo.text!!.isNotEmpty()) {
                    val consumo: Long = binding.consumo.text.toString().toLong()
                    calculate(consumo)
                } else {
                    Toast.makeText(this, getString(R.string.readall), Toast.LENGTH_SHORT).show()
                }
            }
        }
        binding.btnFlipMode.setOnClickListener {
            UIUtils.flip(binding.layoutLectura, binding.layoutConsumo)
            modeRead = !modeRead
        }

        calculate(0)

        Update.searchUpdate(this, packageName)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mnu_registry -> {
                startActivity(Intent("com.jcarrasco96.une2021.REGISTROS"))
                true
            }
            R.id.mnu_contador -> {
                startActivity(Intent("com.jcarrasco96.une2021.CONTADORES"))
                true
            }
            R.id.mnu_spend -> {
                showDialogSpend()
                true
            }
            R.id.mnu_settings -> {
                startActivity(Intent("com.jcarrasco96.une2021.SETTINGS"))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showDialogSpend() {
        val dialogBinding = DialogCanPayBinding.inflate(layoutInflater)
        val dialog = UIUtils.showDialog(
            this,
            dialogBinding.root,
            false,
            WindowManager.LayoutParams.MATCH_PARENT
        )

        dialogBinding.btnCalcularSpend.setOnClickListener {
            if (dialogBinding.money.text!!.isNotEmpty()) {
                val money = dialogBinding.money.text.toString().toDouble()
                val spend = Utils.canPay(money)

                if (spend == -1) {
                    dialogBinding.tvSpend.setText(R.string.more_5000)
                } else {
                    dialogBinding.tvSpend.text = getString(R.string.minus_5000, spend)
                    calculate(spend.toLong())
                }
            } else {
                dialogBinding.tvSpend.setText(R.string.readall)
            }
        }

        dialog.show()
    }

    private fun calculate(consumo: Long) {
        binding.tableLayout.removeAllViews()

        val tabla = Table(this, binding.tableLayout)

        tabla.addHeader(
            resources.getStringArray(R.array.cabecera_tabla),
            R.style.tableCabecera,
            R.drawable.tabla_celda_cabecera
        )

        val consumoxtramo = Utils.kWhXTramos(consumo)
        val precioTotal = Utils.calcularImporte(consumoxtramo)

        val ranges = resources.getStringArray(R.array.ranges)

        for (i in consumoxtramo.indices) {
            val elementos = ArrayList<String>()
            elementos.add(ranges[i])
            elementos.add(consumoxtramo[i].toString())
            elementos.add(nf.format(Utils.PRECIO[i]))
            elementos.add(nf.format(consumoxtramo[i] * Utils.PRECIO[i]))
            tabla.addRow(elementos, R.style.tableCelda, R.drawable.tabla_celda)
        }

        binding.consKwh.text = getString(R.string.consumo_kWh, consumo)
        binding.importe.text = getString(R.string.importe_cup, nf.format(precioTotal))
    }

}