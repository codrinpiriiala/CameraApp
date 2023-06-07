package com.example.camera2

import android.net.Uri
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.net.URI

@Parcelize
data class Photo(val id: Long, val uri: Uri): Parcelable
