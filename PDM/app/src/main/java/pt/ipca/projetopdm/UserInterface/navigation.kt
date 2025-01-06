package pt.ipca.projetopdm.UserInterface

//import pt.ipca.projetopdm.app.PostOfficeApp
//import pt.ipca.projetopdm.navigation.Screen
//import pt.ipca.projetopdm.ui.detail.Detail
//import pt.ipca.projetopdm.UserInterface.profileEdit.uploadProfileImage
import android.app.Application
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import pt.ipca.projetopdm.Telas.LoginTelas
import pt.ipca.projetopdm.Telas.SignUpTela
import pt.ipca.projetopdm.Telas.TermosCondicoesTela
import pt.ipca.projetopdm.UserInterface.home.HomeTela
import pt.ipca.projetopdm.UserInterface.productsList.ProductsList
import pt.ipca.projetopdm.UserInterface.profileEdit.ProfileEdit
//import pt.ipca.projetopdm.UserInterface.profileEdit.profileImageUrl
import pt.ipca.projetopdm.UserInterface.chat.Chat
import pt.ipca.projetopdm.UserInterface.chat.ChatScreen
import pt.ipca.projetopdm.UserInterface.chat.UserListPeopleScreen
import android.net.Uri
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import pt.ipca.projetopdm.UserInterface.news.News
import pt.ipca.projetopdm.UserInterface.news.data.Repository.NewsRepositoryImplementation
import pt.ipca.projetopdm.UserInterface.news.di.RetrofitClient
import pt.ipca.projetopdm.UserInterface.news.domain.repository.NewsRepository
import pt.ipca.projetopdm.UserInterface.news.presentation.article_screen.ArticleScreen
import pt.ipca.projetopdm.UserInterface.news.presentation.news_screen.NewsScreen
import pt.ipca.projetopdm.UserInterface.news.presentation.news_screen.NewsScreenViewModel
import pt.ipca.projetopdm.UserInterface.productsList.AppDatabase
//import pt.ipca.projetopdm.UserInterface.productsList.FoodDaoImpl
import pt.ipca.projetopdm.UserInterface.productsList.FoodScreen
import pt.ipca.projetopdm.UserInterface.productsList.FoodViewModel
import pt.ipca.projetopdm.UserInterface.productsList.FoodViewModelFactory
import pt.ipca.projetopdm.UserInterface.productsList.ReceivedListsScreen
import pt.ipca.projetopdm.UserInterface.productsList.ReceivedListsScreen
import pt.ipca.projetopdm.UserInterface.productsList.SavedListsScreen
import pt.ipca.projetopdm.UserInterface.productsList.SharedListsScreen
import pt.ipca.projetopdm.UserInterface.productsList.SharedListsScreen

//import pt.ipca.projetopdm.UserInterface.home.HomeTela


enum class Routes {
    Home,
    Detail,
    ProfileEdit,
    ProductsList,
    Chat,
    UserListPeopleScreen,
    News,
    SavedLists,
    SharedLists,
    ReceivedLists
}

enum class AuthRoutes {
    Login,
    SignUp,
    TermsAndConditions
}


private const val HOME_NAVIGATION_TAG = "HomeNavigation"

