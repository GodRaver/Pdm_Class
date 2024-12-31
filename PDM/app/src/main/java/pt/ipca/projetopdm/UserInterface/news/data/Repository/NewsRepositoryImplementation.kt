package pt.ipca.projetopdm.UserInterface.news.data.Repository

import pt.ipca.projetopdm.UserInterface.news.data.remote.NewsStore
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import pt.ipca.projetopdm.UserInterface.news.util.Resource

class NewsRepositoryImplementation(

    private val NewsStore: NewsStore
): NewsRepository {


/*  nao usar exemplo da aula

override suspend fun getTopHeadlines(category: String): Resource<List<Data>> { //Ctrl + i




    return try{
        val response = NewsStore.getBreakingNews(section = category)
        Resource.Success(response.data)
    }catch (e:Exception) {
        Resource.Error("Failed to fetch News ${e.message}")
    }



}

override suspend fun searchForNews(query: String): Resource<List<Data>> {

    return try{
        val response = NewsStore.searchForNews(query = query)
        Resource.Success(response.data)
    }catch (e:Exception) {
        Resource.Error("Failed to fetch News ${e.message}")
    }

*/

    override suspend fun getTopHeadlines(category: String): Resource<List<Data>> {
        return Resource.Success(emptyList())
    }

    override suspend fun searchForNews(query: String): Resource<List<Data>> {
        return Resource.Success(emptyList())
    }



    override suspend fun getFinanceNews(

    countries: String?,
    language: String?,
    limit: Int?,
    page: Int?
): Resource<List<Data>> {
    return try {
        val response = NewsStore.getFinanceNews(
            countries = countries,
            language = language,
            limit = limit,
            page = page
        )
        Resource.Success(response.data)
    } catch (e: Exception) {
        Resource.Error("Failed to fetch Finance News: ${e.message}")
    }
}


}

