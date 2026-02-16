package com.fbaldhagen.readbooks.common.result

sealed interface AppError {

    val message: String

    // region Network
    data class Network(
        override val message: String = "Network error",
        val cause: Throwable? = null
    ) : AppError

    data object NoInternet : AppError {
        override val message: String = "No internet connection"
    }
    // endregion

    // region Database
    data class Database(
        override val message: String = "Database error",
        val cause: Throwable? = null
    ) : AppError
    // endregion

    // region File
    data class FileError(
        override val message: String = "File operation failed",
        val cause: Throwable? = null
    ) : AppError

    data object FileNotFound : AppError {
        override val message: String = "File not found"
    }
    // endregion

    // region Book-specific
    data object BookAlreadyExists : AppError {
        override val message: String = "Book already exists in library"
    }

    data object InvalidEpub : AppError {
        override val message: String = "Invalid EPUB file"
    }

    data object DownloadFailed : AppError {
        override val message: String = "Download failed"
    }
    // endregion

    // region Generic
    data class Unknown(
        override val message: String = "An unexpected error occurred",
        val cause: Throwable? = null
    ) : AppError
    // endregion
}

fun AppError.toException(): Exception = when (this) {
    is AppError.Network -> Exception(message, cause)
    is AppError.NoInternet -> Exception(message)
    is AppError.Database -> Exception(message, cause)
    is AppError.FileError -> Exception(message, cause)
    is AppError.FileNotFound -> Exception(message)
    is AppError.BookAlreadyExists -> Exception(message)
    is AppError.InvalidEpub -> Exception(message)
    is AppError.DownloadFailed -> Exception(message)
    is AppError.Unknown -> Exception(message, cause)
}

fun Throwable.toAppError(): AppError = when (this) {
    is java.net.UnknownHostException -> AppError.NoInternet
    is java.net.SocketTimeoutException -> AppError.Network("Connection timed out", this)
    is java.io.FileNotFoundException -> AppError.FileNotFound
    is java.io.IOException -> AppError.FileError(message ?: "IO error", this)
    else -> AppError.Unknown(message ?: "An unexpected error occurred", this)
}