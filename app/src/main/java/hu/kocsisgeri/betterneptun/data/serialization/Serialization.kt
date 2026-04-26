package hu.kocsisgeri.betterneptun.data.serialization

import kotlinx.serialization.json.Json

object Serialization {

    val instance = Json {
        ignoreUnknownKeys = true
    }
}