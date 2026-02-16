package com.fbaldhagen.readbooks.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.usecase.GetHomeContentUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHomeContent: GetHomeContentUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        observeHomeContent()
    }

    private fun observeHomeContent() {
        viewModelScope.launch {
            getHomeContent()
                .onStart {
                    _state.value = _state.value.copy(isLoading = true)
                }
                .catch { e ->
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load home content"
                    )
                }
                .collect { content ->
                    _state.value = HomeState(
                        isLoading = false,
                        currentlyReading = content.currentlyReading,
                        recentBooks = content.recentBooks,
                        readingGoalProgress = content.readingGoalProgress,
                        recentAchievements = content.recentAchievements
                    )
                }
        }
    }

    fun dismissError() {
        _state.value = _state.value.copy(error = null)
    }
}