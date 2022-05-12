package ch.kra.todo.di

import ch.kra.todo.auth.data.remote.AuthApi
import ch.kra.todo.auth.data.remote.FakeAuthApi
import ch.kra.todo.auth.data.repository.AuthRepositoryImpl
import ch.kra.todo.auth.domain.repository.AuthRepository
import ch.kra.todo.auth.domain.use_case.*
import ch.kra.todo.core.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestAuthModule {

    @Provides
    @Singleton
    fun provideAuthApi(okHttpClient: OkHttpClient): AuthApi {
        return FakeAuthApi()
    }

    @Provides
    @Singleton
    fun provideAuthRepository(authApi: AuthApi): AuthRepository {
        return AuthRepositoryImpl(authApi)
    }

    @Provides
    @Singleton
    fun provideLogin(repository: AuthRepository): Login {
        return Login(repository)
    }

    @Provides
    @Singleton
    fun provideRegister(repository: AuthRepository): Register {
        return Register(repository)
    }

    @Provides
    @Singleton
    fun provideValidateUsername(): ValidateUsername{
        return ValidateUsername()
    }

    @Provides
    @Singleton
    fun provideValidatePassword(): ValidateLoginPassword {
        return ValidateLoginPassword()
    }

    @Provides
    @Singleton
    fun provideValidateRegisterPassword(): ValidateRegisterPassword {
        return ValidateRegisterPassword()
    }

    @Provides
    @Singleton
    fun provideValidateRepeatedRegisterPassword(): ValidateRepeatedRegisterPassword {
        return ValidateRepeatedRegisterPassword()
    }

    @Provides
    @Singleton
    fun provideValidateEmail(): ValidateEmail {
        return ValidateEmail()
    }
}