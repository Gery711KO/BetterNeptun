package hu.kocsisgeri.betterneptun.common.utils.serialization

import kotlinx.serialization.json.Json

object Serialization {

    val instance = Json {
        ignoreUnknownKeys = true
        explicitNulls = true
    }
}
