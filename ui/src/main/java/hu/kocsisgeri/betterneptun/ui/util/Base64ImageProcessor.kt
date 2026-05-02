package hu.kocsisgeri.betterneptun.ui.util

import android.util.Base64

fun decodeBase64ToBitmap(base64String: String): ByteArray? {
    return try {
        // A Base64 string dekódolása bájtokká
        Base64.decode(base64String, Base64.DEFAULT)
    } catch (e: Exception) {
        null
    }
}