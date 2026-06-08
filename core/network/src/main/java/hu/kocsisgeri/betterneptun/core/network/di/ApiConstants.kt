package hu.kocsisgeri.betterneptun.core.network.di

import hu.kocsisgeri.betterneptun.core.network.BuildConfig

internal object ApiConstants {

    const val BASE_URL = "https://neptun.uni-obuda.hu/ujhallgato/api/"
    const val LOCALIZATION_BASE_URL = "https://cdn.simplelocalize.io/${BuildConfig.LOCALIZATION_TOKEN}/${BuildConfig.LOCALIZATION_SNAPSHOT}/"
}