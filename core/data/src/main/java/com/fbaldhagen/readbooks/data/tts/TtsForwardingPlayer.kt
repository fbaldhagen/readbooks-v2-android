package com.fbaldhagen.readbooks.data.tts

import androidx.annotation.OptIn
import androidx.media3.common.ForwardingPlayer
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi

@OptIn(UnstableApi::class)
class TtsForwardingPlayer(
    player: Player,
    private val onSkipToNext: () -> Unit,
    private val onSkipToPrevious: () -> Unit
) : ForwardingPlayer(player) {

    /**
     * Advertise that skip-next and skip-previous are available,
     * so the MediaSession notification renders the buttons as enabled.
     */
    override fun getAvailableCommands(): Player.Commands {
        return super.getAvailableCommands().buildUpon()
            .addAll(
                COMMAND_SEEK_TO_NEXT,
                COMMAND_SEEK_TO_PREVIOUS,
                COMMAND_SEEK_TO_NEXT_MEDIA_ITEM,
                COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM
            )
            .build()
    }

    override fun isCommandAvailable(command: Int): Boolean {
        return when (command) {
            COMMAND_SEEK_TO_NEXT,
            COMMAND_SEEK_TO_PREVIOUS,
            COMMAND_SEEK_TO_NEXT_MEDIA_ITEM,
            COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM -> true
            else -> super.isCommandAvailable(command)
        }
    }

    override fun seekToNext() {
        onSkipToNext()
    }

    override fun seekToNextMediaItem() {
        onSkipToNext()
    }

    override fun seekToPrevious() {
        onSkipToPrevious()
    }

    override fun seekToPreviousMediaItem() {
        onSkipToPrevious()
    }
}