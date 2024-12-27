package pt.ipca.projetopdm.UserInterface.productsList


import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.Text

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsList(
    auth: FirebaseAuth,        // Instância do Firebase Authentication
    onLogout: () -> Unit,      // Callback de logout
    onNavigateToSignUp: () -> Unit, // Navegação para tela de registro
    onEditClick: (String) -> Unit
) {

    Text(text = "teste")


}