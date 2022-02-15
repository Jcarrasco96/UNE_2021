package com.jcarrasco96.une2021.activities

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.sqlite.SQLiteConstraintException
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.CheckBoxPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.jcarrasco96.une2021.R
import com.jcarrasco96.une2021.database.AppDatabase
import com.jcarrasco96.une2021.database.ContadorDao
import com.jcarrasco96.une2021.database.RegistryDao
import com.jcarrasco96.une2021.database.SaveJson
import com.jcarrasco96.une2021.utils.Preferences
import com.jcarrasco96.une2021.utils.Update
import com.jcarrasco96.utils.FileUtils
import com.jcarrasco96.utils.UIUtils
import java.io.IOException

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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

    class SettingsFragment : PreferenceFragmentCompat() {

        private var versionName: String = ""

        private lateinit var createFileIntent: ActivityResultLauncher<Intent>
        private lateinit var openFileIntent: ActivityResultLauncher<Intent>

        private lateinit var registryDao: RegistryDao
        private lateinit var contadorDao: ContadorDao

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.preferences, rootKey)

            registryDao = AppDatabase.appDatabase(requireContext()).registryDao()
            contadorDao = AppDatabase.appDatabase(requireContext()).contadorDao()

            createFileIntent =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == RESULT_OK) {
                        it.data?.data?.also { uri ->
                            val registries = registryDao.list()
                            val contadors = contadorDao.list()
                            val save = SaveJson(registries, contadors)

                            try {
                                FileUtils.writeTextToUri(
                                    requireActivity().contentResolver, uri, Gson().toJson(save)
                                )
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            openFileIntent =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    if (it.resultCode == RESULT_OK) {
                        it.data?.data?.also { uri ->
                            try {
                                val contenido = FileUtils.readTextFromUri(
                                    requireActivity().contentResolver,
                                    uri
                                )
                                val sType = object : TypeToken<SaveJson>() {}.type
                                val jsonSave = Gson().fromJson<SaveJson>(contenido, sType)
                                jsonSave.registries.forEach { registry ->
                                    registryDao.insert(
                                        registry.lectura,
                                        registry.ultima_lectura,
                                        registry.fecha,
                                        registry.oficial
                                    )
                                }
                                jsonSave.contadors.forEach { contador ->
                                    try {
                                        contadorDao.insert(contador)
                                    } catch (ex: SQLiteConstraintException) {
                                    }
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                        }
                    }
                }

            versionName =
                UIUtils.versionApp(requireActivity().packageManager, requireActivity().packageName)

            findPreference<Preference>("key_app_name")?.summary = getString(R.string.version, versionName)

            findPreference<Preference>("key_send_feedback")?.onPreferenceClickListener =
                Preference.OnPreferenceClickListener {
                    sendFeedback(requireActivity())
                    true
                }

            findPreference<CheckBoxPreference>("pref_night_mode")?.setOnPreferenceClickListener {
                if (Preferences.nightMode) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
                requireActivity().recreate()
                true
            }

            findPreference<Preference>("key_update")?.setOnPreferenceClickListener {
                Update.searchUpdate(requireContext(), requireActivity().packageName, true)
                true
            }

            findPreference<Preference>("key_develop")?.setOnPreferenceClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/jcarrasco96"))

                try {
                    if (requireActivity().packageManager.getPackageInfo(
                            "org.telegram.messenger.web",
                            0
                        ) != null
                    ) {
                        intent.setPackage("org.telegram.messenger.web")
                    }
                } catch (ex: PackageManager.NameNotFoundException) {
                }
                try {
                    if (requireActivity().packageManager.getPackageInfo(
                            "org.telegram.messenger",
                            0
                        ) != null
                    ) {
                        intent.setPackage("org.telegram.messenger")
                    }
                } catch (ex: PackageManager.NameNotFoundException) {
                }
                startActivity(intent)

                true
            }

            findPreference<Preference>("key_web")?.setOnPreferenceClickListener {
                startActivity(
                    Intent(
                        "android.intent.action.VIEW",
                        Uri.parse("https://cmsagency.com.es/dqpXRQDPoS")
                    )
                )
                true
            }

            findPreference<Preference>("key_export")?.setOnPreferenceClickListener {
                FileUtils.createFile(createFileIntent, UIUtils.makeDate(true), "application/json")
                true
            }

            findPreference<Preference>("key_import")?.setOnPreferenceClickListener {
                FileUtils.openFile(openFileIntent, "application/json")
                true
            }
        }

        private fun sendFeedback(context: Context) {
            val body: String = context.getString(
                R.string.send_comments,
                Build.VERSION.RELEASE,
                versionName,
                Build.BRAND,
                Build.MODEL,
                Build.MANUFACTURER
            )
            val intent = Intent(Intent.ACTION_SEND)
            intent.type = "message/rfc822"
            intent.putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf("jcarrasco96joker@gmail.com")
            )
            intent.putExtra(Intent.EXTRA_SUBJECT, "Problema!");
            intent.putExtra(Intent.EXTRA_TEXT, body)
            context.startActivity(Intent.createChooser(intent, "Seleccione"))
        }

    }

}