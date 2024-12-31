package pt.ipca.projetopdm.UserInterface.news.presentation.news_screen



import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data

data class NewsScreenState(

    val isLoading: Boolean = false,
    val datas: List<Data> = emptyList(),
    val error: String? = null,
    val isSearchBarVisible: Boolean = false,
    val selectedArticle: Data? = null,
    val searchQuery: String = "",
    val country: String = "us"

)