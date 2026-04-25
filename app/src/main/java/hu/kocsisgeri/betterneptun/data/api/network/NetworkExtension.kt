package hu.kocsisgeri.betterneptun.data.api.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import hu.kocsisgeri.betterneptun.data.api.interceptor.NetworkConnectionInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Converter
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit

fun hasInternetConnection(context: Context?): Boolean {
    return context?.let { networkType(it) != NetworkType.Offline } ?: false
}

private fun networkType(context: Context): NetworkType {
    val connectivityManager = context.getSystemService(
        Context.CONNECTIVITY_SERVICE
    ) as? ConnectivityManager
    val nw = connectivityManager?.activeNetwork ?: return NetworkType.Offline
    val actNw = connectivityManager.getNetworkCapabilities(nw)
        ?: return NetworkType.Offline
    return when {
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> NetworkType.Wifi
        actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> NetworkType.Mobile
        else -> NetworkType.Offline
    }
}
