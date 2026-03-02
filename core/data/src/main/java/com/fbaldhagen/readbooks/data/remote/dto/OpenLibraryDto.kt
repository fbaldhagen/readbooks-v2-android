package com.fbaldhagen.readbooks.data.remote.dto

import com.squareup.moshi.FromJson
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.squareup.moshi.JsonReader
import com.squareup.moshi.ToJson

@JsonClass(generateAdapter = true)
data class OpenLibraryAuthorSearchDto(
    @Json(name = "docs") val docs: List<OpenLibraryAuthorDocDto>,
    @Json(name = "numFound") val numFound: Int
)

@JsonClass(generateAdapter = true)
data class OpenLibraryAuthorDocDto(
    @Json(name = "key") val key: String,
    @Json(name = "name") val name: String,
    @Json(name = "birth_date") val birthDate: String?,
    @Json(name = "death_date") val deathDate: String?,
    @Json(name = "work_count") val workCount: Int?
)

@JsonClass(generateAdapter = true)
data class OpenLibraryAuthorDto(
    @Json(name = "key") val key: String,
    @Json(name = "name") val name: String,
    @Json(name = "bio") val bio: String?,
    @Json(name = "birth_date") val birthDate: String?,
    @Json(name = "death_date") val deathDate: String?
)

@JsonClass(generateAdapter = true)
data class OpenLibraryBioDto(
    @Json(name = "value") val value: String?
)

class OpenLibraryBioAdapter {
    @FromJson
    fun fromJson(reader: JsonReader): String? {
        return when (reader.peek()) {
            JsonReader.Token.STRING -> reader.nextString()
            JsonReader.Token.BEGIN_OBJECT -> {
                var value: String? = null
                reader.beginObject()
                while (reader.hasNext()) {
                    if (reader.nextName() == "value") {
                        value = reader.nextString()
                    } else {
                        reader.skipValue()
                    }
                }
                reader.endObject()
                value
            }
            else -> {
                reader.skipValue()
                null
            }
        }
    }

    @ToJson
    fun toJson(value: String?): String? = value
}