package hu.kocsisgeri.betterneptun.utils.data_manager

import android.content.SharedPreferences
import com.squareup.moshi.Moshi
import hu.kocsisgeri.betterneptun.data.dao.MessageDao
import hu.kocsisgeri.betterneptun.utils.delete
import hu.kocsisgeri.betterneptun.utils.get
import hu.kocsisgeri.betterneptun.utils.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DataManager(
    val moshi: Moshi,
    val sharedPreferences: SharedPreferences,
    val dao: MessageDao,
    override val coroutineContext: CoroutineContext = Dispatchers.IO
): CoroutineScope {
    companion object {
        const val DEFAULT = "default"
    }

    inline fun <reified T> getData(key: String, type: Class<T>): T? {
        val json = sharedPreferences.get(key, DEFAULT)
        return if (json != DEFAULT) {
            moshi.adapter(type).fromJson(json)
        } else {
            null
        }
    }

    inline fun <reified T> putData(key: String, data: T) {
        val json = moshi.adapter(T::class.java).toJson(data)
        sharedPreferences.put(key, json)
    }

    fun deleteData(key: String) {
        sharedPreferences.delete(key)
    }

    fun purgeData() {
        sharedPreferences.edit().clear().apply()
        launch {
            dao.deleteAll()
        }
    }
}