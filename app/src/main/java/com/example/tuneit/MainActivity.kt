package com.example.tuneit

import android.content.ComponentName
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.ImageView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.tuneit.fragment.Flag
import com.example.tuneit.fragment.MainFragment


class MainActivity : AppCompatActivity() {

    lateinit var playPause: ImageView
    private lateinit var navController: NavController
    private lateinit var mediaBrowser:MediaBrowserCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController
        setupActionBarWithNavController(navController)

        mediaBrowser= MediaBrowserCompat(
            this,
            ComponentName(applicationContext,MediaPlaybackService::class.java),
            connectionCallback,
            null
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }


    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            mediaBrowser.sessionToken.also { token ->
                val mediaController = MediaControllerCompat(
                    this@MainActivity,
                    token
                )

                MediaControllerCompat.setMediaController(this@MainActivity, mediaController)
            }
            buildTransportControls()
        }

        override fun onConnectionSuspended() {
            super.onConnectionSuspended()
            // The Service has crashed. Disable transport controls until it automatically reconnects
        }

        override fun onConnectionFailed() {
            super.onConnectionFailed()
            // The Service has refused our connection
        }
    }

    fun buildTransportControls(){
        val mediaController = MediaControllerCompat.getMediaController(this)
        playPause = findViewById<ImageView>(R.id.play_pause).apply {
            setOnClickListener {
                val pbSatate = mediaController.playbackState.state
                if (pbSatate==PlaybackStateCompat.STATE_PLAYING){
                    mediaController.transportControls.pause()
                    playPause.setImageResource(R.drawable.ic_outline_play_arrow_24)
                }
                else{
                    mediaController.transportControls.play()
                    playPause.setImageResource(R.drawable.ic_baseline_pause_24)
                }
            }
        }

        val metadata = mediaController.metadata
        val pbState = mediaController.playbackState

        mediaController.registerCallback(controllerCallback)
    }

    private var controllerCallback=object :MediaControllerCompat.Callback(){
        override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
            super.onMetadataChanged(metadata)
        }

        override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
            super.onPlaybackStateChanged(state)
        }
    }

    override fun onStart() {
        super.onStart()
        mediaBrowser.connect()
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream= AudioManager.STREAM_MUSIC
    }

    override fun onDestroy() {
        super.onDestroy()
        Flag=true
    }
}
