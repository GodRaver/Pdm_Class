package pt.ipca.projetopdm.UserInterface.news.presentation.news_screen

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import pt.ipca.projetopdm.UserInterface.news.util.Resource
import javax.inject.Inject

/*
class NewsScreenViewModel(
    private val newsRepository: NewsRepository,
    private val auth: FirebaseAuth,

) : ViewModel() {

    var state by mutableStateOf(NewsScreenState())
    private set

    val currentUser = auth.currentUser


    private var searchJob: Job? = null

      init {
        getNewsArticlesByCountry("us")
     }

    fun onEvent(event: NewsScreenEvent) {
        when (event) {
            is NewsScreenEvent.onCountryChange -> {
                state = state.copy(selectedCountry = event.countryCode)
                getNewsArticlesByCountry(event.countryCode)
            }


            NewsScreenEvent.onCloseIconClicked -> {
                state = state.copy(isSearchBarVisible = false)
                getNewsArticlesByCountry(state.selectedCountry)
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

    private fun getNewsArticlesByCountry(country: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            // Verifica se as notícias já foram carregadas para o país selecionado
            val cachedNews = state.countriesNews[country]

            if (cachedNews != null) {
                // Se já houver notícias para esse país, só atualiza o estado
                state = state.copy(
                    datas = cachedNews,
                    isLoading = false,
                    error = null
                )
            } else {
                // Se não houver notícias para esse país, faz a requisição
                val result = newsRepository.getFinanceNews(countries = country)
                when (result) {
                    is Resource.Success -> {
                        // Atualiza o mapa com as notícias do país selecionado
                        val updatedCountriesNews = state.countriesNews.toMutableMap()
                        updatedCountriesNews[country] = result.data ?: emptyList()

                        state = state.copy(
                            countriesNews = updatedCountriesNews,
                            datas = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        state = state.copy(
                            error = result.message,
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {
                        state = state.copy(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }
}


 */




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



class NewsScreenViewModel(
    private val newsRepository: NewsRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    var state by mutableStateOf(NewsScreenState())
        private set

    val currentUser = auth.currentUser

    // Job para cancelar a pesquisa em andamento
    private var searchJob: Job? = null

    init {
        // Carregar as notícias do país default 'us' na inicialização
        getNewsArticlesByCountry("us")
    }

    fun onEvent(event: NewsScreenEvent) {
        when (event) {
            is NewsScreenEvent.onCountryChange -> {
                state = state.copy(selectedCountry = event.countryCode)
                getNewsArticlesByCountry(event.countryCode)
            }

            NewsScreenEvent.onCloseIconClicked -> {
                state = state.copy(isSearchBarVisible = false)
                getNewsArticlesByCountry(state.selectedCountry)
            }

            is NewsScreenEvent.onNewsCardClicked -> {
                state = state.copy(selectedArticle = event.data)
            }

            NewsScreenEvent.onSearchIconClicked -> {
                state = state.copy(isSearchBarVisible = true, datas = emptyList())
            }

            // Descomentando o evento de mudança da query de pesquisa
            is NewsScreenEvent.onSearchQueryChanged -> {
                // Atualizando o estado com a nova query
                state = state.copy(searchQuery = event.searchQuery)

                // Cancelando qualquer job anterior de busca
                searchJob?.cancel()

                // Iniciando um novo job para pesquisa com delay (debounce)
                searchJob = viewModelScope.launch {
                    delay(1000)  // Atraso de 1 segundo para permitir que o usuário termine de digitar
                    searchForNews(query = state.searchQuery)
                }
            }
        }
    }

    // Função de busca de notícias por país
    private fun getNewsArticlesByCountry(country: String) {
        viewModelScope.launch {
            state = state.copy(isLoading = true, error = null)
            // Verifica se as notícias já foram carregadas para o país selecionado
            val cachedNews = state.countriesNews[country]

            if (cachedNews != null) {
                // Se já houver notícias para esse país, atualiza o estado
                state = state.copy(
                    datas = cachedNews,
                    isLoading = false,
                    error = null
                )
            } else {
                // Se não houver notícias para esse país, faz a requisição
                Log.d("API Request", "Requisição para o país: $country")
                val result = newsRepository.getFinanceNews(countries = country)
                when (result) {
                    is Resource.Success -> {
                        // Atualiza o mapa com as notícias do país selecionado
                        Log.d("API Response", "Notícias recebidas para $country: ${result.data}")
                        val updatedCountriesNews = state.countriesNews.toMutableMap()
                        updatedCountriesNews[country] = result.data ?: emptyList()

                        state = state.copy(
                            countriesNews = updatedCountriesNews,
                            datas = result.data ?: emptyList(),
                            isLoading = false,
                            error = null
                        )
                    }
                    is Resource.Error -> {
                        Log.e("API Error", "Erro ao buscar notícias: ${result.message}")
                        state = state.copy(
                            error = result.message,
                            isLoading = false
                        )
                    }
                    is Resource.Loading -> {
                        state = state.copy(
                            isLoading = true
                        )
                    }
                }
            }
        }
    }

    // Função para pesquisar notícias com base na query de pesquisa
    private suspend fun searchForNews(query: String) {
        if (query.isBlank()) {
            // Se a pesquisa estiver em branco, não faz nada
            return
        }
        // Chamando o repositório para buscar as notícias com a query
        val result = newsRepository.searchForNews(query)
        when (result) {
            is Resource.Success -> {
                state = state.copy(datas = result.data ?: emptyList())
            }
            is Resource.Error -> {
                state = state.copy(error = result.message)
            }
            is Resource.Loading -> {
                state = state.copy(isLoading = true)
            }
        }
    }
}
