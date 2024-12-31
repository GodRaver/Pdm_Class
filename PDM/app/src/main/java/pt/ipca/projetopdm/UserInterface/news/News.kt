package pt.ipca.projetopdm.UserInterface.news

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import pt.ipca.projetopdm.UserInterface.news.util.NavGraphSetup
import pt.ipca.projetopdm.UserInterface.news.presentation.news_screen.NewsScreenViewModel


@Composable
fun News(navController: NavController, auth : FirebaseAuth) {

    val navController = rememberNavController()
    NavGraphSetup(navController = navController)
    val viewModel: NewsScreenViewModel = hiltViewModel()

}
