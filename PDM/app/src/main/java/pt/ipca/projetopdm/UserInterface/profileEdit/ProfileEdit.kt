package pt.ipca.projetopdm.UserInterface

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEdit(

    auth: FirebaseAuth,        // Instância do Firebase Authentication
    onLogout: () -> Unit,      // Callback de logout
    onNavigateToSignUp: () -> Unit
) {

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA)) // Fundo azul claro
    ) {



    }

}