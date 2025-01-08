package pt.ipca.projetopdm.UserInterface.home

import android.annotation.SuppressLint
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
//import pt.ipca.projetopdm.data.room.models.Item
//import pt.ipca.projetopdm.data.room.models.ItemsWithStoreAndList
//import pt.ipca.projetopdm.ui.Category
import androidx.compose.material3.Surface
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth
//import pt.ipca.projetopdm.Screens.HomeScreen
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberImagePainter
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import com.google.firebase.storage.FirebaseStorage
import pt.ipca.projetopdm.UserInterface.AuthRoutes
import pt.ipca.projetopdm.UserInterface.Routes
import java.util.*
import pt.ipca.projetopdm.R
import pt.ipca.projetopdm.UserInterface.profileEdit.ProfileImage

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTela(
    navController: NavController, // Navegação para Detalhes
    auth: FirebaseAuth,        // Instância do Firebase Authentication
    onLogout: () -> Unit,
    userId: String
    //onNavigateToSignUp: () -> Unit
) {
    val user = auth.currentUser
    //val userProfileImage = remember { mutableStateOf(user?.photoUrl?.toString() ?: "") }
    val userName = remember { mutableStateOf(user?.displayName ?: "Nome não disponível") }
    val userEmail = remember { mutableStateOf(user?.email ?: "Email não disponível") }

    val profileImageUrl = remember { mutableStateOf("") }
    val userProfileImage = remember { mutableStateOf(user?.photoUrl?.toString() ?: "") }

    val context = LocalContext.current

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(), //permissao e escolha do arquivo
        onResult = { uri ->
            if (uri != null) {
                val storageRef = FirebaseStorage.getInstance().reference
                val photoRef = storageRef.child("profile_photos/$userId/${System.currentTimeMillis()}.jpg")

                // Faça o upload da imagem
                val uploadTask = photoRef.putFile(uri)
                uploadTask.addOnSuccessListener {
                    // Obtendo o URL da imagem carregada
                    photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                        //profileImageUrl.value = downloadUri.toString()
                        Log.d("HomeTela", "URL da imagem de perfil: ${downloadUri.toString()}")
                        userProfileImage.value = downloadUri.toString()
                    }.addOnFailureListener { e ->
                        Log.e("HomeScreen", "Erro ao obter URL: ${e.message}")
                    }
                }.addOnFailureListener { e ->
                    Log.e("HomeScreen", "Erro no upload: ${e.message}")
                }
            }
        }
    )










    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // Exibe nome e email no título
                    Column {
                        Text(text = userName.value, style = MaterialTheme.typography.titleMedium)
                        Text(text = userEmail.value, style = MaterialTheme.typography.bodySmall)
                    }
                },
                actions = {
                    userProfileImage.value?.let {

                            IconButton(onClick = { launcher.launch("image/*") }) {
                                AsyncImage(
                                    model = it,
                                    contentDescription = "Foto de perfil",
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.Gray)
                                )
                            }
                        }
                },
                colors = TopAppBarDefaults.mediumTopAppBarColors()
            )
        },
        content = {
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFE0F7FA)) // Fundo azul claro
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Título do menu
                    Text(
                        text = "Menu Principal",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.DarkGray,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Menu em grade
                    Spacer(modifier = Modifier.height(16.dp))
                    MenuGrid(navController)

                    Button(
                        onClick = {
                            navController.navigate(AuthRoutes.Login.name)
                        },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Go Back to Sign Up")
                    }
                }
            }
        }
    )
}



@Composable
fun MenuGrid(navController: NavController) {
    // Organizar itens em uma grid de 2 colunas
    Column {
        // Primeira linha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuItem(icon = painterResource(id = R.drawable.productlist), label = "Products List") {
                Log.d("Navigation", "Navigating to Products List")
                navController.navigate(Routes.ProductsList.name) // Navegação para a lista de produtos
            }
            MenuItem(icon = painterResource(id = R.drawable.news), label = "News") {
                Log.d("Navigation", "Navigating to News")
                navController.navigate(Routes.News.name) // Navegação para a edição de perfil
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Segunda linha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuItem(painterResource(id = R.drawable.chat), label = "Chat") {
                Log.d("Navigation", "Navigating to Chat")
                navController.navigate(Routes.Chat.name) // Navegação para a lista de produtos
            }
            MenuItem(icon = painterResource(id = R.drawable.profileedit), label = "Profile Edit") {
                Log.d("Navigation", "Navigating to Profile Edit")
                navController.navigate(Routes.ProfileEdit.name) // Navegação para a edição de perfil
            }

        }
        Spacer(modifier = Modifier.height(16.dp))

        // Terceira linha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuItem(painterResource(id = R.drawable.savedlists), label = "SavedLists") {
                Log.d("Navigation", "Navigating to SavedLists")
                navController.navigate(Routes.SavedLists.name) // Navegação para a lista de produtos
            }
            MenuItem(icon = painterResource(id = R.drawable.shared), label = "Share Lists") {
                Log.d("Navigation", "Navigating to SharedLists")
                navController.navigate(Routes.SharedLists.name) // Navegação para a edição de perfil
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuItem(painterResource(id = R.drawable.receive), label = "Received Lists") {
                Log.d("Navigation", "Navigating to ReceivedLists")
                navController.navigate(Routes.ReceivedLists.name)
            }
            MenuItem(icon = painterResource(id = R.drawable.option), label = "Retrieve Password") {
                Log.d("Navigation", "Navigating to RetrievePassword")
                navController.navigate(Routes.UserRecovery.name)
            }
        }

    }
}

@Composable
fun MenuItem(icon: Painter, label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(120.dp)
            .background(Color.White, shape = RoundedCornerShape(16.dp))
            .padding(8.dp)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.Unspecified
            )
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = Color.DarkGray
            )
        }
    }
}








