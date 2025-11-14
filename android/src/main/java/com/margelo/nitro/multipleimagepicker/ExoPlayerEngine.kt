package com.margelo.nitro.multipleimagepicker

import android.content.Context
import android.view.View
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.PlaybackException
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.StyledPlayerView
import com.luck.picture.lib.interfaces.OnPlayerListener

class ExoPlayerEngine : OnPlayerListener {

    private var player: ExoPlayer? = null

    override fun onStarPlayer(playerView: StyledPlayerView?, url: String?) {
        val view = playerView ?: return
        val videoUrl = url ?: return

        try {
            val mediaItem = when {
                videoUrl.startsWith("http://") -> MediaItem.fromUri(videoUrl)
                videoUrl.startsWith("https://") -> MediaItem.fromUri(videoUrl)
                else -> MediaItem.fromUri(videoUrl)
            }

            player?.let { exoPlayer ->
                exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onResume(playerView: StyledPlayerView?) {
        player?.play()
    }

    override fun onPause(playerView: StyledPlayerView?) {
        player?.pause()
    }

    override fun isPlaying(playerView: StyledPlayerView?): Boolean {
        return player?.isPlaying ?: false
    }

    override fun onPlayerAttachedToWindow(playerView: StyledPlayerView?) {
        val view = playerView ?: return
        val context = view.context ?: return

        try {
            val exoPlayer: Player = ExoPlayer.Builder(context).build()
            view.player = exoPlayer
            player = exoPlayer as? ExoPlayer
            exoPlayer.addListener(playerListener)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onPlayerDetachedFromWindow(playerView: StyledPlayerView?) {
        player?.let { exoPlayer ->
            try {
                exoPlayer.removeListener(playerListener)
                exoPlayer.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        player = null
    }

    override fun destroy(playerView: StyledPlayerView?) {
        player?.let { exoPlayer ->
            try {
                exoPlayer.removeListener(playerListener)
                exoPlayer.release()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val playerListener: Player.Listener = object : Player.Listener {
        override fun onPlayerError(error: PlaybackException) {
            super.onPlayerError(error)
            error.printStackTrace()
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            super.onPlaybackStateChanged(playbackState)
            when (playbackState) {
                Player.STATE_IDLE -> {
                    // Player is idle
                }
                Player.STATE_READY -> {
                    // Player is ready
                }
            }
        }
    }
}
