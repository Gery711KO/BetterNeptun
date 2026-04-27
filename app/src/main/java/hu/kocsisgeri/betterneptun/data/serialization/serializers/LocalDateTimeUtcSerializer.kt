package hu.kocsisgeri.betterneptun.data.serialization.serializers

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset

object LocalDateTimeUtcSerializer : KSerializer<LocalDateTime> {
    override val descriptor: SerialDescriptor =
        PrimitiveSerialDescriptor("LocalDateTime", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: LocalDateTime) {
        val instant = value.toInstant(ZoneOffset.UTC)
        encoder.encodeString(instant.toString()) // outputs ISO-8601 with Z
    }

    override fun deserialize(decoder: Decoder): LocalDateTime {
        val string = decoder.decodeString()
        return try {
            Instant.parse(string).atOffset(ZoneOffset.UTC).toLocalDateTime()
        } catch (e: Exception) {
            LocalDateTime.parse(string)
        }
    }
}