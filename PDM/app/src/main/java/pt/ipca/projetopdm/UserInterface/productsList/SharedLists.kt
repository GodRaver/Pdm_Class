package pt.ipca.projetopdm.UserInterface.productsList

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SharedLists(viewModel: FoodViewModel, onBack: () -> Unit, auth: FirebaseAuth) {
    val savedListsWithFoods by viewModel.savedListsWithFoods.observeAsState(emptyList())
    val currentUserId = auth.currentUser?.uid ?: return
    val savedLists by viewModel.savedListsWithFoods.observeAsState(emptyList())
    var selectedList by remember { mutableStateOf<SavedList?>(null) }

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
    Button(onClick = onBack, modifier = Modifier.padding(16.dp)) {
        Text("Voltar")
    }
}

@Composable
fun SelectListToShare(
    lists: List<SavedList>,
    onShare: (SavedList) -> Unit
) {
    LazyColumn {
        items(lists) { list ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onShare(list) }
                    .padding(16.dp)
            ) {
                Text(text = list.name, modifier = Modifier.weight(1f))
                Icon(imageVector = Icons.Default.Share, contentDescription = "Share")
            }
        }
    }
}

@Composable
fun ShareListWithUser(
    onShareWithUser: (String) -> Unit
) {
    var userId by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        TextField(
            value = userId,
            onValueChange = { userId = it },
            label = { Text("ID do Utilizador") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        Button(onClick = { onShareWithUser(userId) }) {
            Text("Compartilhar")
        }
    }
}

