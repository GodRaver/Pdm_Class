package pt.ipca.projetopdm.UserInterface.news.presentation.news_screen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import pt.ipca.projetopdm.UserInterface.news.util.Resource
import javax.inject.Inject

@HiltViewModel
class NewsScreenViewModel @Inject constructor(
    private val newsRepository: NewsRepository
) : ViewModel() {

    var state by mutableStateOf(NewsScreenState())

    private var searchJob: Job? = null

    //  init {
    //     getNewsArticles(category = "home")
    // }

    fun onEvent(event: NewsScreenEvent) {
        when (event) {
            is NewsScreenEvent.onCountryChange -> {
                state = state.copy(country = event.countryCode)
                getNewsArticlesByCountry(state.country)
            }


            NewsScreenEvent.onCloseIconClicked -> {
                state = state.copy(isSearchBarVisible = false)
                getNewsArticlesByCountry(state.country)
            }
            is NewsScreenEvent.onNewsCardClicked -> {
                state = state.copy(selectedArticle = event.data)
            }
            NewsScreenEvent.onSearchIconClicked -> {
                state = state.copy(isSearchBarVisible = true, datas = emptyList())
            }
           // is NewsScreenEvent.onSearchQueryChanged -> {
           //     state = state.copy(searchQuery = event.searchQuery)
           //     searchJob?.cancel()
           //     searchJob = viewModelScope.launch {

          //          delay(1000)
           //         searchForNews(query = state.searchQuery)
           //     }
          //  }
        }
    }

    private fun getNewsArticlesByCountry(countries: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            val result = newsRepository.getFinanceNews(countries = countries)  // Mudança aqui
            when (result) {
                is Resource.Success -> {
                    state = state.copy(
                        datas = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        error = result.message,
                        isLoading = false,
                        datas = emptyList()
                    )
                }
            }
        }
    }

    /* private fun searchForNews(query: String) {
        if(query.isEmpty()) {

            return
        }
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            val result = newsRepository.searchForNews(query = query)
            when (result) {
                is Resource.Success -> {
                    state = state.copy(
                        datas = result.data ?: emptyList(),
                        isLoading = false,
                        error = null
                    )
                }
                is Resource.Error -> {
                    state = state.copy(
                        error = result.message,
                        isLoading = false,
                        datas = emptyList()
                    )
                }
            }
        }


   }
   */


}