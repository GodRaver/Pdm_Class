package pt.ipca.projetopdm.UserInterface.productsList

import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.ColumnScopeInstance.weight
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
//import kotlinx.coroutines.flow.internal.NoOpContinuation.context

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedListsScreen(viewModel: FoodViewModel, onBack: () -> Unit, auth: FirebaseAuth) {
    val savedListsWithFoods by viewModel.savedListsWithFoods.observeAsState(emptyList())
    val currentUserId = auth.currentUser?.uid ?: return
    val savedLists by viewModel.savedListsWithFoods.observeAsState(emptyList())
    var selectedList by remember { mutableStateOf<SavedList?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween // Espaça os elementos verticalmente
    ) {
        if (selectedList == null) {
            // Selecionar uma lista
            SelectListToShare(
                lists = savedLists.map { it.savedList },
                onShare = { list -> selectedList = list }
            )
        } else {
            // Selecionar um utilizador para compartilhar
            ShareListWithUser(
                onShareWithUser = { userId ->
                    selectedList?.let {
                        viewModel.shareListWithUser(it.listId, userId)
                        selectedList = null // Reset após compartilhar
                    }
                }
            )
        }

        // Botão para voltar
        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Voltar")
        }
    }
}

@Composable
fun SelectListToShare(
    lists: List<SavedList>,
    onShare: (SavedList) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Escolhe uma lista para compartilhar com outros utiizadores:",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp) // Espaçamento entre os itens
        ) {
            items(lists) { list ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onShare(list) }
                        .padding(8.dp)
                ) {
                    Text(
                        text = list.name,
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
                }
            }
        }
    }
}

@Composable
fun ShareListWithUser(
    onShareWithUser: (String) -> Unit
) {
    var userEmail by remember { mutableStateOf("") }
    val context = LocalContext.current


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Compartilhar com outro utilizador",
            style = MaterialTheme.typography.titleMedium
        )

        TextField(
            value = userEmail,
            onValueChange = { userEmail = it },
            label = { Text("Insira o email para enviar: ") },
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {
            getUserIdByEmail(userEmail) { userId ->
                if (userId != null) {
                    onShareWithUser(userId)
                } else {
                    Toast.makeText(context, "Utilizador não encontrado.", Toast.LENGTH_SHORT).show()
                }
            }
        }) {
            Text("Compartilhar")
        }
    }
}

fun getUserIdByEmail(email: String, onUserFound: (String?) -> Unit) {
    val db = FirebaseFirestore.getInstance()

    db.collection("Utilizadores")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                val userId = documents.first().id  // userId é a ID do documento
                onUserFound(userId)
            } else {
                // Nenhum utilizador encontrado com esse email
                onUserFound(null)
            }
        }
        .addOnFailureListener {
            // Tratar erro de consulta
            onUserFound(null)
        }
}

