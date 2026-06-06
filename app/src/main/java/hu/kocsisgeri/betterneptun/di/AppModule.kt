package hu.kocsisgeri.betterneptun.di

import hu.kocsisgeri.betterneptun.BuildConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Factory
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@KoinApplication
object App

@Module
@Configuration
@ComponentScan(BuildConfig.NAMESPACE)
class AppModule {

    @Factory
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Single
    fun provideApplicationScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
}
