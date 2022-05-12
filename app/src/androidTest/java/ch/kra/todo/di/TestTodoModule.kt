package ch.kra.todo.di

import ch.kra.todo.core.Constants.BASE_URL
import ch.kra.todo.todo.data.remote.FakeTodoApi
import ch.kra.todo.todo.data.remote.TodoApi
import ch.kra.todo.todo.data.repository.TodoRepositoryImpl
import ch.kra.todo.todo.domain.repository.TodoRepository
import ch.kra.todo.todo.domain.use_case.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object TestTodoModule {

    @Provides
    @Singleton
    fun provideTodoApi(okHttpClient: OkHttpClient): TodoApi {
        return FakeTodoApi()
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