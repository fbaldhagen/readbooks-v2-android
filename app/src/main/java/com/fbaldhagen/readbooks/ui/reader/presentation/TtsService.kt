package com.fbaldhagen.readbooks.ui.reader.presentation

import android.content.Intent
import androidx.annotation.OptIn
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.DefaultMediaNotificationProvider
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.fbaldhagen.readbooks.R
import com.fbaldhagen.readbooks.data.tts.TtsController
import com.fbaldhagen.readbooks.domain.model.isActive
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class TtsService : MediaSessionService() {

    @Inject
    lateinit var ttsController: TtsController
    private var mediaSession: MediaSession? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    @OptIn(UnstableApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)

        if (mediaSession == null) {
            NotificationCompat.Builder(
                this,
                DefaultMediaNotificationProvider.DEFAULT_CHANNEL_ID
            )
                .setContentTitle("Text to Speech")
                .setSmallIcon(R.drawable.ic_notification)
                .build()
                .let {
                    startForeground(DefaultMediaNotificationProvider.DEFAULT_NOTIFICATION_ID, it)
                }
            ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            stopSelf(startId)
        }

        return START_NOT_STICKY
    }

    @OptIn(UnstableApi::class)
    override fun onCreate() {
        super.onCreate()

        val player = ttsController.getMedia3Player()
        if (player == null) {
            stopSelf()
            return
        }

        mediaSession = MediaSession.Builder(this, player)
            .setId("ReadBooksTts")
            .setCallback(object : MediaSession.Callback {
                override fun onConnect(
                    session: MediaSession,
                    controller: MediaSession.ControllerInfo
                ): MediaSession.ConnectionResult {
                    return MediaSession.ConnectionResult.AcceptedResultBuilder(session)
                        .setAvailablePlayerCommands(
                            MediaSession.ConnectionResult.DEFAULT_PLAYER_COMMANDS.buildUpon()
                                .add(Player.COMMAND_SEEK_TO_NEXT)
                                .add(Player.COMMAND_SEEK_TO_PREVIOUS)
                                .build()
                        )
                        .build()
                }
            })
            .build()

        addSession(mediaSession!!)

        serviceScope.launch {
            ttsController.ttsState.collect { state ->
                if (!state.isActive) stopSelf()
            }
        }
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    override fun onDestroy() {
        mediaSession?.release()
        mediaSession = null
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        serviceScope.launch { ttsController.stop() }
        stopSelf()
    }
}