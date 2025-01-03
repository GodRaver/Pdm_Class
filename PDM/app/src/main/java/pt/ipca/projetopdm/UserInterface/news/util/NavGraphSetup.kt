package pt.ipca.projetopdm.UserInterface.news.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import pt.ipca.projetopdm.UserInterface.news.presentation.article_screen.ArticleScreen
import pt.ipca.projetopdm.UserInterface.news.presentation.news_screen.NewsScreenViewModel
import pt.ipca.projetopdm.UserInterface.news.presentation.news_screen.NewsScreen

@Composable
fun NavGraphSetup(

    navController: NavHostController,
    newsRepository: NewsRepository,
    auth: FirebaseAuth
) {


    val argKey = "web_url"
    NavHost(navController = navController, startDestination = "news_screen") {

        composable(route = "news_screen") {

            val viewModel = remember { NewsScreenViewModel(newsRepository, auth) }


            NewsScreen(state = viewModel.state,
                onEvent = viewModel::onEvent,
                onReadFullStoryButtonClicked = {url ->

                    navController.navigate("article_screen?web_url=$url")
                },
                auth = auth,
                navController = navController,
                newsRepository = newsRepository

                )
        }
        composable(

            route = "article_screen?$argKey={$argKey}",
            arguments = listOf(navArgument(name = argKey) {

                type = NavType.StringType
            })
        ) {

                BackStackEntry ->

            ArticleScreen(url = BackStackEntry.arguments?.getString(argKey), onBackPressed = {navController.navigateUp()})
        }
    }

}