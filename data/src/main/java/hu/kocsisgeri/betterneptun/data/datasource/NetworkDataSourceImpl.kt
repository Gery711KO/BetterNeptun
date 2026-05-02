package hu.kocsisgeri.betterneptun.data.datasource

import hu.kocsisgeri.betterneptun.data.api.MainApiService
import hu.kocsisgeri.betterneptun.data.model.ApiResponseDto
import hu.kocsisgeri.betterneptun.data.model.PostIdsRequestDto
import hu.kocsisgeri.betterneptun.data.model.UserAvatarDto
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

    override suspend fun getUserAvatars(userIds: List<String>) = withContext(ioDispatcher) {
        api.getUserAvatars(userIds)
    }

    override suspend fun getMessageDetails(messageId: String) =
        withContext(ioDispatcher) {
            api.getMessageDetails(messageId, messageId)
        }

    override suspend fun postMessagePostRead(
        messageId: String,
        postIds: PostIdsRequestDto
    ) {
        withContext(ioDispatcher) {
            api.postMessagePostRead(messageId, postIds)
        }
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