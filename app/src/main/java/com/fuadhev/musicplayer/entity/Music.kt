package com.fuadhev.musicplayer.entity

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class Music(
    val m_name: String,
    val artist_name: String,
    val album: String,
    val path: String,
    val m_img: String,
    val duration: Long
): Parcelable {
}