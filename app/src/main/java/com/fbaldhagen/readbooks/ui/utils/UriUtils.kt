package com.fbaldhagen.readbooks.ui.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

fun Context.createTempImageUri(): Uri {
    val file = File.createTempFile("avatar_", ".jpg", cacheDir).apply {
        createNewFile()
    }
    return FileProvider.getUriForFile(this, "${packageName}.provider", file)
}