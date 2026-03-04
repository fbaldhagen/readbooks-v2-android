package com.fbaldhagen.readbooks.ui.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fbaldhagen.readbooks.domain.model.Achievement
import com.fbaldhagen.readbooks.domain.usecase.AchievementUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.shareIn
import javax.inject.Inject

@HiltViewModel
class AchievementViewModel @Inject constructor(
    achievementUseCases: AchievementUseCases
) : ViewModel() {

    val newlyUnlocked: SharedFlow<List<Achievement>> = achievementUseCases
        .observeNewlyUnlocked()
        .shareIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly
        )
}