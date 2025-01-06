package pt.ipca.projetopdm.UserInterface.profileEdit

import android.app.Activity
import android.content.pm.PackageManager
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import pt.ipca.projetopdm.R
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil3.compose.rememberAsyncImagePainter
import android.Manifest
import android.content.Context
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.compose.runtime.MutableState
import coil3.request.ImageRequest
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import coil.compose.rememberImagePainter
import coil3.compose.AsyncImage


const val REQUEST_CODE = 100

val auth = FirebaseAuth.getInstance()
val db = FirebaseFirestore.getInstance()
//val profileImageUrl = remember { mutableStateOf("") }




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEdit(
    auth: FirebaseAuth,
    onLogout: () -> Unit,
    onNavigateToSignUp: () -> Unit,
    onEditClick: (String) -> Unit,
    onPhotoSelected: (String) -> Unit,
    //userId: String,
    profileImageUrl: MutableState<String>

) {
    val context = LocalContext.current
    val activity = context as? Activity
    val painter = rememberImagePainter(
        data = profileImageUrl.value,
        builder = {
            crossfade(true)
            placeholder(R.drawable.wait)
            error(R.drawable.error)
        }
    )

    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        activity?.let {
            ActivityCompat.requestPermissions(
                it,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                REQUEST_CODE
            )
        }
    }

    val db = FirebaseFirestore.getInstance()
    val userProfile = remember { mutableStateOf<Map<String, String>>(emptyMap()) }
    val isLoading = remember { mutableStateOf(true) }
    //val profileImageUrl = remember { mutableStateOf("") }
    val userId = auth.currentUser?.uid ?: ""
    var lastUploadedImageUrl by remember { mutableStateOf<String?>(null) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri ->
            Log.d("ProfileEdit", "URI selecionada: $uri")

            val userId = auth.currentUser?.uid // Obtenha o userId

            Log.d("ProfileEdit", "User ID: $userId")
            if (userId == null) {
                Log.e("ProfileEdit", "Usuário não autenticado")
                return@rememberLauncherForActivityResult
            }

            val oldImageUrl = profileImageUrl.value
            val storageRef = FirebaseStorage.getInstance().reference

            uri?.let {

                if (oldImageUrl.isNotEmpty()) {
                    val imageRef = storageRef.child("${Uri.parse(oldImageUrl)?.lastPathSegment}")
                    imageRef.delete()
                        .addOnSuccessListener {
                            Log.d("ProfileEdit", "Imagem excluída com sucesso.")
                        }
                        .addOnFailureListener { exception ->
                            Log.e("ProfileEdit", "Erro ao excluir a imagem", exception)
                            Log.d("ProfileEdit", "Erro ao excluir imagem $imageRef")
                        }
                }


                uploadProfilePhoto(
                    context = context,
                    uri = it,
                    userId = userId,
                    onSuccess = { imageUrl ->


                        val updatedFields = mapOf("profileimageurl" to imageUrl)  // estava profileImageurl


                        profileImageUrl.value = "$imageUrl?t=${System.currentTimeMillis()}"
                        Log.d("ProfileEdit", "profileImageUrl.value atualizado: ${profileImageUrl.value}")

                        // Salva as mudanças no Firestore
                        saveProfileChanges(userId, updatedFields)

                        // Atualiza a interface com a nova URL
                        onPhotoSelected(imageUrl)
                    },
                    onFailure = { exception ->
                        Log.e("ProfileEdit", "Erro ao fazer upload da foto", exception)
                    }
                )
            }
        }
    )



    LaunchedEffect(auth) {
        loadUserProfile(auth, db) { profile ->
            userProfile.value = profile
            profileImageUrl.value = profile["profileImageUrl"] ?: ""
            isLoading.value = false
        }
    }

    LaunchedEffect(profileImageUrl.value) {
        Log.d("ProfileEdit", "imagem altera: ${profileImageUrl.value}")
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFE0F7FA))
    ) {
        val scrollState = rememberScrollState()


        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(
                contentAlignment = Alignment.BottomEnd,
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.Gray)
            ) {
                Log.d("ProfileEdit", "URL da imagem passada para AsyncImage: ${profileImageUrl.value}")

                Image(
                    painter = painter,
                    contentDescription = "Foto de Perfil",
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { launcher.launch("image/*") },
                    modifier = Modifier
                        .size(32.dp)
                        .background(Color.White, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Alterar Foto",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Campos editáveis
            val fields = listOf(
                "fullname" to (userProfile.value["fullname"] ?: ""),
                "email" to (userProfile.value["email"] ?: ""),
                "address" to (userProfile.value["address"] ?: ""),
                "password" to (userProfile.value["password"] ?: ""),
                "newEmail" to (userProfile.value["newEmail"] ?: ""), // Adicionar campo newEmail
                "newPassword" to (userProfile.value["newPassword"]
                    ?: "") // Adicionar campo newPassword


            )

            fields.forEach { (label, value) ->
                if (label == "password" || label == "newPassword") {
                    EditablePasswordField(
                        label = label,
                        initialValue = value,
                        onValueChange = { updatedValue ->
                            userProfile.value = userProfile.value.toMutableMap().apply {
                                this[label] = updatedValue
                            }
                        }
                    )
                } else {
                    EditableFieldWithTextField(
                        label = label,
                        initialValue = value,
                        onValueChange = { updatedValue ->
                            userProfile.value = userProfile.value.toMutableMap().apply {
                                this[label] = updatedValue
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }

            Button(
                onClick = {
                    val normalizedUserProfile = normalizeKeys(userProfile.value)

                    val currentEmail = normalizedUserProfile["email"] ?: ""
                    val newEmail = normalizedUserProfile["newemail"] ?: ""
                    val currentPassword = normalizedUserProfile["password"] ?: ""
                    val newPassword = normalizedUserProfile["newpassword"] ?: ""
                    val user = FirebaseAuth.getInstance().currentUser

                    if (currentPassword.isEmpty()) {
                        Toast.makeText(context, "A senha atual é obrigatória.", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    if (newPassword.isNotEmpty()) {
                        Log.d("ProfileEdit", "Atualizando senha...")
                        updatePassword(
                            currentPassword = currentPassword,
                            newPassword = newPassword,
                            onSuccess = {
                                Log.d("ProfileEdit", "Senha atualizada com sucesso.")
                                Toast.makeText(context, "Senha atualizada com sucesso.", Toast.LENGTH_SHORT).show()
                            },
                            onFailure = { error ->
                                Log.e("ProfileEdit", "Erro ao atualizar a senha: ${error.message}", error)
                                Toast.makeText(context, "Erro ao atualizar a senha: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    }

                    if (newEmail.isNotEmpty()) {
                        Log.d("ProfileEdit", "Atualizando e-mail...")
                        updateEmail(
                            context = context,
                            currentPassword = currentPassword,
                            newEmail = newEmail,
                            onSuccess = {
                                Log.d("ProfileEdit", "E-mail atualizado com sucesso.")
                                Toast.makeText(context, "E-mail atualizado com sucesso.", Toast.LENGTH_SHORT).show()

                                user!!.reload().addOnCompleteListener { reloadTask ->
                                    if (reloadTask.isSuccessful) {
                                        Log.d("ProfileEdit", "Usuário recarregado. Novo e-mail: ${user!!.email}")
                                    } else {
                                        Log.e("ProfileEdit", "Falha ao recarregar o usuário", reloadTask.exception)
                                    }
                                }
                            },
                            onFailure = { error ->
                                Log.e("ProfileEdit", "Erro ao atualizar o e-mail: ${error.message}", error)
                                Toast.makeText(context, "Erro ao atualizar o e-mail: ${error.message}", Toast.LENGTH_LONG).show()
                            }
                        )
                    }

                    if (newPassword.isEmpty() && newEmail.isEmpty()) {
                        Toast.makeText(context, "Nada para atualizar.", Toast.LENGTH_SHORT).show()
                    }

                    saveProfileChanges(
                        userId = user?.uid ?: "",
                        updatedFields = normalizeKeys(userProfile.value)
                    )
                    Toast.makeText(context, "Alterações salvas com sucesso", Toast.LENGTH_SHORT).show()
                    Log.d("ProfileEdit", "Conteúdo do userProfile: ${userProfile.value}")
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Salvar Alterações")
            }


        }
    }
}


    @Composable
    fun EditableFieldWithTextField(
        label: String,
        initialValue: String,
        onValueChange: (String) -> Unit
    ) {
        var textValue by remember { mutableStateOf(initialValue) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            TextField(
                value = textValue,
                onValueChange = {
                    textValue = it // Atualiza o valor local
                    onValueChange(it) // Atualiza o valor global
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White, MaterialTheme.shapes.medium)
                    .height(80.dp),
                singleLine = true
            )
        }
    }


    fun uploadProfilePhoto(
        context: Context,
        uri: Uri,
        userId: String,
        //profileImageUrl: MutableState<String>,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val storageRef = FirebaseStorage.getInstance().reference
            val photoRef = storageRef.child("profile_photos/$userId/${System.currentTimeMillis()}.jpg")

            Log.d("ProfileEdit", "Iniciando upload: $userId, Uri: $uri") // Log para debugar


            // Upload do arquivo
            val uploadTask = photoRef.putFile(uri)

            uploadTask.addOnSuccessListener {
                photoRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    Log.d("ProfileEdit", "Imagem upload com sucesso: $downloadUri")
                    onSuccess(downloadUri.toString())
                }.addOnFailureListener { exception ->
                    Log.e("ProfileEdit", "Erro ao obter o URL da imagem", exception)
                    onFailure(exception)
                }
            }.addOnFailureListener { exception ->
                Log.e("ProfileEdit", "Erro ao fazer upload da imagem", exception)
                onFailure(exception)
            }
        } catch (e: Exception) {
            Log.e("ProfileEdit", "Erro no processo de upload", e)
            onFailure(e)
        }
    }


    @Composable
    fun ProfileFields(userProfile: MutableState<Map<String, String>>) {
        val fields = listOf(
            "fullName" to (userProfile.value["fullname"] ?: ""),
            "email" to (userProfile.value["email"] ?: ""),
            "address" to (userProfile.value["address"] ?: ""),
            "password" to (userProfile.value["password"] ?: "")
        )

        fields.forEach { (label, value) ->
            EditableFieldWithTextField(
                label = label,
                initialValue = value,
                onValueChange = { updatedValue ->
                    userProfile.value = userProfile.value.toMutableMap().apply {
                        this[label] = updatedValue // Atualiza o valor no mapa
                    }
                }
            )
        }
    }


    fun normalizeKeys(fields: Map<String, String>): Map<String, String> {
        return fields.mapKeys { it.key.lowercase() }
    }

    fun getFileFromUri(context: Context, uri: Uri): String? {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (columnIndex != -1) {
                    return cursor.getString(columnIndex)
                }
            }
        }
        return null
    }


    @Composable
    fun ProfileImage() {
        val imageUrl = "https://picsum.photos/200/300"  // URL de exemplo de Picsum para teste de imagens
        val painter = rememberImagePainter(
            imageUrl,
            builder = {
                crossfade(true)
                error(R.drawable.error)  // Imagem de erro
                placeholder(R.drawable.wait)
            }
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.Gray)  // Fundo cinza caso a imagem não carregue
        ) {
            Image(
                painter = painter,
                contentDescription = "Foto de Perfil",
                modifier = Modifier.fillMaxSize()
            )
        }
    }

fun isValidEmail(email: String): Boolean {
    val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    return email.matches(emailRegex.toRegex())
}

@Composable
fun EditablePasswordField(
    label: String,
    initialValue: String,
    onValueChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 2.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        TextField(
            value = initialValue,
            onValueChange = onValueChange,
            label = { Text(label) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions.Default,
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White, MaterialTheme.shapes.medium)
                .height(80.dp),
            singleLine = true
        )
    }
}






/*
fun createOrUpdateUserProfile() {
    val currentUser = auth.currentUser

    // Verifique se o usuário está autenticado
    if (currentUser != null) {
        val userId = currentUser.uid

        // Verifique se o perfil do usuário já existe no Firestore
        val userRef = db.collection("users").document(userId)

        // Caso o perfil já exista, obtenha os dados
        userRef.get().addOnSuccessListener { document ->
            if (document.exists()) {
                // Se o perfil já existe, atualize os dados, se necessário
                val updatedData = mapOf(
                    "email" to (user.email ?: ""), // Garante que o email será sempre uma string, mesmo que seja null
                    "fullname" to (user.displayName ?: ""), // Se o displayName for nulo, passa uma string vazia
                    "profileImageUrl" to (user.photoUrl?.toString() ?: "")
                )

                userRef.update(updatedData) // Atualiza o documento
                    .addOnSuccessListener {
                        println("Perfil atualizado com sucesso!")
                    }
                    .addOnFailureListener {
                        println("Erro ao atualizar o perfil: ${it.message}")
                    }

            } else {
                // Se o perfil não existe, crie um novo documento
                val newUserProfile = mapOf(
                    "email" to (user.email ?: ""), // Garantir que o email seja sempre uma string
                    "fullname" to (user.displayName ?: ""), // Garantir que o fullname seja sempre uma string
                    "profileImageUrl" to (user.photoUrl?.toString() ?: ""), // Garantir que a URL da foto seja sempre uma string
                    "address" to ""
                )

                userRef.set(newUserProfile) // Cria o perfil no Firestore
                    .addOnSuccessListener {
                        println("Perfil criado com sucesso!")
                    }
                    .addOnFailureListener {
                        println("Erro ao criar perfil: ${it.message}")
                    }
            }
        }
    } else {
        println("Nenhum usuário autenticado.")
    }
}
 */

/*
fun uploadProfilePhoto(
    filePath: String, // URI do arquivo selecionado
    userId: String, // ID do usuário autenticado
    onSuccess: (String) -> Unit, // Callback para retorno da URL da foto
    onFailure: (Exception) -> Unit // Callback para erros
) {
    val storageRef = FirebaseStorage.getInstance().reference.child("")
    val photoRef = storageRef.child("profile_photos/$userId.jpg")

    // Converter o caminho do arquivo para URI
    val fileUri = Uri.parse(filePath)


    val uploadTask = storageRef.putFile(Uri.parse(filePath))
    uploadTask.addOnSuccessListener {
        // Recuperar a URL da imagem armazenada
        storageRef.downloadUrl.addOnSuccessListener { uri ->
            onSuccess(uri.toString())
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }.addOnFailureListener { exception ->
        onFailure(exception)
    }
}
 */

