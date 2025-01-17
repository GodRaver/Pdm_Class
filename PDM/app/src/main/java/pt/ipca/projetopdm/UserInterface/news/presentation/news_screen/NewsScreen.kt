package pt.ipca.projetopdm.UserInterface.news.presentation.news_screen

import android.util.Log
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import pt.ipca.projetopdm.UserInterface.news.data.Repository.NewsRepositoryImplementation
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
//import pt.ipca.experiencia9.domain.model.Data
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Similar
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import pt.ipca.projetopdm.UserInterface.news.presentation.component.BottomSheetComponent
import pt.ipca.projetopdm.UserInterface.news.presentation.component.CountryTabRow
import pt.ipca.projetopdm.UserInterface.news.presentation.component.NewsArticleCard
import pt.ipca.projetopdm.UserInterface.news.presentation.component.NewsScreenTopBar
import pt.ipca.projetopdm.UserInterface.news.presentation.component.RetryContent
import pt.ipca.projetopdm.UserInterface.news.presentation.component.SearchAppBar
import pt.ipca.projetopdm.UserInterface.news.util.Resource
import pt.ipca.projetopdm.UserInterface.news.di.RetrofitClient


val newsStore = RetrofitClient.getNewsStoreApi()
val newsRepository = NewsRepositoryImplementation(newsStore)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun NewsScreen(
    navController: NavController,
    auth: FirebaseAuth,
    newsRepository: NewsRepository,
    state: NewsScreenState,
    onEvent: (NewsScreenEvent) -> Unit,
    onReadFullStoryButtonClicked: (String) -> Unit



) {

    val viewModel = remember { NewsScreenViewModel(newsRepository, auth) }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pagerState = rememberPagerState(pageCount = {41})
    val coroutineScope = rememberCoroutineScope()
    val countries = listOf("ar", "au", "be", "br", "ca", "ch", "cl", "cn", "cz", "de", "eg", "es", "eu", "fr", "gb", "global", "gr", "hk", "hu", "id", "ie", "il", "it", "jp", "kr", "lk", "mx", "my", "nl", "no", "nz", "ph", "pt", "qa", "ru", "sa", "tr", "tw", "us", "ve", "za")
    //var selectedCountry by remember { mutableStateOf(countries[0]) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var shouldBottomSheetShow by remember { mutableStateOf(false) }
    val selectedCountry = state.selectedCountry

    val result = remember { mutableStateOf<Resource<List<Data>>>(Resource.Loading()) }

    LaunchedEffect(Unit) {
        try {
            result.value = Resource.Loading()
            result.value = newsRepository.getFinanceNews("us")  // ou qualquer outro país
            Log.d("News", "Notícias carregadas com sucesso.")
        } catch (e: Exception) {
            result.value = Resource.Error("Erro ao carregar notícias: ${e.message}")
            Log.e("NewsScreen", "Erro ao carregar notícias: ${e.message}")
        }
    }




/*
    LaunchedEffect(pagerState.currentPage) {
        val country = countries[pagerState.currentPage]
        if (selectedCountry != country) {
            onEvent(NewsScreenEvent.onCountryChange(country)) // Atualiza o país no ViewModel
        }
    }


 */





    val focusRequester = remember {
        FocusRequester()
    }

    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    if (sheetState.isVisible || shouldBottomSheetShow) {
        Log.d("NewsScreen", "Abrindo ModalBottomSheet para o artigo: ${state.selectedArticle?.title}")
        ModalBottomSheet(
            onDismissRequest = {
                coroutineScope.launch { sheetState.hide() }
            },
            sheetState = sheetState,
            modifier = Modifier.fillMaxSize(),  //retirar para voltar a deixar em baixo
            content = {
                state.selectedArticle?.let { article ->
                    Log.d("NewsScreen", "Artigo exibido no BottomSheet: ${article.title}")
                    BottomSheetComponent(
                        article = article,
                        onReadFullStoryButtonClicked = {
                            Log.d("NewsScreen", "Botão 'Leia a notícia completa' clicado para: ${article.url}")
                            onReadFullStoryButtonClicked(article.url)
                            coroutineScope.launch { sheetState.hide() }
                        }
                    )
                }
            }
        )
    }


    LaunchedEffect(key1 = pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            onEvent(NewsScreenEvent.onCountryChange(countries[page]))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "Selected Country: ${state.selectedCountry}",
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = "Number of Articles: ${state.datas.size}",
            style = MaterialTheme.typography.bodyMedium
        )

        Crossfade(targetState = state.isSearchBarVisible) { isVisible ->
            if (isVisible) {
                Column {
                    NewsArticlesList(
                        state = state,
                        onCardClicked = { article ->
                            shouldBottomSheetShow = true
                            onEvent(NewsScreenEvent.onNewsCardClicked(data = article))
                        },
                        onRetry = {
                            onEvent(NewsScreenEvent.onCountryChange(state.selectedCountry))
                        },
                        newsRepository = newsRepository
                    )
                }
            } else {
                Scaffold(
                    modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
                    topBar = {
                        NewsScreenTopBar(
                            scrollBehavior = scrollBehavior,
                            onSearchIconClicked = {
                                coroutineScope.launch {
                                    delay(500)
                                    focusRequester.requestFocus()
                                }
                                onEvent(NewsScreenEvent.onSearchIconClicked)
                            }
                        )
                    }
                ) { padding ->
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {
                        CountryTabRow(
                            pagerState = pagerState,
                            countries = countries,
                            onTabSelected = { index ->
                                coroutineScope.launch { pagerState.animateScrollToPage(index) }
                            }
                        )

                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.weight(1f)
                        ) { page ->
                            NewsArticlesList(
                                state = state,
                                onCardClicked = { article ->
                                    shouldBottomSheetShow = true
                                    onEvent(NewsScreenEvent.onNewsCardClicked(data = article))
                                },
                                onRetry = {
                                    onEvent(NewsScreenEvent.onCountryChange(countries[page]))
                                },
                                newsRepository = newsRepository
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NewsArticlesList(
    state: NewsScreenState,
    onCardClicked: (Data) -> Unit,
    onRetry: () -> Unit,
    newsRepository: NewsRepository

) {
    val result = remember { mutableStateOf<Resource<List<Data>>>(Resource.Loading()) }

    LaunchedEffect(state.selectedCountry) {
        try {
            Log.d("NewsScreen", "Iniciando carregamento de notícias para o país: ${state.selectedCountry}")
            result.value = Resource.Loading()
            // Simula chamada à API
            val news = withContext(Dispatchers.IO) {
                // Substitua pelo método real de carregamento
                newsRepository.getFinanceNews(state.selectedCountry)
            }
            result.value = news
            Log.d("NewsScreen", "Notícias carregadas: ${(news as? Resource.Success)?.data?.size ?: 0} artigos")
        } catch (e: Exception) {
            Log.e("NewsScreen", "Erro ao carregar notícias: ${e.message}")
            result.value = Resource.Error("Erro ao carregar notícias: ${e.message}")
        }
    }


    when (val resource = result.value) {
        is Resource.Loading -> {
            Log.d("NewsScreen", "Carregando notícias...")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        is Resource.Error -> {
            Log.e("NewsScreen", "Erro ao carregar notícias: ${resource.message}")
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = resource.message ?: "An error occurred",
                    color = Color.Red
                )
                Button(onClick = onRetry) {
                    Text("Retry")
                }
            }
        }

        is Resource.Success -> {
            Log.d("NewsScreen", "Notícias carregadas com sucesso: ${resource.data?.size} artigos")
            Log.d("TESTETESTE", "log para ver se tem sucesso")
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                resource.data?.let { articles ->
                    Log.d("LazyColumn", "Exibindo ${articles.size} artigos.")
                    items(articles) { article ->
                        NewsArticleCard(data = article,
                            onCardClicked = {
                                Log.d("NewsArticlesList", "Artigo clicado enviado: ${it.title}")
                                onCardClicked(it)

                            })
                    }
                }
            }
        }

    }
}

/* SearchAppBar(
                        modifier = Modifier.focusRequester(focusRequester),
                        value = state.searchQuery,
                        onInputValueChange = {

                                newValue ->

                            onEvent(NewsScreenEvent.onSearchQueryChanged(newValue))
                        },
                        onCloseIconClicked = { onEvent(NewsScreenEvent.onCloseIconClicked)},
                        onSearchIconClicked = {

                            keyboardController?.hide()
                            focusManager.clearFocus()
                        })

                    */