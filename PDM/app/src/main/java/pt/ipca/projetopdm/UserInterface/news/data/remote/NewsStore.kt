package pt.ipca.projetopdm.UserInterface.news.data.remote


import pt.ipca.projetopdm.UserInterface.news.domain.Model.NewsResponse
import retrofit2.http.Query
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

interface NewsStore {

    @GET("news/all")
    suspend fun getFinanceNews(

        @Query("countries") countries: String? = null,
        @Query("api_token") apikey: String = API_KEY
    ): NewsResponse


 @GET("entity/search")
 suspend fun searchForNews(
     @Query("query") query: String,        // Palavra-chave para buscar notícias
     @Query("api_token") apikey: String = API_KEY
 ): NewsResponse



 companion object {



     const val BASE_URL = "https://api.marketaux.com/v1/"
     const val API_KEY = "AiQhjlSoYtdswxtnE2lhQT59XChlhqtnWeCuLrXs"


 }

}

//@Query("api_token") apiToken: String = API_KEY,
//@Query("symbols") symbols: String? = null,
//@Query("entity_types") entityTypes: String? = null,
//@Query("industries") industries: String? = null,
//@Query("sentiment_gte") sentimentGte: Double? = null,
//@Query("sentiment_lte") sentimentLte: Double? = null,
//@Query("min_match_score") minMatchScore: Double? = null,
//@Query("filter_entities") filterEntities: Boolean? = null,
//@Query("must_have_entities") mustHaveEntities: Boolean? = null,
//@Query("group_similar") groupSimilar: Boolean? = null,
//@Query("search") search: String? = null,
//@Query("domains") domains: String? = null,
//@Query("exclude_domains") excludeDomains: String? = null,
//@Query("source_ids") sourceIds: String? = null,
//@Query("exclude_source_ids") excludeSourceIds: String? = null,
//@Query("published_before") publishedBefore: String? = null,
//@Query("published_after") publishedAfter: String? = null,
//@Query("sort") sort: String? = null,
//@Query("sort_order") sortOrder: String? = null,


/*

    suspend fun getFinanceNews(countries: String?): NewsResponse {
        val urlString = "$BASE_URL/news/all?countries=$countries&api_token=$API_KEY"
        return fetchNews(urlString)
    }

    private fun fetchNews(urlString: String): NewsResponse {
        val url = URL(urlString)
        var connection: HttpURLConnection? = null
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.setRequestProperty("Content-Type", "application/json")

            val inputStream = connection.inputStream
            val reader = InputStreamReader(inputStream)
            val response = reader.readText()

            return NewsResponse(data = emptyList())  // Exemplo simplificado
        } catch (e: Exception) {
            return NewsResponse(error = "Falha na requisição: ${e.message}")
        } finally {
            connection?.disconnect()
        }
    }
     */


/* não usar exemplo da aula


 @GET("news/all")
 suspend fun getBreakingNews(
     @Query("categories") section: String, // "business", "technology", etc.
     @Query("api_token") apikey: String = API_KEY
 ): NewsResponse


 */