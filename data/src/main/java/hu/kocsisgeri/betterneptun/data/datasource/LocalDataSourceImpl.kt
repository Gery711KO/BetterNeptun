package hu.kocsisgeri.betterneptun.data.datasource

import android.content.SharedPreferences
import androidx.core.content.edit
import hu.kocsisgeri.betterneptun.data.dao.AppDatabase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class LocalDataSourceImpl(
    override val appDatabase: AppDatabase,
    override val cache: SharedPreferences,
    private val ioDispatcher: CoroutineDispatcher,
): LocalDataSource {

    override suspend fun purge() {
        withContext(ioDispatcher) {
            cache.edit { clear() }
            appDatabase.localEvents.deleteAll()
        }
    }
}