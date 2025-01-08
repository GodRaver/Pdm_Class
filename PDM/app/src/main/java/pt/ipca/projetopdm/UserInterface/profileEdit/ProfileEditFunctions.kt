package pt.ipca.projetopdm.UserInterface.profileEdit

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.*
import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage


fun updateUserProfile(userId: String, updatedFields: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("Utilizadores").document(userId)
        .update(updatedFields)
        .addOnSuccessListener {
            Log.d("Firestore", "Perfil atualizado com sucesso!")
            onSuccess()
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Erro ao atualizar perfil", e)
            onFailure(e)
        }
}

fun loadUserProfile(auth: FirebaseAuth, db: FirebaseFirestore, onProfileLoaded: (Map<String, String>) -> Unit) {
    //val auth = FirebaseAuth.getInstance()
    //val db = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid

    if (userId != null) {
        db.collection("Utilizadores").document(userId).get()
            .addOnSuccessListener { document ->
                val userProfile = mutableMapOf<String, String>()
                document.data?.forEach { (key, value) ->
                    userProfile[key] = value.toString()
                }
                onProfileLoaded(userProfile)
            }
            .addOnFailureListener { exception ->
                Log.e("ProfileEdit", "Erro ao carregar o perfil do user", exception)
            }
    }
}


fun saveProfileChanges(userId: String, updatedFields: Map<String, Any>) {
    val db = FirebaseFirestore.getInstance()
    val userRef = db.collection("Utilizadores").document(userId)


    userRef
        .update(updatedFields)
        .addOnSuccessListener {
            Log.d("Firestore", "Perfil atualizado com sucesso! $userRef")
        }
        .addOnFailureListener { e ->
            Log.e("Firestore", "Erro ao atualizar perfil", e)
        }
}



fun updateEmail(
    context: Context,
    currentPassword: String,
    newEmail: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val currentEmail = user?.email

    if (user == null) {
        Log.e("ProfileEdit", "Utilizador não autenticado")
        Toast.makeText(context, "Você precisa estar autenticado para alterar o e-mail", Toast.LENGTH_SHORT).show()
        onFailure(Exception("Utilizador não autenticado"))
        return
    }


    if (user.isEmailVerified) {
        Log.d("ProfileEdit", "O e-mail atual é: $currentEmail")
        Log.d("ProfileEdit", "E-mail verificado, pode ser alterado")
    } else {
        // Enviar link de verificação
        user.sendEmailVerification()
            .addOnSuccessListener {
                Log.d("ProfileEdit", "Link de verificação enviado para $currentEmail")
                Toast.makeText(context, "Verifique seu e-mail e clique no link de confirmação antes de continuar.", Toast.LENGTH_LONG).show()
                onFailure(Exception("E-mail não verificado"))
            }
            .addOnFailureListener {
                Log.e("ProfileEdit", "Falha ao enviar link de verificação", it)
                onFailure(it)
            }
        return
    }

    val credential = EmailAuthProvider.getCredential(user?.email!!, currentPassword)
    val providerData = user?.providerData
    Log.d("ProfileEdit", "ProviderData: ${providerData?.joinToString() ?: "null"}")
    val isExternalProvider = providerData?.any { it.providerId != "password" } == true

    Log.d("ProfileEdit", "isExternalProvider: $isExternalProvider")

    user.reauthenticate(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("ProfileEdit", "Reautenticação bem-sucedida")
                user.verifyBeforeUpdateEmail(newEmail)
                    .addOnSuccessListener {
                        // Atualizar no Firestore
                        Log.d("Profile Edit", "enviado para verificar email antes do update")
                        FirebaseFirestore.getInstance()
                            .collection("Utilizadores")
                            .document(user.uid)
                            .update("email", newEmail)
                            .addOnSuccessListener {
                                Log.d("ProfileEdit", "E-mail atualizado no Firestore")
                                onSuccess()
                            }
                            .addOnFailureListener {
                                Log.e("ProfileEdit", "Falha ao atualizar e-mail no Firestore", it)
                                onFailure(it)
                            }
                    }
                    .addOnFailureListener { onFailure(it) }
            } else {
                Log.e("ProfileEdit", "Falha na reautenticação", task.exception)
                onFailure(task.exception ?: Exception("Reauthentication failed"))
            }
        }
}



fun updatePassword(
    currentPassword: String,
    newPassword: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    val credential = EmailAuthProvider.getCredential(user?.email!!, currentPassword)
    user.reauthenticate(credential)
        .addOnCompleteListener { task ->
            if (task.isSuccessful) {
                user.updatePassword(newPassword)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onFailure(it) }
            } else {
                onFailure(task.exception ?: Exception("Reauthentication failed"))
            }
        }
}

fun uploadImageToStorage(userId: String, imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference.child("profile_photos/$userId.jpg")

    storageRef.putFile(imageUri)
        .addOnSuccessListener { taskSnapshot ->
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                onSuccess(imageUrl) // URL da imagem
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception) // Falha no upload
        }
}

fun saveUserProfileImage(userId: String, imageUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("Utilizadores").document(userId)
        .update("profileImageUrl", imageUrl)
        .addOnSuccessListener {
            onSuccess() // Sucesso na atualização
        }
        .addOnFailureListener { e ->
            onFailure(e) // Falha na atualização
        }
}

fun handleProfileImageUpdate(userId: String, imageUri: Uri, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    uploadImageToStorage(userId, imageUri, { imageUrl ->
        saveUserProfileImage(userId, imageUrl, onSuccess, onFailure)
    }, { exception ->
        onFailure(exception) // Falha no upload da imagem
    })
}

/*

fun uploadImageToStorage(userId: String, imageUri: Uri, onSuccess: (String) -> Unit, onFailure: (Exception) -> Unit) {
    val storageRef = FirebaseStorage.getInstance().reference.child("user_images/$userId/profile.jpg")

    storageRef.putFile(imageUri)
        .addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                val imageUrl = uri.toString()
                onSuccess(imageUrl) // Retorna a URL da imagem
            }
        }
        .addOnFailureListener { exception ->
            onFailure(exception) // Falha no upload
        }
}
 */



fun updateUserImage(userId: String, imageUrl: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("Utilizadores").document(userId)
        .update("profileImageUrl", imageUrl)
        .addOnSuccessListener {
            onSuccess() // Sucesso ao atualizar
        }
        .addOnFailureListener { e ->
            onFailure(e) // Falha ao salvar a URL
        }
}

fun uploadAndSaveUserImage(userId: String, imageUri: Uri, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
    uploadImageToStorage(userId, imageUri, { imageUrl ->
        updateUserImage(userId, imageUrl, onSuccess, onFailure)
    }, { exception ->
        onFailure(exception) // Falha no upload
    })
}

