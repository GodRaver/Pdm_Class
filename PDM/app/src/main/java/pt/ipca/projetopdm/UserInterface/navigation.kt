package pt.ipca.projetopdm.UserInterface

//import pt.ipca.projetopdm.app.PostOfficeApp
//import pt.ipca.projetopdm.navigation.Screen
//import pt.ipca.projetopdm.ui.detail.Detail
//import pt.ipca.projetopdm.UserInterface.profileEdit.uploadProfileImage
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

//import pt.ipca.projetopdm.UserInterface.home.HomeTela


enum class Routes {
    Home,
    Detail,
    ProfileEdit,
    ProductsList,
    Chat,
    UserListPeopleScreen
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
                    Log.d(
                        "RouteCheck",
                        "Navegando para rota: ${AuthRoutes.TermsAndConditions.name}"
                    )

                    navController.navigate(AuthRoutes.SignUp.name) // Navega para a tela de cadastro
                }

                //onNavigateBack = { navController.popBackStack() } // Retorna à tela anterior
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
                navController.navigate(AuthRoutes.Login.name) // Navega de volta para Login após logout
            }
        }

        //Tela Profile Edit
        composable(route = Routes.ProfileEdit.name) {
            // Verifique se o usuário está autenticado
            var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }

            DisposableEffect(auth) {
                Log.d(
                    "AuthState",
                    "Verificando autenticação. Usuário atual: ${auth.currentUser?.email}"
                )

                // Adiciona um ouvinte para mudanças no estado de autenticação
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

            // Se o usuário não estiver autenticado, navegue para o login
            if (!isAuthenticated) {
                Log.d("AuthState", "Usuário não autenticado. Navegando para Login...")
                navController.navigate(AuthRoutes.Login.name) {
                    // Garante que não voltaremos para a tela anterior após navegar para o login
                    popUpTo(0) // Zera a pilha de navegação
                    launchSingleTop = true
                }
            } else {
                // Caso o usuário esteja autenticado, exibe a tela de ProfileEdit
                Log.d("Navigation", "Navigating to Profile Edit")
                ProfileEdit(
                    auth = auth,
                    onLogout = {
                        Log.d("ProfileEdit", "Logout iniciado")
                        auth.signOut()
                        navController.navigate(AuthRoutes.Login.name) {
                            popUpTo(0) // Zera a pilha de navegação
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

                // Adiciona um ouvinte para mudanças no estado de autenticação
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
                Log.d("AuthState", "Usuário não autenticado. Navegando para Login...")
                navController.navigate(AuthRoutes.Login.name) {
                    // Garante que não voltaremos para a tela anterior após navegar para o login
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

            // Passa os parâmetros para o ChatScreen

            Log.d("ChatScreen", "Sender Email: $senderEmail, Recipient Email: $recipientEmail")


            ChatScreen(
                senderEmail = senderEmail,
                recipientEmail = recipientEmail,
                auth = FirebaseAuth.getInstance(),
                onLogout = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(AuthRoutes.Login.name) {
                        popUpTo(0) // Zera a pilha de navegação
                        launchSingleTop = true
                    }
                },
                navController = navController
            )
        }





        composable(route = Routes.ProductsList.name) {
            ProductsList(

                auth = auth,
                onLogout = {
                    auth.signOut() // Realiza o logout
                    Log.i("ListPurchases", "Logout realizado. Navegando para Login...")
                    navController.navigate(AuthRoutes.Login.name) // Navega de volta para Login após logout
                },
                onNavigateToSignUp = {navController.navigate(AuthRoutes.SignUp.name)},
                onEditClick = { field ->
                    Log.d("ProfileEdit", "Edit button clicked on field: $field")
                }
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

