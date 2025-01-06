package pt.ipca.projetopdm.Telas


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import pt.ipca.projetopdm.Components.ButtonComponent
import pt.ipca.projetopdm.Components.CheckBoxComponent
import pt.ipca.projetopdm.Components.ClickableLoginTextComponent
import pt.ipca.projetopdm.Components.ClickableTextComponent
import pt.ipca.projetopdm.Components.DividerTextComponent
import pt.ipca.projetopdm.Components.MyTextFieldComponent
import pt.ipca.projetopdm.Components.PasswordTextFieldComponent
import pt.ipca.projetopdm.Components.SignUpText
import pt.ipca.projetopdm.Components.SignUpToContinueText
import pt.ipca.projetopdm.R
//import pt.ipca.projetopdm.navigation.PostOfficeAppRouter
//import pt.ipca.projetopdm.navigation.Screen
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import pt.ipca.projetopdm.Components.UnderlineSignUpText


const val SIGN_UP_TAG = "SignUpScreen"


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpTela(
    onSignUpSuccess: (FirebaseUser) -> Unit,
    onNavigateToLogin: () -> Unit,
    onNavigateToTerms: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    var isNavigating by remember { mutableStateOf(false) } // Flag para navegação controlada

    fun createUserProfile(user: FirebaseUser) {
        val userId = user.uid
        val userRef = db.collection("Utilizadores").document(userId)

        // Dados do perfil que serão armazenados no Firestore
        val userProfileData = mapOf(
            "email" to (user.email ?: ""),
            //"fullname" to (user.displayName ?: ""),
            //"profileImageUrl" to (user.photoUrl?.toString() ?: "")

        )

        // Cria o perfil do usuário no Firestore
        userRef.set(userProfileData)
            .addOnSuccessListener {
                Log.i(SIGN_UP_TAG, "Perfil criado com sucesso no Firestore!")
            }
            .addOnFailureListener {
                Log.e(SIGN_UP_TAG, "Erro ao criar o perfil no Firestore: ${it.message}")
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Sign Up", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(24.dp))

        // Email Field
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password Field
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password Field
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sign Up Button
        Button(
            onClick = {
                if (password != confirmPassword) {
                    errorMessage = "Passwords do not match!"
                    return@Button
                }
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.i(SIGN_UP_TAG, "Sign up successful")
                            auth.currentUser?.let { user ->
                            createUserProfile(user)
                            onSignUpSuccess(user)
                            }

                        } else {
                            val error = task.exception?.localizedMessage ?: "Sign up failed"
                            Log.e(SIGN_UP_TAG, error)
                            errorMessage = error
                        }
                    }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // CheckBoxComponent(value = stringResource(id = R.string.terms_and_conditions),
        //   onTextSelected = {

//                Log.d("TermsAndConditionsScreen", "TermsAndConditionsScreen from sign up")

        //PostOfficeAppRouter.navigateTo(Screen.TermsAndConditionsScreen)


        //          }
        //    )

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

        // Botão de navegação para a tela de Login
        TextButton(onClick = {
            if (!isNavigating) {
                isNavigating = true
                Log.d("LoginScreen", "Navigate to Login")
                onNavigateToLogin()  // Navegar para a tela de SignUp
                isNavigating = false
            }
        }) {
            Text("Don't have an account? Sign Up")
        }


        // Error Message
        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = errorMessage ?: "", color = MaterialTheme.colorScheme.error)
        }
    }
}


/*

@Composable
fun SignUpScreen() {


    Surface(

        //color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
            .background(Color.White)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {


            SignUpText(value = stringResource(id = R.string.sign_in))
            SignUpToContinueText(value = stringResource(id = R.string.sign_in_continue))
            //MyTextFieldComponent(labelValue = stringResource(id = R.string.app_name))

        }

    }
}


@Preview
@Composable
fun DefaultPreviewOfSignUpScreen() {


    Surface(

        //color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
            .background(Color.White)
    ) {

        Column(modifier = Modifier.fillMaxSize()) {




            SignUpText(value = stringResource(id = R.string.sign_in))
            SignUpToContinueText(value = stringResource(id = R.string.sign_in_continue))
            Spacer(modifier = Modifier.height(20.dp))
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.name),
                painterResource(id = R.drawable.name)
            )
            MyTextFieldComponent(
                labelValue = stringResource(id = R.string.email),
                painterResource(id = R.drawable.email)
            )
            PasswordTextFieldComponent(
                labelValue = stringResource(id = R.string.password),
                painterResource(id = R.drawable.password)
            )

            CheckBoxComponent(value = stringResource(id = R.string.terms_and_conditions),
            onTextSelected = {

                PostOfficeAppRouter.navigateTo(Screen.TermsAndConditionsScreen)
            }
            )




            Spacer(modifier = Modifier.height(80.dp))


            ButtonComponent(value = stringResource(id = R.string.register))


            Spacer(modifier = Modifier.height(20.dp))

            DividerTextComponent()

            ClickableLoginTextComponent(tryingToLogin = true, onTextSelected = {
             PostOfficeAppRouter.navigateTo(Screen.LoginScreen)
            })

        }


    }

}

 */
