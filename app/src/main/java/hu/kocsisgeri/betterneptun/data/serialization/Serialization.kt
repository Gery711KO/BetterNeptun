package hu.kocsisgeri.betterneptun.data.serialization

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType

object Serialization {

    val instance = Json {
        ignoreUnknownKeys = true
    }

    val converterFactory get() = instance
        .asConverterFactory("application/json".toMediaType())
}