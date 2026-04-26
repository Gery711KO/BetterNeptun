package hu.kocsisgeri.betterneptun.data.datamanager

import android.content.SharedPreferences
import hu.kocsisgeri.betterneptun.data.dao.ColorDao
import hu.kocsisgeri.betterneptun.data.dao.MessageDao
import hu.kocsisgeri.betterneptun.utils.delete
import hu.kocsisgeri.betterneptun.utils.get
import hu.kocsisgeri.betterneptun.utils.put
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class DataManager(
    val sharedPreferences: SharedPreferences,
    val messages: MessageDao,
    val colors: ColorDao,
): CoroutineScope {

    override val coroutineContext: CoroutineContext = Dispatchers.IO

    inline fun <reified T> getDefault(key: String, default: T) : T {
        return sharedPreferences.get(key, default)
    }

    inline fun <reified T> putData(key: String, data: T) {
        sharedPreferences.put(key, data)
    }

    fun deleteData(key: String) {
        sharedPreferences.delete(key)
    }

    fun purgeData() {
        sharedPreferences.edit().clear().apply()
        launch {
            messages.deleteAll()
            colors.deleteAll()
        }
    }
}