package ch.kra.todo.di

import ch.kra.todo.auth.data.remote.AuthApi
import ch.kra.todo.auth.data.repository.AuthRepositoryImpl
import ch.kra.todo.auth.domain.repository.AuthRepository
import ch.kra.todo.auth.domain.use_case.Login
import ch.kra.todo.auth.domain.use_case.Register
import ch.kra.todo.auth.domain.use_case.ValidatePassword
import ch.kra.todo.auth.domain.use_case.ValidateUsername
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
object AuthModule {

    @Provides
    @Singleton
    fun provideAuthApi(okHttpClient: OkHttpClient): AuthApi {
        return Retrofit.Builder()
            .client(okHttpClient)
            .baseUrl(Constants.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthApi::class.java)
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
    fun provideValidatePassword(): ValidatePassword {
        return ValidatePassword()
    }
}