package hu.kocsisgeri.betterneptun.domain.api.network

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl

class CustomCookieJar : CookieJar {
    private var cookies: List<Cookie>? = null

    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        return if (cookies != null) cookies!! else arrayListOf()
    }

    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        this.cookies = cookies
    }

    fun getCookies() = cookies?: listOf()
}