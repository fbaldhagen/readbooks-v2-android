package com.fbaldhagen.readbooks.data.mapper

import com.fbaldhagen.readbooks.domain.model.TtsSettings
import org.readium.navigator.media.tts.android.AndroidTtsPreferences
import org.readium.r2.shared.ExperimentalReadiumApi

@OptIn(ExperimentalReadiumApi::class)
fun TtsSettings.toReadiumPreferences(): AndroidTtsPreferences =
    AndroidTtsPreferences(
        speed = speed.toDouble(),
        pitch = pitch.toDouble()
    )