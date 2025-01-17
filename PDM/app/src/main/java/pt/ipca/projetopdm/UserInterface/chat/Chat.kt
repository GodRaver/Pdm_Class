package pt.ipca.projetopdm.UserInterface.chat

import android.content.Context
import android.net.Uri
import android.os.Parcel
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.firestore.auth.User
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import pt.ipca.projetopdm.UserInterface.AuthRoutes
//import pt.ipca.projetopdm.UserInterface.HOME_NAVIGATION_TAG
import pt.ipca.projetopdm.UserInterface.Routes
import pt.ipca.projetopdm.UserInterface.profileEdit.auth
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseUser

val LocalGetMessages = compositionLocalOf<(String, String, (List<String>) -> Unit) -> Unit> { error("No GetMessages function provided") }
val LocalSendMessage = compositionLocalOf<(String, String, String) -> Unit> { error("No SendMessage function provided") }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chat(
    auth: FirebaseAuth,
    onLogout: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    recipientEmail: String,
    navController: NavController

) {

    var isAuthenticated by remember { mutableStateOf(auth.currentUser != null) }
    var currentUserEmail by remember { mutableStateOf(auth.currentUser?.email ?: "") }
    Log.d("CHAT", "na funcao CHAT")


}


fun saveUserToFirestore(userId: String, email: String) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("Utilizadores").document(userId) // Cria um documento para cada usuário

    val userData = hashMapOf(
        "email" to email
    )

    userRef.set(userData)
        .addOnSuccessListener {
            Log.d("Firebase", "Usuário salvo com sucesso!")
        }
        .addOnFailureListener { e ->
            Log.w("Firebase", "Erro ao salvar usuário", e)
        }
}

fun getUsersFromFirestore(onResult: (List<String>) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("Utilizadores")
        .get()
        .addOnSuccessListener { result ->
            val userList = result.documents.mapNotNull { document ->
                document.getString("email") // Recupera apenas o campo "email"
            }
            Log.d("Firestore", "Utilizadores recuperados: $userList")
            onResult(userList)
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Erro ao buscar utilizador", exception)
            onResult(emptyList()) // Retorna lista vazia em caso de erro
        }
}


@Composable
fun UserListPeopleScreen(navController: NavController, auth : FirebaseAuth) {
    val users = remember { mutableStateOf<List<String>>(emptyList()) }

    LaunchedEffect(Unit) {
        Log.d("UserListScreen", "Iniciando carregamento dos utilizadores...")
        getUsersFromFirestore { userList ->
            Log.d("UserListScreen", "Usuários recuperados: $userList")
            val currentUserEmail = auth.currentUser?.email
            if(currentUserEmail != null) {

                val filteredUserList = userList.filter {it != currentUserEmail}
                users.value = filteredUserList
            } else {
                users.value = userList
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "PEOPLE", style = MaterialTheme.typography.bodyLarge)

        LazyColumn {
            items(users.value) { recipientEmail ->
                Log.d("UserListScreen", "Renderizando utilizador: $recipientEmail")
                Text(
                    text = recipientEmail,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f))
                        .padding(16.dp)
                        .clickable {
                            val senderEmail = auth.currentUser?.email
                            if (senderEmail != null) {

                                val encodedSenderEmail = Uri.encode(senderEmail)
                                val encodedRecipientEmail = Uri.encode(recipientEmail)
                                Log.d("UserListScreen", "Clicou em: $recipientEmail")
                                Log.d("UserListScreen", "Clicou em: $senderEmail")
                                Log.d(
                                    "UserListScreen",
                                    "Vamos para o chat/$senderEmail/$recipientEmail"
                                )
                                //   try {
                                //      navController.navigate("chat/$senderEmail/$recipientEmail")
                                //  } catch (e: Exception) {
                                //      Log.e(
                                //          "Navigate_to_chat",
                                //          "Erro ao tentar navegar para a pagina do chat: ${e.message}"
                                //      )

                                navController.navigate("chat/$encodedSenderEmail/$encodedRecipientEmail") {


                                    launchSingleTop = true

                                }
                            } else {
                                Log.w(
                                    "UserListScreen",
                                    "utilizador não autenticado. Não é possível iniciar o chat."
                                )
                            }
                        }
                )
            }
        }


        if (users.value.isEmpty()) {
            Text(
                text = "Nenhum utilizador encontrado.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 16.dp)
            )
            Log.w("UserListScreen", "A lista de utilizadores está vazia.") // Log se a lista estiver vazia
        }
    }
}



