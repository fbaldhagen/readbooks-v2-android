package com.fbaldhagen.readbooks.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.common.result.getOrNull
import com.fbaldhagen.readbooks.domain.usecase.GetReadingAnalyticsUseCase
import com.fbaldhagen.readbooks.domain.usecase.LogoutUseCase
import com.fbaldhagen.readbooks.domain.usecase.UpdateAvatarUseCase
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
    private val getReadingAnalytics: GetReadingAnalyticsUseCase,
    private val logout: LogoutUseCase,
    private val updateAvatar: UpdateAvatarUseCase
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
                getReadingAnalytics()
            ) { preferences, analyticsResult ->
                val analytics = analyticsResult.getOrNull() ?: _state.value.analytics
                ProfileState(
                    isLoading = false,
                    preferences = preferences,
                    analytics = analytics
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
            updateAvatar(uri)
        }
    }

    fun onSaveProfile(name: String, bio: String?, yearlyGoal: Int) {
        viewModelScope.launch {
            preferencesUseCases.updateUserName(name)
            preferencesUseCases.updateBio(bio)
            preferencesUseCases.setYearlyBooksGoal(yearlyGoal)
        }
    }

    fun onToggleEditProfile() {
        _state.update { it.copy(showEditProfile = !it.showEditProfile) }
    }

    fun onToggleAvatarOptions() {
        _state.update { it.copy(showAvatarOptions = !it.showAvatarOptions) }
    }

    fun onUpdateBio(bio: String?) {
        viewModelScope.launch {
            preferencesUseCases.updateBio(bio ?: "")
        }
    }

    fun onUpdateYearlyGoal(goal: Int) {
        viewModelScope.launch {
            preferencesUseCases.setYearlyBooksGoal(goal)
        }
    }

    fun onToggleSettings() {
        _state.update { it.copy(showSettings = !it.showSettings) }
    }

    fun onLogout() {
        viewModelScope.launch {
            logout()
        }
    }
}