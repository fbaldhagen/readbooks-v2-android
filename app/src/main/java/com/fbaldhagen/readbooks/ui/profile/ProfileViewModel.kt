package com.fbaldhagen.readbooks.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.usecase.GetUserProfileStatsUseCase
import com.fbaldhagen.readbooks.domain.usecase.UserPreferencesUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val preferencesUseCases: UserPreferencesUseCases,
    private val getUserProfileStats: GetUserProfileStatsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state: StateFlow<ProfileState> = _state.asStateFlow()

    init {
        observeProfile()
    }

    private fun observeProfile() {
        viewModelScope.launch {
            combine(
                preferencesUseCases.observe(),
                getUserProfileStats()
            ) { preferences, stats ->
                ProfileState(
                    isLoading = false,
                    preferences = preferences,
                    stats = stats
                )
            }
                .catch { e ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = e.message ?: "Failed to load profile"
                        )
                    }
                }
                .collect { profileState ->
                    _state.value = profileState
                }
        }
    }

    fun onUpdateUserName(name: String) {
        viewModelScope.launch {
            preferencesUseCases.updateUserName(name)
        }
    }

    fun onUpdateAvatarUri(uri: String?) {
        viewModelScope.launch {
            preferencesUseCases.updateAvatarUri(uri)
        }
    }
}