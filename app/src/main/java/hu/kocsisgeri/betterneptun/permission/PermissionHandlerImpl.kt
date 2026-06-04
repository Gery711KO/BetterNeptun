package hu.kocsisgeri.betterneptun.permission

import android.app.Activity
import hu.kocsisgeri.betterneptun.ui.core.permission.PermissionHandler
import hu.kocsisgeri.betterneptun.ui.core.permission.model.PermissionData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import org.koin.core.annotation.Provided
import org.koin.core.annotation.Singleton

@Singleton
class PermissionHandlerImpl(
    @Provided private val handledPermissions: List<PermissionData>,
) : PermissionHandler {

    private var isConfigured = false
    private val refresher = MutableSharedFlow<Unit>(1, 10)

    private val _permissions = MutableStateFlow<List<PermissionData>>(emptyList())

    override val permissions: StateFlow<List<PermissionData>>
        get() {
            check(isConfigured) { ERROR_MESSAGE }

            return _permissions.asStateFlow()
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPermissions(activity: Activity) = refresher
        .onStart {
            isConfigured = true
            emit(Unit)
        }
        .flatMapLatest {
            flowOf(
                handledPermissions.map {
                    it.apply { refreshPermissionState(activity) }
                }
            )
        }.onEach {
            _permissions.value = it
        }

    override fun refreshPermissions() {
        refresher.tryEmit(Unit)
    }

    companion object {
        private val ERROR_MESSAGE = """
            ====================================================================
            PERMISSION HANDLER ERROR
            ====================================================================
            You are accessing 'permissions' StateFlow before it is configured!
                
            ACTION REQUIRED:
            You must collect 'getPermissions(activity)' FIRST from your activity
            before accessing this property.
            ====================================================================
        """.trimIndent()
    }
}