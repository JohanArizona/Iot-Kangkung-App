package com.example.dashboardhidroponikkangkung.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dashboardhidroponikkangkung.data.model.HidroponikData
import com.example.dashboardhidroponikkangkung.data.preferences.TdsPreferencesManager
import com.example.dashboardhidroponikkangkung.data.repository.HidroponikRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TdsRange(val min: Int, val max: Int)

class HidroponikViewModel(application: Application) : AndroidViewModel(application) {

    private val prefsManager = TdsPreferencesManager(application.applicationContext)
    private val repository = HidroponikRepository()

    private val _uiState = MutableStateFlow(HidroponikData())
    val uiState: StateFlow<HidroponikData> = _uiState.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _tdsRange = MutableStateFlow(
        TdsRange(min = prefsManager.getMinTds(), max = prefsManager.getMaxTds())
    )
    val tdsRange: StateFlow<TdsRange> = _tdsRange.asStateFlow()

    init {
        observeData()
    }

    private fun observeData() {
        viewModelScope.launch {
            repository.observeHidroponikData().collect { data ->
                _uiState.value = data
                _isLoading.value = false
            }
        }
    }

    fun toggleMode() {
        viewModelScope.launch {
            val newMode = !_uiState.value.isAutomatic
            repository.setMode(newMode)
        }
    }

    // NYALAin pompa A & B
    fun activatePumps() {
        viewModelScope.launch {
            repository.turnOnPump("A")
            repository.turnOnPump("B")
        }
    }

    // MATIin pompa A & B
    fun deactivatePumps() {
        viewModelScope.launch {
            repository.turnOffPump("A")
            repository.turnOffPump("B")
        }
    }

    fun updateTdsRange(min: Int, max: Int) {
        prefsManager.setTdsRange(min, max)
        _tdsRange.value = TdsRange(min, max)
    }

    fun resetTdsRange() {
        prefsManager.resetToDefault()
        _tdsRange.value = TdsRange(
            min = prefsManager.getMinTds(),
            max = prefsManager.getMaxTds()
        )
    }
}