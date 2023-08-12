package com.fuadhev.musicplayer.entity

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity(tableName = "Music")
@Parcelize
data class Music(
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "n_name")
    var m_name: String,
    @ColumnInfo(name = "artist_name")val artist_name: String,
    @ColumnInfo(name = "album")val album: String,
    @ColumnInfo(name = "path")val path: String,
    @ColumnInfo(name = "m_img")val m_img: String,
    @ColumnInfo(name = "lastPlayTime") var lastPlayTime: Long
): Parcelable {
}