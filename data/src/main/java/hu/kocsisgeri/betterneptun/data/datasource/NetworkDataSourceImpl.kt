package hu.kocsisgeri.betterneptun.data.datasource

import hu.kocsisgeri.betterneptun.data.api.MainApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

internal class NetworkDataSourceImpl(
    private val api: MainApiService,
    private val ioDispatcher: CoroutineDispatcher,
) : NetworkDataSource {

    override suspend fun getUserInfo() =
        withContext(ioDispatcher) {
            api.getUserInfo()
        }

    override suspend fun getUnreadMessageCount() =
        withContext(ioDispatcher) {
            api.getUnreadMessagesCount()
        }

    override suspend fun getReceivedMessages(
        firstRow: Int,
        lastRow: Int
    ) = withContext(ioDispatcher) {
        api.getReceivedMessages(
            firstRow = firstRow,
            lastRow = lastRow
        )
    }

    override suspend fun getMessageDetails(messageId: String) =
        withContext(ioDispatcher) {
            api.getMessageDetails(messageId, messageId)
        }

    override suspend fun getExtendedTerms() =
        withContext(ioDispatcher) {
            api.getExtendedTerms()
        }

    override suspend fun getTermDetails(termId: String) =
        withContext(ioDispatcher) {
            api.getTermDetails(termId)
        }

    override suspend fun getTerms() =
        withContext(ioDispatcher) {
            api.getTerms()
        }

    override suspend fun getTermAverages() =
        withContext(ioDispatcher) {
            api.getTermAverages()
        }

    override suspend fun getTakenSubjects(termId: String) =
        withContext(ioDispatcher) {
            api.getTakenSubjects(termId)
        }
}