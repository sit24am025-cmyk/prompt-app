package com.idchan.prompt.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.idchan.prompt.domain.model.HistoryItem
import com.idchan.prompt.domain.usecase.ManageHistoryUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HistoryViewModel(
    private val manageHistoryUseCase: ManageHistoryUseCase
) : ViewModel() {

    val historyItems: StateFlow<List<HistoryItem>> = manageHistoryUseCase.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun toggleFavorite(id: String) {
        viewModelScope.launch {
            manageHistoryUseCase.toggleFavorite(id)
        }
    }

    fun deleteItem(id: String) {
        viewModelScope.launch {
            manageHistoryUseCase.deleteHistory(id)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            manageHistoryUseCase.clearAll()
        }
    }
}
