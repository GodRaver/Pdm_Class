package pt.ipca.projetopdm.UserInterface.news.presentation.news_screen

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
//import pt.ipca.experiencia9.domain.model.Data
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Similar
import pt.ipca.projetopdm.UserInterface.news.presentation.component.BottomSheetComponent
import pt.ipca.projetopdm.UserInterface.news.presentation.component.CountryTabRow
//import pt.ipca.projetopdm.UserInterface.news.presentation.component.NewsArticleCard
import pt.ipca.projetopdm.UserInterface.news.presentation.component.NewsScreenTopBar
import pt.ipca.projetopdm.UserInterface.news.presentation.component.RetryContent
import pt.ipca.projetopdm.UserInterface.news.presentation.component.SearchAppBar

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class,
    ExperimentalComposeUiApi::class
)
@Composable
fun NewsScreen(

    state:NewsScreenState,
    onEvent: (NewsScreenEvent) -> Unit,
    onReadFullStoryButtonClicked: (String) -> Unit


) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val pagerState = rememberPagerState(pageCount = {38})
    val coroutineScope = rememberCoroutineScope()
    val countries = listOf("ar", "au", "be", "br", "ca", "ch",
        "cl", "cn", "cz", "de", "eg", "es", "eu", "fr", "gb", "global", "gr",
        "hk", "hu", "id", "ie", "il", "it", "jp", "kr", "lk", "mx", "my", "nl", "no", "nz", "ph", "pt", "qa", "ru", "sa", "tr", "tw", "us", "ve", "za")

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var shouldBottomSheetShow by remember { mutableStateOf(false) }


    val focusRequester = remember {

        FocusRequester()
    }

    val focusManager = LocalFocusManager.current

    val keyboardController = LocalSoftwareKeyboardController.current

    if(shouldBottomSheetShow) {

        ModalBottomSheet(
            onDismissRequest = {

                shouldBottomSheetShow = false
            },
            sheetState = sheetState,
            content = {

                state.selectedArticle?.let {




                    BottomSheetComponent(article = it,onReadFullStoryButtonClicked = {


                        onReadFullStoryButtonClicked(it.url)
                        coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {

                            if(!sheetState.isVisible) shouldBottomSheetShow = false
                        }
                    })
                }
            }


        )
    }

    LaunchedEffect(key1 = pagerState) {

        snapshotFlow { pagerState.currentPage }.collect {page -> onEvent(NewsScreenEvent.onCountryChange(countries[page]))

        }
    }

    LaunchedEffect(key1 = Unit) {

        if(state.searchQuery.isNotEmpty()) {

            onEvent(NewsScreenEvent.onSearchQueryChanged(searchQuery = state.searchQuery))
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Crossfade(targetState = state.isSearchBarVisible) {

                isVisible ->

            if (isVisible) {


                Column {


                    SearchAppBar(
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


                    NewsArticlesList(
                        state = state,
                        onCardClicked = {

                                article ->
                            shouldBottomSheetShow = true
                            onEvent(NewsScreenEvent.onNewsCardClicked(data = article))
                        },
                        onRetry = {
                            onEvent(NewsScreenEvent.onCountryChange(state.country))
                        }
                    )

                }




            }  else {


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
                            })

                    }
                ) {

                        padding ->
                    Column(

                        modifier = Modifier
                            .fillMaxSize()
                            .padding(padding)
                    ) {

                        CountryTabRow(pagerState = pagerState,
                            countries = countries,
                            onTabSelected = {index ->

                                coroutineScope.launch {pagerState.animateScrollToPage(index)}
                            }

                        )


                        HorizontalPager(
                            state = pagerState,
                            modifier = Modifier.weight(1f)
                        ) { page ->

                            val country = countries[page]
                            NewsArticlesList(
                                state = state,
                                onCardClicked = {

                                        article ->
                                    shouldBottomSheetShow = true
                                    onEvent(NewsScreenEvent.onNewsCardClicked(data = article))
                                },
                                onRetry = {
                                    onEvent(NewsScreenEvent.onCountryChange(countries[page]))
                                }
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
    onRetry: () -> Unit
) {



    //LazyColumn(

     //   verticalArrangement = Arrangement.spacedBy(16.dp),
     //   contentPadding = PaddingValues(16.dp)

   // )

    /*
    {

        items(state.datas) {

                data ->
            NewsArticleCard(data = data, onCardClicked = onCardClicked)

        }
    }

     */


    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        if(state.isLoading) {

            CircularProgressIndicator()
        }

        if(state.error != null) {

            RetryContent(error = state.error, onRetry =  onRetry )
        }

    }
}