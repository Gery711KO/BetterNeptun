package hu.kocsisgeri.betterneptun.permission

import android.app.Activity
import android.content.SharedPreferences
import hu.kocsisgeri.betterneptun.common.utils.isBefore
import hu.kocsisgeri.betterneptun.common.utils.now
import hu.kocsisgeri.betterneptun.common.utils.plus
import hu.kocsisgeri.betterneptun.common.utils.serialization.Serialization
import hu.kocsisgeri.betterneptun.data.util.get
import hu.kocsisgeri.betterneptun.data.util.put
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
import kotlinx.datetime.LocalDateTime
import org.koin.core.annotation.Singleton
import kotlin.time.Duration.Companion.days

@Singleton
class PermissionHandlerImpl(
    private val sharedPreferences: SharedPreferences,
    private val handledPermissions: List<PermissionData>,
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
                handledPermissions.filter { permission ->
                    val dismissedUntil = sharedPreferences.get<String?>(
                        key = permission.dismissedUntil,
                        defaultValue = null
                    )?.let {
                        Serialization.instance.decodeFromString<LocalDateTime>(it)
                    }

                    if (dismissedUntil != null) {
                        LocalDateTime.now().isBefore(dismissedUntil).not()
                    } else {
                        true
                    }
                }.map {
                    it.apply { refreshPermissionState(activity) }
                }
            )
        }.onEach {
            _permissions.value = it
        }

    override fun refreshPermissions() {
        refresher.tryEmit(Unit)
    }

    override fun dismissPermission(permission: PermissionData) {
        val until = LocalDateTime.now().plus(30.days)
        sharedPreferences.put(
            permission.dismissedUntil,
            Serialization.instance.encodeToString(until)
        )
        refreshPermissions()
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
