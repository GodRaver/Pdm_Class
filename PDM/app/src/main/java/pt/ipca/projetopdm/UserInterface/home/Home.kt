package pt.ipca.projetopdm.UserInterface.home

import android.annotation.SuppressLint
import android.util.Log
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
import androidx.compose.material3.TopAppBar
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import pt.ipca.projetopdm.UserInterface.AuthRoutes
import pt.ipca.projetopdm.UserInterface.Routes
import java.util.*

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTela(
    navController: NavController, // Navegação para Detalhes
    auth: FirebaseAuth,        // Instância do Firebase Authentication
    onLogout: () -> Unit,      // Callback de logout
    //onNavigateToSignUp: () -> Unit
) {
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
                text = "Profile",
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

@Composable
fun MenuGrid(navController: NavController) {
    // Organizar itens em uma grid de 2 colunas
    Column {
        // Primeira linha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            MenuItem(iconText = "📊", label = "Products List") {
                Log.d("Navigation", "Navigating to Products List")
                navController.navigate(Routes.ProductsList.name) // Navegação para a lista de produtos
            }
            MenuItem(iconText = "👤", label = "Profile Edit") {
                Log.d("Navigation", "Navigating to Profile Edit")
                navController.navigate(Routes.ProfileEdit.name) // Navegação para a edição de perfil
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Segunda linha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Adicionar mais MenuItems aqui, se necessário
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Terceira linha
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Adicionar mais MenuItems aqui, se necessário
        }
    }
}

@Composable
fun MenuItem(iconText: String, label: String, onClick: () -> Unit) {
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
            Text(
                text = iconText,
                fontSize = 36.sp,
                textAlign = TextAlign.Center
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








