package com.fbaldhagen.readbooks.data.local.file

import android.content.Context
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class UserFileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val avatarsDir: File
        get() = File(context.filesDir, "avatars").also { it.mkdirs() }

    fun saveAvatar(uri: Uri): File {
        val timestamp = System.currentTimeMillis()
        val file = File(avatarsDir, "avatar_$timestamp.jpg")
        avatarsDir.listFiles()?.forEach { it.delete() }

        context.contentResolver.openInputStream(uri)?.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        return file
    }

    fun deleteAvatar() {
        File(avatarsDir, "avatar.jpg").delete()
    }
}