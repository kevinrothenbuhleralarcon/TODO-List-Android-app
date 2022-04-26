package ch.kra.todo.di

import ch.kra.todo.auth.data.remote.AuthApi
import ch.kra.todo.auth.data.repository.AuthRepositoryImpl
import ch.kra.todo.auth.domain.repository.AuthRepository
import ch.kra.todo.auth.domain.use_case.Login
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideLogin(repository: AuthRepository): Login {
        return Login(repository)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authApi: AuthApi): AuthRepository {
        return AuthRepositoryImpl(authApi)
    }
}