package com.example.tuneit.viewModel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.tuneit.data.Song

class SongViewModel : ViewModel() {

    var songList: List<Song> = emptyList()

    override fun onCleared() {
        super.onCleared()
        Log.i("SongViewModel", "SongViewModel destroyed!")
    }

}