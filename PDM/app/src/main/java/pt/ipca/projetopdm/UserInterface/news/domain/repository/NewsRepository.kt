package pt.ipca.projetopdm.UserInterface.news.domain.repository

import pt.ipca.projetopdm.UserInterface.news.di.RetrofitClient
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
import pt.ipca.projetopdm.UserInterface.news.util.Resource
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Similar


interface NewsRepository {



    suspend fun searchForNews(query: String): Resource<List<Data>>



    suspend fun getFinanceNews(countries: String? = null, ): Resource<List<Data>>

    }








/*

class NewsRepository {

    private val newsStore = RetrofitClient.getNewsStoreApi()

    suspend fun getFinanceNews(countries: String?): Resource<List<Data>> {
        return try {
            val response = newsStore.getFinanceNews(countries)
            if (response.data.isNotEmpty()) {
                Resource.Success(response.data)
            } else {
                Resource.Error("No data found")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to fetch data: ${e.message}")
        }
    }

    suspend fun searchForNews(query: String): Resource<List<Data>> {
        return try {
            val response = newsStore.searchForNews(query)
            if (response.data.isNotEmpty()) {
                Resource.Success(response.data)
            } else {
                Resource.Error("No data found")
            }
        } catch (e: Exception) {
            Resource.Error("Failed to fetch data: ${e.message}")
        }
    }
}
 */