package com.example.dashboardhidroponikkangkung.data.preferences

import android.content.Context
import android.content.SharedPreferences

class TdsPreferencesManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("tds_settings", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_MIN_TDS = "min_tds"
        private const val KEY_MAX_TDS = "max_tds"
        private const val DEFAULT_MIN_TDS = 1050
        private const val DEFAULT_MAX_TDS = 1400
    }

    fun getMinTds(): Int {
        return prefs.getInt(KEY_MIN_TDS, DEFAULT_MIN_TDS)
    }

    fun getMaxTds(): Int {
        return prefs.getInt(KEY_MAX_TDS, DEFAULT_MAX_TDS)
    }

    fun setTdsRange(min: Int, max: Int) {
        prefs.edit().apply {
            putInt(KEY_MIN_TDS, min)
            putInt(KEY_MAX_TDS, max)
            apply()
        }
    }

    fun resetToDefault() {
        setTdsRange(DEFAULT_MIN_TDS, DEFAULT_MAX_TDS)
    }
}