package pt.ipca.projetopdm.UserInterface.news.util

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import pt.ipca.projetopdm.UserInterface.news.presentation.article_screen.ArticleScreen
import pt.ipca.projetopdm.UserInterface.news.presentation.news_screen.NewsScreenViewModel
import pt.ipca.projetopdm.UserInterface.news.presentation.news_screen.NewsScreen

@Composable
fun NavGraphSetup(

    navController: NavHostController
) {


    val argKey = "web_url"
    NavHost(navController = navController, startDestination = "news_screen") {

        composable(route = "news_screen") {

            val viewModel: NewsScreenViewModel = hiltViewModel()
            NewsScreen(state = viewModel.state,
                onEvent = viewModel::onEvent,
                onReadFullStoryButtonClicked = {url ->

                    navController.navigate("article_screen?web_url=$url")
                })
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