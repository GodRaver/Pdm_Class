package pt.ipca.projetopdm.UserInterface.news.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import pt.ipca.projetopdm.UserInterface.news.data.Repository.NewsRepositoryImplementation
import pt.ipca.projetopdm.UserInterface.news.data.remote.NewsStore
import pt.ipca.projetopdm.UserInterface.news.data.remote.NewsStore.Companion.BASE_URL
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NewsAppModule {

    @Provides
    @Singleton
    fun provideNewsApi(): NewsStore {

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(NewsStore::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsRepository(newsStore : NewsStore): NewsRepository {

        return NewsRepositoryImplementation(newsStore)
    }


}