//_-----------------------------------------------------------------ChatScreen---------------------------------------------------------------------



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    recipientEmail: String,
    navController: NavController,
    auth: FirebaseAuth,
    senderEmail: String,
    onLogout: () -> Unit
) {


    val database = FirebaseDatabase.getInstance("https://socialstore-app-pdm-default-rtdb.europe-west1.firebasedatabase.app")
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

    // Se o usuário não estiver autenticado, navegue para o login
    if (!isAuthenticated) {
        LaunchedEffect(Unit) {
            navController.navigate(AuthRoutes.Login.name) {
                popUpTo(0) // Zera a pilha de navegação
                launchSingleTop = true
            }
        }
        return // Caso o usuário não esteja autenticado, não renderiza a tela de chat
    }




    // Estado para as mensagens e para o texto digitado
    val messages = remember { mutableStateListOf<Pair<String,Boolean>>() }
    val newMessage = remember { mutableStateOf("") }
    val currentUserEmail = auth.currentUser?.email ?: ""

    // Carregar as mensagens quando a tela é aberta (isso pode ser feito com Firebase)
    LaunchedEffect(currentUserEmail, recipientEmail) {
        val safeSenderEmail = currentUserEmail.replace("[^a-zA-Z0-9_]".toRegex(), "_")
        val safeRecipientEmail = recipientEmail.replace("[^a-zA-Z0-9_]".toRegex(), "_")
        getMessagesFromDatabase(safeSenderEmail, safeRecipientEmail) { retrievedMessages ->
            Log.d("ChatScreen", "Mensagens recuperadas: $retrievedMessages")
            messages.clear()
            messages.addAll(retrievedMessages)
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // TopBar com o e-mail do destinatário
        TopAppBar(
            title = { Text(text = recipientEmail) },
            navigationIcon = {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color(0x8000FF00), // Verde translúcido
                titleContentColor = Color.White, // Cor do título
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Área de mensagens
        LazyColumn(
            modifier = Modifier.weight(1f),
            reverseLayout = false // Para mostrar a mensagem mais recente na parte superior
        ) {
            items(messages) { message ->
                val (messageContent, isSender) = message
                Row(
                    horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Box(
                        contentAlignment = if (isSender) Alignment.CenterEnd else Alignment.CenterStart,
                        modifier = Modifier
                            .background(
                                if (isSender) Color.Blue else Color.LightGray,
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(12.dp)
                    ) {
                        Text(
                            text = messageContent,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        // Campo de texto para digitar mensagens
        TextField(
            value = newMessage.value,
            onValueChange = { newMessage.value = it },
            label = { Text("Digite sua mensagem") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        // Botão de envio
        Button(
            onClick = {
                sendMessageToDatabase(currentUserEmail, recipientEmail, newMessage.value)
                if(newMessage.value.isNotBlank()) {

                    messages.add(0, newMessage.value to true)
                    newMessage.value = ""
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        ) {
            Text("Enviar")
        }
    }

    //Text(text = "teste")



}

data class Message(

    val content: String,
    val isSender: Boolean,
    val timestamp: Long
)

fun sendMessageToDatabase(senderEmail: String, recipientEmail: String, message: String) {

    val safeSenderEmail = senderEmail.replace("[^a-zA-Z0-9_]".toRegex(), "_")
    val safeRecipientEmail = recipientEmail.replace("[^a-zA-Z0-9_]".toRegex(), "_")


    val database = FirebaseDatabase.getInstance("https://socialstore-app-pdm-default-rtdb.europe-west1.firebasedatabase.app")
    val ref = database.getReference("chats")

    val chatRef = ref.child(safeSenderEmail).child(safeRecipientEmail)

    val messageData = hashMapOf(
        "message" to message,
        "timestamp" to System.currentTimeMillis()
    )

    Log.d("Firebase", "Enviando mensagem: $messageData")


    chatRef.push().setValue(messageData).addOnCompleteListener { task ->
        if (task.isSuccessful) {
            Log.d("Firebase", "Mensagem enviada com sucesso")
        } else {
            Log.e("Firebase", "Erro ao enviar mensagem: ${task.exception?.message}")
        }
    }
}

fun getMessagesFromDatabase(senderEmail: String, recipientEmail: String, onResult: (List<Pair<String, Boolean>>) -> Unit) {
    val database = FirebaseDatabase.getInstance("https://socialstore-app-pdm-default-rtdb.europe-west1.firebasedatabase.app")
    val ref = database.getReference("chats")

    val safeSenderEmail = senderEmail.replace("[^a-zA-Z0-9_]".toRegex(), "_")
    val safeRecipientEmail = recipientEmail.replace("[^a-zA-Z0-9_]".toRegex(), "_")

    // Referência para o chat entre remetente e destinatário
    val allMessages = mutableListOf<Message>()

    // Listener para buscar mensagens de sender -> recipient
    val senderToRecipientRef = ref.child(safeSenderEmail).child(safeRecipientEmail)
    senderToRecipientRef.addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            snapshot.children.forEach {
                val message = it.child("message").getValue(String::class.java)
                val timestamp = it.child("timestamp").getValue(Long::class.java)
                if (message != null && timestamp != null) {
                    allMessages.add(Message(message,true,timestamp))
                }
            }

            // Listener para buscar mensagens de recipient -> sender
            val recipientToSenderRef = ref.child(safeRecipientEmail).child(safeSenderEmail)
            recipientToSenderRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    snapshot.children.forEach {
                        val message = it.child("message").getValue(String::class.java)
                        val timestamp = it.child("timestamp").getValue(Long::class.java)
                        if (message != null && timestamp != null) {
                            allMessages.add(Message(message,false,timestamp))
                        }
                    }

                    // Ordenar todas as mensagens pelo timestamp
                    allMessages.sortBy { it.timestamp }

                    val mappedMessages = allMessages.map {it.content to it.isSender}
                    onResult(mappedMessages)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Erro ao recuperar mensagens", error.toException())
                    onResult(emptyList())
                }
            })
        }

        override fun onCancelled(error: DatabaseError) {
            Log.e("Firebase", "Erro ao recuperar mensagens", error.toException())
            onResult(emptyList())
        }
    })

}

//_--------------------------------------- teste renderizaçao nao funciona, serviço que nao podem ser simulados pelo preview??

/*
fun mockFirebaseInitialization(context: Context) {
    try {
        if (!isPreviewEnvironment()) {
            // Only initialize Firebase if it's not already initialized
            if (FirebaseApp.getApps(context).isEmpty()) {
                FirebaseApp.initializeApp(context)
            }
        }
    } catch (e: Exception) {
        // Catch exception in case it's already initialized or any other error
    }
}

fun mockFirebaseAuth(): FirebaseAuth {
    val mockAuth = FirebaseAuth.getInstance()

    // Mock the current user
    val mockUser = mock(FirebaseUser::class.java)
    mockAuth.signInWithCustomToken("mock-token") // Optionally mock any sign-in logic here

    // Define mock properties for the user
    `when`(mockUser.email).thenReturn("mockuser@example.com")
    `when`(mockUser.uid).thenReturn("mock-uid-123")

    // Mock other necessary methods or user info if needed
    val mockUserInfo = mock(UserInfo::class.java)
    `when`(mockUser.providerId).thenReturn("password")
    `when`(mockUser.displayName).thenReturn("Mock User")

    // Set the mocked user for the FirebaseAuth instance
    `when`(mockAuth.currentUser).thenReturn(mockUser)

    return mockAuth
    }


fun isPreviewEnvironment(): Boolean {
    return java.lang.Boolean.getBoolean("compose.preview")
}

@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    // Mocked NavController
    val context = LocalContext.current
    mockFirebaseInitialization(context)
    val mockAuth = mockFirebaseAuth()

    val navController = rememberNavController()



        // Define mock methods for messages and sending
    fun mockGetMessages(currentUserEmail: String, recipientEmail: String, callback: (List<String>) -> Unit) {
        callback(listOf("Hi!", "How are you?", "I'm good, thanks!"))
    }

    fun mockSendMessage(currentUserEmail: String, recipientEmail: String, message: String) {
        // No-op for preview purposes
    }

    // Use a wrapper composable to replace database calls with mocks
    CompositionLocalProvider(
        LocalGetMessages provides ::mockGetMessages,
        LocalSendMessage provides ::mockSendMessage
    ) {
        MaterialTheme {
            Surface {
                ChatScreen(
                    recipientEmail = "example@example.com",
                    senderEmail = "mockuser@example.com",
                    navController = navController,
                    onLogout = {},
                    auth = mockAuth
                )
            }
        }
    }
}
 */













/*
fun getMessagesFromFirestore(senderEmail: String, recipientEmail: String, onResult: (List<String>) -> Unit) {
    // Função para buscar mensagens entre dois utilizadores
    val db = FirebaseFirestore.getInstance()
    val chatRef = db.collection("chats")
        .document(senderEmail)
        .collection(recipientEmail)

    chatRef.get()
        .addOnSuccessListener { result ->
            val retrievedMessages = result.documents.mapNotNull { document ->
                document.getString("message") // Recupera a mensagem
            }
            onResult(retrievedMessages)
        }
        .addOnFailureListener { exception ->
            Log.e("Firestore", "Erro ao recuperar mensagens", exception)
            onResult(emptyList()) // Retorna lista vazia em caso de erro
        }
}

fun sendMessageToFirestore(senderEmail: String, recipientEmail: String, message: String) {
    // Função para enviar uma mensagem para o Firestore
    val db = FirebaseFirestore.getInstance()

    // Adiciona a mensagem na coleção de chats (pode ser em ambos os sentidos: sender -> recipient e recipient -> sender)
    val chatRefSender = db.collection("chats")
        .document(senderEmail)
        .collection(recipientEmail)
        .add(mapOf("message" to message))

    val chatRefRecipient = db.collection("chats")
        .document(recipientEmail)
        .collection(senderEmail)
        .add(mapOf("message" to message))

    // Você pode adicionar uma verificação de sucesso ou falha aqui, se necessário
    chatRefSender.addOnSuccessListener {
        Log.d("Firestore", "Mensagem enviada com sucesso")
    }.addOnFailureListener {
        Log.e("Firestore", "Erro ao enviar mensagem", it)
    }
}
 */