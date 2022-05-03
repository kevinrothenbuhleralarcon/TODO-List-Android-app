package ch.kra.todo.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStoreFile
import ch.kra.todo.auth.data.remote.AuthApi
import ch.kra.todo.core.Constants.BASE_URL
import ch.kra.todo.core.Constants.CONNECTION_PREFERENCE_NAME
import ch.kra.todo.core.data.local.SettingsDataStore
import ch.kra.todo.todo.data.remote.TodoApi
import ch.kra.todo.todo.data.repository.TodoRepositoryImpl
import ch.kra.todo.todo.domain.repository.TodoRepository
import ch.kra.todo.todo.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.create
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TodoModule {

    @Provides
    @Singleton
    fun provideTodoApi(okHttpClient: OkHttpClient): TodoApi {
        return Retrofit.Builder()
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()
            .create(TodoApi::class.java)
    }

    @Provides
    @Singleton
    fun provideTodoRepository(todoApi: TodoApi): TodoRepository {
        return TodoRepositoryImpl(todoApi)
    }

    @Provides
    @Singleton
    fun provideGetTodoList(todoRepository: TodoRepository): GetTodoList {
        return GetTodoList(todoRepository)
    }

    @Provides
    @Singleton
    fun provideGetTodo(todoRepository: TodoRepository): GetTodo {
        return GetTodo(todoRepository)
    }

    @Provides
    @Singleton
    fun provideAddTodo(todoRepository: TodoRepository): AddTodo {
        return AddTodo(todoRepository)
    }

    @Provides
    @Singleton
    fun provideUpdateTodo(todoRepository: TodoRepository): UpdateTodo {
        return UpdateTodo(todoRepository)
    }

    @Provides
    @Singleton
    fun provideDeleteTodo(todoRepository: TodoRepository): DeleteTodo {
        return DeleteTodo(todoRepository)
    }

    @Provides
    @Singleton
    fun provideValidateTodoTitle(): ValidateTodoTitle {
        return ValidateTodoTitle()
    }

    @Provides
    @Singleton
    fun provideValidateTaskDescription(): ValidateTaskDescription {
        return ValidateTaskDescription()
    }

    @Provides
    @Singleton
    fun provideValidateTaskEmpty(): ValidateTaskEmpty {
        return ValidateTaskEmpty()
    }
}