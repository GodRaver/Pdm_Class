package pt.ipca.projetopdm.UserInterface.news.util

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import pt.ipca.projetopdm.UserInterface.news.presentation.article_screen.ArticleScreen
import pt.ipca.projetopdm.UserInterface.news.presentation.news_screen.NewsScreenViewModel
import pt.ipca.projetopdm.UserInterface.news.presentation.news_screen.NewsScreen

/*                                                            Codigo obsoleto

@Composable
fun NavGraphSetup(

    navController: NavHostController,
    newsRepository: NewsRepository,
    auth: FirebaseAuth
) {


    navController.addOnDestinationChangedListener { controller, destination, arguments ->
        Log.d("NavController", "Destino atual: ${destination.route}")

        // Verificando a rota de destino
        if (destination.route?.startsWith("article_screen") == true) {
            val url = arguments?.getString("url")
            Log.d("NavController", "Rota: article_screen?url=${url}")
        }
    }

    val argKey = "url"

    val newsNavController = rememberNavController()

    NavHost(navController = newsNavController, startDestination = "news_screen") {
        Log.d("NavGraph", "Configuração do NavHost iniciada.")

        composable(route = "news_screen") {
            Log.d("NavGraph", "Rota de Notícias carregada: news_screen")
            val viewModel = remember { NewsScreenViewModel(newsRepository, auth) }

            // Tela de Notícias
            NewsScreen(state = viewModel.state,
                onEvent = viewModel::onEvent,
                onReadFullStoryButtonClicked = { url ->

                    val encodedUrl = Uri.encode(url)
                    //  para a tela de artigos
                    navController.navigate("article_screen?$argKey=$encodedUrl")
                    Log.d("NavGraph", "estou a ir para aqui: $url")

                },
                auth = auth,
                navController = navController,
                newsRepository = newsRepository
            )
        }

        composable(
            route = "article_screen?url={url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            Log.d("NavGraph", "Rota de Artigo carregada: $route")
            val url = backStackEntry.arguments?.getString("url")
            val decodedUrl = Uri.decode(url ?: "")

            ArticleScreen(url = decodedUrl, onBackPressed = { navController.navigateUp() })
        }
    }

}





@Composable
fun NavGraphSetup(
    navController: NavController,
    newsRepository: NewsRepository,
    auth: FirebaseAuth
) {
    navController.addOnDestinationChangedListener { controller, destination, arguments ->
        Log.d("NavController", "Destino atual: ${destination.route}")

        // Verificando a rota de destino
        if (destination.route?.startsWith("article_screen") == true) {
            val url = arguments?.getString("url")
            Log.d("NavController", "Rota: article_screen?url=${url}")
        }
    }

    val argKey = "url"

    NavHost(navController = navController, startDestination = "news_screen") {
        Log.d("NavGraph", "Configuração do NavHost iniciada.")

        composable(route = "news_screen") {
            Log.d("NavGraph", "Rota de Notícias carregada: news_screen")
            val viewModel = remember { NewsScreenViewModel(newsRepository, auth) }

            // Tela de Notícias
            NewsScreen(
                state = viewModel.state,
                onEvent = viewModel::onEvent,
                onReadFullStoryButtonClicked = { url ->
                    // Codificando a URL para garantir que seja válida
                    val encodedUrl = Uri.encode(url)
                    // Navegando para a tela de artigos
                    navController.navigate("article_screen?$argKey=$url")
                    Log.d("NavGraph", "estou a ir para aqui: $url")
                },
                auth = auth,
                navController = navController,
                newsRepository = newsRepository
            )
        }

        composable(
            route = "article_screen?url={url}",
            arguments = listOf(navArgument("url") { type = NavType.StringType })
        ) { backStackEntry ->
            // Obtendo o parâmetro "url" da navegação
            val url = backStackEntry.arguments?.getString("url")
            val decodedUrl = Uri.decode(url ?: "")

            // Exibindo a tela de detalhes do artigo
            ArticleScreen(url = decodedUrl, onBackPressed = { navController.navigateUp() })
        }
    }
}
 */