@Composable
fun NavControllerNavigation(auth: FirebaseAuth) {
    val navController = rememberNavController()

    var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

    DisposableEffect(auth) {
        val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            isAuthenticated = firebaseAuth.currentUser != null
        }
        auth.addAuthStateListener(authStateListener)
        onDispose {
            auth.removeAuthStateListener(authStateListener)
        }
    }

    val profileImageUrl = remember { mutableStateOf("") }

    NavHost(
        navController = navController,
        startDestination = if (isAuthenticated) Routes.Home.name else AuthRoutes.Login.name
    ) {


        // Tela de Login
        composable(route = AuthRoutes.Login.name) {
            LoginTelas(
                onLoginSuccess = {
                    auth.currentUser?.let { user ->
                        Log.i("PostOfficeApp", "Login efetuado: ${user.email}")
                    }
                    navController.navigate(Routes.Home.name) // Navega para a Home após login
                },
                onNavigateToSignUp = {
                    Log.i(HOME_NAVIGATION_TAG, "Navegação para o signup atraves do login.")
                    navController.navigate(AuthRoutes.SignUp.name) // Navega para a tela de cadastro
                },
                onNavigateToTerms = {
                    Log.i(HOME_NAVIGATION_TAG, "Navegação para os termos atraves do login.")
                    try {
                        navController.navigate(AuthRoutes.TermsAndConditions.name)
                    } catch (e: Exception) {
                        Log.e(
                            HOME_NAVIGATION_TAG,
                            "Erro ao tentar navegar para os termos: ${e.message}"
                        )
                    }
                }

            )
        }


        composable(route = AuthRoutes.SignUp.name) {
            SignUpTela(
                onSignUpSuccess = {
                    navController.navigate(Routes.Home.name)
                },
                onNavigateToLogin = {
                    Log.i(HOME_NAVIGATION_TAG, "Navegação para login.")
                    navController.navigate(AuthRoutes.Login.name) // Volta para a tela de login
                },
                onNavigateToTerms = {
                    Log.i(HOME_NAVIGATION_TAG, "Navegação para tela de termos e condições.")
                    navController.navigate(AuthRoutes.TermsAndConditions.name)
                }
            )
        }

        // Tela de Termos e Condições
        composable(route = AuthRoutes.TermsAndConditions.name) {
            TermosCondicoesTela(

                onNavigateToSignUp = {
                    Log.d(
                        "RouteCheck",
                        "Navegando para rota: ${AuthRoutes.TermsAndConditions.name}"
                    )

                    navController.navigate(AuthRoutes.SignUp.name)
                }

                //onNavigateBack = { navController.popBackStack() }
            )
        }

        // Tela Home
        composable(route = Routes.Home.name) {
            HomeTela(
                navController = navController,
                auth = auth

            ) {
                auth.signOut() // Realiza o logout
                Log.i("HomeNavigation", "Logout realizado. Navegando para Login...")
                navController.navigate(AuthRoutes.Login.name)
            }
        }

        //Tela Profile Edit
        composable(route = Routes.ProfileEdit.name) {
            var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

            DisposableEffect(auth) {
                Log.d(
                    "AuthState",
                    "Verificando autenticação. Utilizador atual: ${auth.currentUser?.email}"
                )

                val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    isAuthenticated = firebaseAuth.currentUser != null
                    Log.d(
                        "AuthState",
                        "Novo estado de autenticação: ${firebaseAuth.currentUser?.email}"
                    )
                }

                auth.addAuthStateListener(authStateListener)
                onDispose {
                    auth.removeAuthStateListener(authStateListener)
                }
            }

            if (!isAuthenticated) {
                Log.d("AuthState", "Utilizador não autenticado. Navegando para Login...")
                navController.navigate(AuthRoutes.Login.name) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            } else {
                Log.d("Navigation", "Navigating to Profile Edit")
                ProfileEdit(
                    auth = auth,
                    onLogout = {
                        Log.d("ProfileEdit", "Logout iniciado")
                        auth.signOut()
                        navController.navigate(AuthRoutes.Login.name) {
                            popUpTo(0)
                            launchSingleTop = true
                        }
                    },
                    onNavigateToSignUp = {
                        Log.d("ProfileEdit", "Navegando para SignUp")
                        navController.navigate(AuthRoutes.SignUp.name)
                    },
                    onEditClick = { field ->
                        Log.d("ProfileEdit", "Campo $field clicado")
                    },
                    onPhotoSelected = { newImageUrl -> profileImageUrl.value = newImageUrl},
                    profileImageUrl = profileImageUrl

                )


            }

        }


        composable(route =  Routes.Chat.name) {


            var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

            DisposableEffect(auth) {
                Log.d(
                    "AuthState",
                    "Verificando autenticação. Utilizador atual: ${auth.currentUser?.email}"
                )

                val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    isAuthenticated = firebaseAuth.currentUser != null
                    Log.d(
                        "AuthState",
                        "Novo estado de autenticação: ${firebaseAuth.currentUser?.email}"
                    )
                }

                auth.addAuthStateListener(authStateListener)
                onDispose {
                    auth.removeAuthStateListener(authStateListener)
                }
            }

            if (!isAuthenticated) {
                Log.d("AuthState", "Utilizador não autenticado. Navegando para Login...")
                navController.navigate(AuthRoutes.Login.name) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            } else {
                // Exibir a lista de e-mails
                UserListPeopleScreen(navController = navController, auth = FirebaseAuth.getInstance())
            }

        }




        composable(
            "chat/{senderEmail}/{recipientEmail}",
            arguments = listOf(
                navArgument("senderEmail") { type = NavType.StringType },
                navArgument("recipientEmail") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            //val senderEmail = backStackEntry.arguments?.getString("senderEmail") ?: ""
            //val recipientEmail = backStackEntry.arguments?.getString("recipientEmail") ?: ""
            val senderEmail = Uri.decode(backStackEntry.arguments?.getString("senderEmail") ?: "")
            val recipientEmail = Uri.decode(backStackEntry.arguments?.getString("recipientEmail") ?: "")


            Log.d("ChatScreen", "Sender Email: $senderEmail, Recipient Email: $recipientEmail")


            ChatScreen(
                senderEmail = senderEmail,
                recipientEmail = recipientEmail,
                auth = FirebaseAuth.getInstance(),
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(AuthRoutes.Login.name) {
                        popUpTo(0)
                        launchSingleTop = true
                    }
                },
                navController = navController
            )
        }

        val newsStore = RetrofitClient.getNewsStoreApi()


        composable(route =  Routes.News.name) {


            var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

            DisposableEffect(auth) {
                Log.d(
                    "AuthState",
                    "Verificando autenticação. Utilizador atual: ${auth.currentUser?.email}"
                )

                val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    isAuthenticated = firebaseAuth.currentUser != null
                    Log.d(
                        "AuthState",
                        "Novo estado de autenticação: ${firebaseAuth.currentUser?.email}"
                    )
                }

                auth.addAuthStateListener(authStateListener)
                onDispose {
                    auth.removeAuthStateListener(authStateListener)
                }
            }

            if (!isAuthenticated) {
                Log.d("AuthState", "Utilizador não autenticado. Navegando para Login...")
                navController.navigate(AuthRoutes.Login.name) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            } else {
                // Exibir noticias

                //val newsRepository = remember { NewsRepositoryImplementation(newsStore) }

                //News(navController = navController, newsRepository = newsRepository)

                val newsRepository = remember { NewsRepositoryImplementation(newsStore) }
                val viewModel = remember { NewsScreenViewModel(newsRepository, auth) }

                NewsScreen(
                    state = viewModel.state,
                    onEvent = viewModel::onEvent,
                    onReadFullStoryButtonClicked = { url ->
                        val encodedUrl = Uri.encode(url)
                        navController.navigate("article_screen?url=$encodedUrl")
                    },
                    auth = auth,
                    navController = navController,
                    newsRepository = newsRepository
                )
            }

        }

        composable(route = "news_screen") {
            Log.d("NavGraph", "Rota de Notícias carregada: news_screen")
            val newsRepository = remember { NewsRepositoryImplementation(newsStore) }

            val viewModel = remember { NewsScreenViewModel(newsRepository, auth) }

            // Tela de Notícias
            NewsScreen(
                state = viewModel.state,
                onEvent = viewModel::onEvent,
                onReadFullStoryButtonClicked = { url ->
                    val encodedUrl = Uri.encode(url)
                    navController.navigate("article_screen?url=$encodedUrl")
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



        composable(route = Routes.ProductsList.name) {
            var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

            DisposableEffect(auth) {
                Log.d(
                    "AuthState",
                    "Verificando autenticação. Utilizador atual: ${auth.currentUser?.email}"
                )

                val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    isAuthenticated = firebaseAuth.currentUser != null
                    Log.d(
                        "AuthState",
                        "Novo estado de autenticação: ${firebaseAuth.currentUser?.email}"
                    )
                }

                auth.addAuthStateListener(authStateListener)
                onDispose {
                    auth.removeAuthStateListener(authStateListener)
                }
            }

            if (!isAuthenticated) {
                Log.d("AuthState", "Utilizador não autenticado. Navegando para Login...")
                navController.navigate(AuthRoutes.Login.name) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            } else {

                val context = LocalContext.current

                val db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "food-database"
                ).build()

                val foodDao = db.foodDao()


                val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModelFactory(application = context.applicationContext as Application))

                FoodScreen(viewModel = foodViewModel, auth = FirebaseAuth.getInstance(), navController = navController,
                    onNavigateToSavedLists = {navController.navigate(Routes.SavedLists.name)},
                    onAddNewFood = {foodViewModel.setAddingProductState(true)})
            }
        }

        composable(route = Routes.SavedLists.name) {
            var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

            DisposableEffect(auth) {
                Log.d(
                    "AuthState",
                    "Verificando autenticação. Utilizador atual: ${auth.currentUser?.email}"
                )

                val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    isAuthenticated = firebaseAuth.currentUser != null
                    Log.d(
                        "AuthState",
                        "Novo estado de autenticação: ${firebaseAuth.currentUser?.email}"
                    )
                }

                auth.addAuthStateListener(authStateListener)
                onDispose {
                    auth.removeAuthStateListener(authStateListener)
                }
            }

            if (!isAuthenticated) {
                Log.d("AuthState", "Utilizador não autenticado. Navegando para Login...")
                navController.navigate(AuthRoutes.Login.name) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            } else {

                val context = LocalContext.current
                val db = Room.databaseBuilder(
                    context,
                    AppDatabase::class.java, "food-database"
                ).build()
                val foodDao = db.foodDao()
                val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModelFactory(context.applicationContext as Application))

                SavedListsScreen(viewModel = foodViewModel, auth = FirebaseAuth.getInstance(), onBack = { navController.popBackStack() })
            }
        }



        composable(route = Routes.SharedLists.name) {
            var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

            val context = LocalContext.current

            val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModelFactory(context.applicationContext as Application))


            DisposableEffect(auth) {
                Log.d(
                    "AuthState",
                    "Verificando autenticação. Utilizador atual: ${auth.currentUser?.email}"
                )

                val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    isAuthenticated = firebaseAuth.currentUser != null
                    Log.d(
                        "AuthState",
                        "Novo estado de autenticação: ${firebaseAuth.currentUser?.email}"
                    )
                }

                auth.addAuthStateListener(authStateListener)
                onDispose {
                    auth.removeAuthStateListener(authStateListener)
                }
            }

            if (!isAuthenticated) {
                Log.d("AuthState", "Utilizador não autenticado. Navegando para Login...")
                navController.navigate(AuthRoutes.Login.name) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            } else {



                SharedListsScreen(viewModel = foodViewModel, auth = FirebaseAuth.getInstance(), onBack = { navController.popBackStack() })
            }
        }

        composable(route = Routes.ReceivedLists.name) {
            var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

            val context = LocalContext.current

            val foodViewModel: FoodViewModel = viewModel(factory = FoodViewModelFactory(context.applicationContext as Application))


            DisposableEffect(auth) {
                Log.d(
                    "AuthState",
                    "Verificando autenticação. Utilizador atual: ${auth.currentUser?.email}"
                )

                val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
                    isAuthenticated = firebaseAuth.currentUser != null
                    Log.d(
                        "AuthState",
                        "Novo estado de autenticação: ${firebaseAuth.currentUser?.email}"
                    )
                }

                auth.addAuthStateListener(authStateListener)
                onDispose {
                    auth.removeAuthStateListener(authStateListener)
                }
            }

            if (!isAuthenticated) {
                Log.d("AuthState", "Utilizador não autenticado. Navegando para Login...")
                navController.navigate(AuthRoutes.Login.name) {
                    popUpTo(0)
                    launchSingleTop = true
                }
            } else {



                ReceivedListsScreen(viewModel = foodViewModel, auth = FirebaseAuth.getInstance(), onBack = { navController.popBackStack() })
            }
        }


        // Tela Detail
       // composable(
        //    route = "${Routes.Detail.name}?id={id}",
        //    arguments = listOf(navArgument("id") { type = NavType.IntType }) // Espera um ID como parametro
       // ) { backStackEntry ->
        //    val id = backStackEntry.arguments?.getInt("id") ?: -1
        //    Detail(
         //       id = id,
         //       auth = auth,
         //       navigateUp = {
          //          navController.navigateUp() // Volta para a tela anterior
           //     }
          //  )
     //   }
    }
}

