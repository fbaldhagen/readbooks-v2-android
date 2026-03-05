package com.fbaldhagen.readbooks.data.mapper

import com.fbaldhagen.readbooks.domain.model.DomainLocator
import com.fbaldhagen.readbooks.domain.model.TtsPlaybackState
import com.fbaldhagen.readbooks.domain.model.TtsSettings
import com.fbaldhagen.readbooks.domain.model.TtsUtterance
import com.fbaldhagen.readbooks.domain.model.TtsVoice
import org.readium.navigator.media.tts.TtsNavigator
import org.readium.r2.shared.ExperimentalReadiumApi
import org.readium.r2.shared.publication.Locator

@OptIn(ExperimentalReadiumApi::class)
fun TtsNavigator.Playback.toDomain(settings: TtsSettings): TtsPlaybackState {
    val utterance = TtsUtterance(
        text = utterance,
        locator = DomainLocator(href = "")
    )
    return when (val s = state) {
        is TtsNavigator.State.Ended -> TtsPlaybackState.Idle
        is TtsNavigator.State.Failure -> TtsPlaybackState.Error(s.error.message)
        is TtsNavigator.State.Ready -> if (playWhenReady)
            TtsPlaybackState.Playing(utterance, settings)
        else
            TtsPlaybackState.Paused(utterance, settings)
        else -> TtsPlaybackState.Paused(utterance, settings)
    }
}

fun Locator.toDomain(): DomainLocator = DomainLocator(
    href = href.toString(),
    progression = locations.progression ?: 0.0
)

fun android.speech.tts.Voice.toDomain(): TtsVoice = TtsVoice(
    id = name,
    name = name,
    language = locale.displayLanguage
)