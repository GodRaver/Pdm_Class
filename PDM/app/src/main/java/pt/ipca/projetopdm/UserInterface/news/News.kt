package pt.ipca.projetopdm.UserInterface.news

import android.util.Log
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import pt.ipca.projetopdm.UserInterface.news.presentation.component.NewsArticleCard
//import pt.ipca.projetopdm.UserInterface.news.util.NavGraphSetup
import pt.ipca.projetopdm.UserInterface.news.util.Resource
import pt.ipca.projetopdm.UserInterface.profileEdit.auth

/*
@Composable
fun News(navController: NavController, auth : FirebaseAuth, newsRepository: NewsRepository) {

    val navController = rememberNavController()
    NavGraphSetup(navController = navController, newsRepository = newsRepository, auth = auth)
    //val viewModel: NewsScreenViewModel = hiltViewModel()


    //Text(text = "teste")
}
 */


@Composable
fun News(navController: NavController, newsRepository: NewsRepository) {
    val result = remember { mutableStateOf<Resource<List<Data>>>(Resource.Loading()) }

    //NavGraphSetup(navController = navController, newsRepository = newsRepository, auth = auth)

    LaunchedEffect(Unit) {
        try {
            result.value = newsRepository.getFinanceNews("us")
            Log.d("News", "Notícias carregadas com sucesso.")
        }
        catch(e: Exception) {

            result.value = Resource.Error("Erro ao carregar notícias: ${e.message}")
            Log.e("NewsScreen", "Erro ao carregar notícias: ${e.message}")
        }
    }

    when (val response = result.value) {
        is Resource.Success -> {
            LazyColumn {
                items(response.data ?: emptyList()) { data ->
                    NewsArticleCard (
                        data = data,
                        onCardClicked = { articleData ->
                            navController.navigate("article_screen?url=${articleData.url}")
                            Log.d("NavController", "Navegação para: article_screen?url=${articleData.url}")
                        }
                    )
                }
            }
        }
        is Resource.Error -> {
            Text(text = "Erro: ${response.message}", color = Color.Red)
        }
        is Resource.Loading -> {
            CircularProgressIndicator()
        }
    }
}


/*
@Composable
fun NewsNavigation(
    state: NewsState,
    onEvent: (NewsEvent) -> Unit,
    onReadFullStoryButtonClicked: (String) -> Unit,
    auth: FirebaseAuth,
    newsRepository: NewsRepository,
    navController: NavController
) {
    NavHost(navController = navController, startDestination = "news_screen") {
        composable("news_screen") {
            News(
                navController = navController, // Passando o navController para a News
                newsRepository = newsRepository,
                onReadFullStoryButtonClicked = { url ->
                    val encodedUrl = Uri.encode(url)
                    navController.navigate("article_screen?url=$encodedUrl")
                    Log.d("NavController", "Navegação para: article_screen?url=$encodedUrl")
                }
            )
        }

        composable(
            route = "article_screen?url={url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            val url = backStackEntry.arguments?.getString("url")
            val decodedUrl = Uri.decode(url ?: "")

            ArticleScreen(url = decodedUrl, onBackPressed = { navController.navigateUp() })
        }
    }
}
 */