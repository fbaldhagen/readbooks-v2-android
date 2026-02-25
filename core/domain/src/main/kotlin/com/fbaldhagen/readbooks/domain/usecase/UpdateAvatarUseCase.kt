package com.fbaldhagen.readbooks.domain.usecase

import com.fbaldhagen.readbooks.domain.repository.UserPreferencesRepository
import com.fbaldhagen.readbooks.domain.repository.UserStorageRepository
import javax.inject.Inject

class UpdateAvatarUseCase @Inject constructor(
    private val userStorageRepository: UserStorageRepository,
    private val userPreferencesRepository: UserPreferencesRepository
) {
    suspend operator fun invoke(uri: String?) {
        if (uri != null) {
            val permanentPath = userStorageRepository.saveAvatar(uri)
            userPreferencesRepository.updateAvatarUri(permanentPath)
        } else {
            userStorageRepository.deleteAvatar()
            userPreferencesRepository.updateAvatarUri(null)
        }
    }
}