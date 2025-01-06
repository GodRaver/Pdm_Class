package pt.ipca.projetopdm.UserInterface.productsList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth

@Composable
fun ReceivedListsScreen(viewModel: FoodViewModel, onBack: () -> Unit, auth: FirebaseAuth) {


    val currentUserId = auth.currentUser?.uid ?: return

    //val sharedListsState by viewModel.sharedLists.observeAsState(emptyList())

    //val sharedLists by viewModel.sharedLists.observeAsState(emptyList())


    //val receivedLists = sharedLists.filter { it.sharedWith.contains(currentUserId) }



    //val selectedList = sharedLists.find { it.listId == targetId }



    LaunchedEffect(Unit) {
        viewModel.fetchSharedLists(currentUserId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Listas Compartilhadas por outros utilizadores",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        /*

        if (sharedLists.isEmpty()) {
            Text(
                text = "Nenhuma lista compartilhada encontrada.",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                receivedLists.forEach { list ->

                    SharedListItem(list = list)
                    ////-------------------------------------------------------------------------------------------------------------------------------------
                }
            }
        }
         */


        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Voltar")
        }
    }
}

/*

@Composable
fun SharedListItem(list: SharedList) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(text = list.name, style = MaterialTheme.typography.bodyLarge)
        Text(
            text = "Itens: ${list.items.joinToString(", ")}",
            style = MaterialTheme.typography.bodySmall
        )
        Text(
            text = "Compartilhado por: ${list.ownerId}",  // Display sharer's name
            style = MaterialTheme.typography.bodySmall
        )
    }
}


 */