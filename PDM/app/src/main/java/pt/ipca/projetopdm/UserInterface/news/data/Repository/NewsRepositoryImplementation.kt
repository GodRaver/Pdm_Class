package pt.ipca.projetopdm.UserInterface.news.data.Repository

import pt.ipca.projetopdm.UserInterface.news.data.remote.NewsStore
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import pt.ipca.projetopdm.UserInterface.news.util.Resource

class NewsRepositoryImplementation(

    private val newsStore: NewsStore
): NewsRepository {




    override suspend fun searchForNews(query: String): Resource<List<Data>> {
        return Resource.Success(emptyList())
    }



    override suspend fun getFinanceNews(countries: String?): Resource<List<Data>> {
        return try {
            // Verifica se 'countries' foi passado
            if (countries.isNullOrEmpty()) {
                return Resource.Error("O parâmetro 'countries' não pode ser nulo ou vazio.")
            }

            // Chama a API para obter notícias financeiras
            val response = newsStore.getFinanceNews(countries)

            // Verifica se a resposta contém dados e retorna o resultado
            if (response.data.isNotEmpty()) {
                Resource.Success(response.data)
            } else {
                Resource.Error("Nenhuma notícia financeira encontrada.")
            }
        } catch (e: Exception) {
            // Caso ocorra algum erro
            Resource.Error("Erro ao buscar notícias financeiras: ${e.message}")
        }
    }
}




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

//override suspend fun getTopHeadlines(category: String): Resource<List<Data>> {
//    return Resource.Success(emptyList())
// }