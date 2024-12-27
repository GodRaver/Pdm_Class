package pt.ipca.projetopdm.Telas

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipca.projetopdm.Components.CheckBoxComponent
import pt.ipca.projetopdm.Components.UnderlineSignUpText
import pt.ipca.projetopdm.R
//import pt.ipca.projetopdm.navigation.PostOfficeAppRouter
//import pt.ipca.projetopdm.navigation.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginTelas(
    onLoginSuccess: (FirebaseUser) -> Unit,
    onNavigateToSignUp: () -> Unit,
    onNavigateToTerms: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var isLoading by remember { mutableStateOf(false) } // Indicador de loading
    var isNavigating by remember { mutableStateOf(false) } // Flag para navegação controlada

    fun createOrUpdateUserProfile(user: FirebaseUser) {
        val userId = user.uid
        val userRef = db.collection("Utilizadores").document(userId)

        // Verifique se o perfil do usuário já existe no Firestore
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Se o perfil já existe, você pode atualizar os dados se necessário
                val updatedData = mapOf(
                    "email" to (user.email ?: ""), // Garante que o email será sempre uma string, mesmo que seja null
                    //"fullname" to (user.displayName ?: ""), // Se o displayName for nulo, passa uma string vazia
                    //"profileImageUrl" to (user.photoUrl?.toString() ?: "")
                )
                userRef.update(updatedData) // Atualiza o documento no Firestore
                    .addOnSuccessListener {
                        Log.i("LoginScreen", "Perfil atualizado com sucesso!")
                    }
                    .addOnFailureListener {
                        Log.e("LoginScreen", "Erro ao atualizar o perfil: ${it.message}")
                    }
            } else {
                // Se o perfil não existe, cria um novo documento
                val newUserProfile = mapOf(
                    "email" to (user.email ?: ""), // Garantir que o email seja sempre uma string
                    "fullname" to (user.displayName ?: ""), // Garantir que o fullname seja sempre uma string
                    "profileImageUrl" to (user.photoUrl?.toString() ?: ""), // Garantir que a URL da foto seja sempre uma string
                    "address" to ""
                )
                userRef.set(newUserProfile) // Cria o perfil no Firestore
                    .addOnSuccessListener {
                        Log.i("LoginScreen", "Perfil criado com sucesso!")
                    }
                    .addOnFailureListener {
                        Log.e("LoginScreen", "Erro ao criar perfil: ${it.message}")
                    }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Login", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()

        )

        Spacer(modifier = Modifier.height(16.dp))

        // Botão de login
        Button(
            onClick = {
                isLoading = true // Define o estado de carregamento
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        isLoading = false // Finaliza o estado de carregamento
                        if (task.isSuccessful) {
                            Log.i("LoginScreen", "Login successful")
                            auth.currentUser?.let {

                                onLoginSuccess(it)
                                createOrUpdateUserProfile(it)
                            } // Chama o sucesso do login
                        } else {
                            val error = task.exception?.localizedMessage ?: "Login failed"
                            Log.e("LoginScreen", error)
                            errorMessage = error
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading // Desabilita o botão enquanto carrega
        ) {
            Text("Login")
        }

        // Mensagem de erro
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        }

        // Se estiver carregando, mostra um indicador de carregamento
        if (isLoading) {
            CircularProgressIndicator()
        }

        Spacer(modifier = Modifier.height(8.dp))




        TextButton(onClick = {
            if (!isNavigating) {
                isNavigating = true
                Log.d("onNavigaetToTerms", "Navigate to Terms from Login")
                onNavigateToTerms()  // Navegar para os Termos
                isNavigating = false
            }
        }) {
            Text("Terms and Conditions")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Botão de navegação para a tela de SignUp
        TextButton(onClick = {
            if (!isNavigating) {
                isNavigating = true
                Log.d("LoginScreen", "Navigate to SignUp")
                onNavigateToSignUp()  // Navegar para a tela de SignUp
                isNavigating = false
            }
        }) {
            Text("Don't have an account? Sign Up")
        }




    }


}