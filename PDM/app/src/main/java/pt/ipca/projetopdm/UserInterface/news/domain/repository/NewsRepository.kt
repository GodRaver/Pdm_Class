package pt.ipca.projetopdm.UserInterface.news.domain.repository

import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
import pt.ipca.projetopdm.UserInterface.news.util.Resource
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Similar


interface NewsRepository {

    suspend fun getTopHeadlines(
        category: String
    ): Resource<List<Data>>

    suspend fun searchForNews(
        query: String
    ): Resource<List<Data>>

    suspend fun getFinanceNews(

        countries: String? = null,
        language: String? = null,
        limit: Int? = null,
        page: Int? = null
    ): Resource<List<Data>> {return Resource.Success(emptyList())

    }

}