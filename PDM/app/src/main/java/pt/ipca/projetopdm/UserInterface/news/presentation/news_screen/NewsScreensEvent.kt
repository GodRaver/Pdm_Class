package pt.ipca.projetopdm.UserInterface.news.presentation.news_screen



import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data

sealed class NewsScreenEvent {


    data class onNewsCardClicked(val data: Data): NewsScreenEvent()
    data class onCountryChange(val countryCode: String): NewsScreenEvent()

    data class onSearchQueryChanged(val searchQuery: String): NewsScreenEvent()
    object onSearchIconClicked: NewsScreenEvent()
    object onCloseIconClicked: NewsScreenEvent()
}