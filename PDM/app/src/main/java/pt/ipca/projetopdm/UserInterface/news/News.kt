package pt.ipca.projetopdm.UserInterface.news

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import pt.ipca.projetopdm.UserInterface.news.data.Repository.NewsRepositoryImplementation
import pt.ipca.projetopdm.UserInterface.news.di.RetrofitClient
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import pt.ipca.projetopdm.UserInterface.news.presentation.component.NewsArticleCard
import pt.ipca.projetopdm.UserInterface.news.util.NavGraphSetup
import pt.ipca.projetopdm.UserInterface.news.presentation.news_screen.NewsScreenViewModel
import pt.ipca.projetopdm.UserInterface.news.util.Resource

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
    // Inicializando o estado de resultado como Resource.Loading corretamente com o tipo esperado
    val result = remember { mutableStateOf<Resource<List<Data>>>(Resource.Loading()) }

    //NavGraphSetup(navController = navController, newsRepository = newsRepository, auth = auth)

    // Usar LaunchedEffect para buscar notícias de forma assíncrona
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

    // Exibindo o estado da requisição
    when (val response = result.value) {
        is Resource.Success -> {
            // Exibindo as notícias
            LazyColumn {
                items(response.data ?: emptyList()) { data ->
                    NewsArticleCard (
                        data = data,
                        onCardClicked = { articleData ->
                            // On card click, navigate to the ArticleScreen with the article's URL
                            navController.navigate("article_screen?url=${articleData.url}")
                            Log.d("NavController", "Navegação para: article_screen?url=${articleData.url}")
                        }
                    )
                }
            }
        }
        is Resource.Error -> {
            // Exibindo mensagem de erro
            Text(text = "Erro: ${response.message}", color = Color.Red)
        }
        is Resource.Loading -> {
            // Exibindo indicador de carregamento
            CircularProgressIndicator()
        }
    }
}