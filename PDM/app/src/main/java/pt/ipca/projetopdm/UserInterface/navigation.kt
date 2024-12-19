package pt.ipca.projetopdm.UserInterface

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import pt.ipca.projetopdm.Telas.LoginTelas
import pt.ipca.projetopdm.Telas.SignUpTela
import pt.ipca.projetopdm.Telas.TermosCondicoesTela
//import pt.ipca.projetopdm.app.PostOfficeApp
//import pt.ipca.projetopdm.navigation.Screen
//import pt.ipca.projetopdm.ui.detail.Detail
import pt.ipca.projetopdm.UserInterface.Routes
import pt.ipca.projetopdm.UserInterface.AuthRoutes
import pt.ipca.projetopdm.UserInterface.home.Home


enum class Routes {
    Home,
    Detail
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

    // Estado para verificar se o usuário está autenticado
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
                        Log.e(HOME_NAVIGATION_TAG, "Erro ao tentar navegar para os termos: ${e.message}")
                    }
                }

            )
        }

        // Tela de Cadastro
        composable(route = AuthRoutes.SignUp.name) {
            SignUpTela(
                onSignUpSuccess = {
                    navController.navigate(Routes.Home.name) // Navega para a Home após cadastro
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
                    Log.d("RouteCheck", "Navegando para rota: ${AuthRoutes.TermsAndConditions.name}")

                    navController.navigate(AuthRoutes.SignUp.name) // Navega para a tela de cadastro
                }

                //onNavigateBack = { navController.popBackStack() } // Retorna à tela anterior
            )
        }

        // Tela Home
        composable(route = Routes.Home.name) {
            Home(
                onNavigate = { id ->
                    navController.navigate("${Routes.Detail.name}?id=$id") // Navega para a tela de detalhes
                },
                auth = auth,
                onLogout = {
                    auth.signOut() // Realiza o logout
                    Log.i("HomeNavigation", "Logout realizado. Navegando para Login...")
                    navController.navigate(AuthRoutes.Login.name) // Navega de volta para Login após logout
                },
                onNavigateToSignUp = {navController.navigate(AuthRoutes.SignUp.name)}
            )
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

