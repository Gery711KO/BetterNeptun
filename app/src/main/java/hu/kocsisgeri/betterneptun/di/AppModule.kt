package hu.kocsisgeri.betterneptun.di

import hu.kocsisgeri.betterneptun.BuildConfig
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Configuration
import org.koin.core.annotation.Factory
import org.koin.core.annotation.KoinApplication
import org.koin.core.annotation.Module

@KoinApplication
object App

@Module
@Configuration
@ComponentScan(BuildConfig.NAMESPACE)
class AppModule {

    @Factory
    fun provideIoDispatcher(): CoroutineDispatcher = Dispatchers.IO
}
