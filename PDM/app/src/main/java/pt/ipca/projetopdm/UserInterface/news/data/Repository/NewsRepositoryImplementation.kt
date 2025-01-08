package pt.ipca.projetopdm.UserInterface.news.data.Repository

import android.util.Log
import pt.ipca.projetopdm.UserInterface.news.data.remote.NewsStore
import pt.ipca.projetopdm.UserInterface.news.data.remote.NewsStore.Companion.API_KEY
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import pt.ipca.projetopdm.UserInterface.news.util.Resource

class NewsRepositoryImplementation(

    private val newsStore: NewsStore
): NewsRepository {


    override suspend fun searchForNews(query: String): Resource<List<Data>> {
        //return Resource.Success(emptyList())
        return try {
            // Chama a API para buscar notícias com a palavra-chave
            val response = newsStore.searchForNews(query)

            // Verifica se a resposta contém dados e retorna o resultado
            if (response.data.isNotEmpty()) {
                Resource.Success(response.data)
            } else {
                Resource.Error("Nenhuma notícia encontrada para a consulta.")
            }
        } catch (e: Exception) {
            // Caso ocorra algum erro
            Resource.Error("Erro ao buscar notícias: ${e.message}")
        }
    }


    override suspend fun getFinanceNews(countries: String?): Resource<List<Data>> {
        return try {

            Log.d("getFinanceNews", "Chamando API com o país: $countries")
            val response = newsStore.getFinanceNews(countries ?: "us")
            // Verifica se 'countries' foi passado
            if (response.data.isNotEmpty()) {
                Log.d(
                    "getFinanceNews",
                    "Notícias carregadas com sucesso: ${response.data.size} artigos"
                )
                Resource.Success(response.data)
            } else {
                Log.e("getFinanceNews", "Nenhuma notícia encontrada.")
                Resource.Error("Nenhuma notícia financeira encontrada.")
            }

        } catch (e: Exception) {
            Log.e("getFinanceNews", "Erro: ${e.message}")
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

*/





//override suspend fun getTopHeadlines(category: String): Resource<List<Data>> {
//    return Resource.Success(emptyList())
// }