package pt.ipca.projetopdm.UserInterface.news.presentation.news_screen



import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
import pt.ipca.projetopdm.UserInterface.news.util.Resource

data class NewsScreenState(

    val isLoading: Boolean = false,
    val datas: List<Data> = emptyList(),
    val countriesNews: Map<String, List<Data>> = emptyMap(),
    val error: String? = null,
    val isSearchBarVisible: Boolean = false,
    val selectedArticle: Data? = null,
    val searchQuery: String = "",
    val isSearchActive: Boolean = false,
    val selectedCountry: String = "us",
    val resource: Resource<List<Data>> = Resource.Loading(),

)