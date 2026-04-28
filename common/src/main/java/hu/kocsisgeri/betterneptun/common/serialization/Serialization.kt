package hu.kocsisgeri.betterneptun.common.serialization

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import hu.kocsisgeri.betterneptun.common.serialization.serializers.LocalDateTimeUtcSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import okhttp3.MediaType.Companion.toMediaType
import java.time.LocalDateTime

object Serialization {

    val instance = Json {
        ignoreUnknownKeys = true
        serializersModule = SerializersModule {
            contextual(LocalDateTime::class, LocalDateTimeUtcSerializer)
        }
    }

    val converterFactory get() = instance
        .asConverterFactory("application/json".toMediaType())
}