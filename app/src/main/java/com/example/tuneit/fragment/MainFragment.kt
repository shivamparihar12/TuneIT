package com.example.tuneit.fragment

import android.content.ComponentName
import android.content.ContentUris
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import androidx.compose.animation.core.animateDpAsState
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tuneit.MediaPlaybackService
import com.example.tuneit.R
import com.example.tuneit.adapter.SongAdapter
import com.example.tuneit.data.Song
import com.example.tuneit.databinding.FragmentMainBinding
import com.example.tuneit.viewModel.SongViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

var Flag: Boolean = false
//lateinit var playPause:ImageView

class MainFragment : Fragment() {

    private var volumeControlStream: Int = 2
    private val songViewModel: SongViewModel by viewModels()
    private val TAG: String = "MainFragment"
    private lateinit var recyclerView: RecyclerView
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var adpater: SongAdapter
//    private lateinit var mediaBrowser:MediaBrowserCompat

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        mediaBrowser= MediaBrowserCompat(
//            this,
//            ComponentName(this,MediaPlaybackService::class.java),
//            connectionCallback,
//            null
//        )

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!Flag) {
            getSongList()
            Flag = true
        }

        adpater = SongAdapter(songViewModel.songList)
        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adpater
    }

    fun getSongList() {
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )
        } else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
        )

        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val query = requireContext().contentResolver.query(
            collection,
            projection,
            null,
            null,
            sortOrder
        )

            var no = 0
            query?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
                val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val durationColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idColumn)
                    val name = cursor.getString(nameColumn)
                    val artist = cursor.getString(artistColumn)
                    val duration = cursor.getInt(durationColumn)
                    val contentUri: Uri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )
                    no += 1
                    songViewModel.songList += ((Song(contentUri, name, artist, duration)))
                    Log.d(TAG, "song fetched ${songViewModel.songList}")
                    Log.d(TAG,"total no of songs is ${no}")
                }
            }

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}