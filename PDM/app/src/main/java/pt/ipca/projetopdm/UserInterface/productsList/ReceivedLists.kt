package pt.ipca.projetopdm.UserInterface.productsList

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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


fun ReceivedListsScreen(viewModel: FoodViewModel, onBack: () -> Unit, auth: FirebaseAuth) { }


/*
@Composable
fun ReceivedListsScreen(viewModel: FoodViewModel, onBack: () -> Unit, auth: FirebaseAuth) {




    val currentUserId = auth.currentUser?.uid ?: return

    //val sharedListsState by viewModel.sharedLists.observeAsState(emptyList())

    val sharedLists by viewModel.sharedLists.observeAsState(emptyList())


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
               items(sharedLists) { savedListWithFoods ->

                    SharedListItem(savedListWithFoods)
                    ////-------------------------------------------------------------------------------------------------------------------------------------
                }
            }
        }



        Button(
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Voltar")
        }
    }
}



@Composable
fun ReceivedListCard(
    sharedList: SharedList,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.elevatedCardElevation(4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Lista Compartilhada: ${sharedList.listId}",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(onClick = onViewDetails) {
                    Icon(Icons.Default.Info, contentDescription = "Detalhes")
                }
            }
        }
    }
}

@Composable
fun EmptyStateReceivedList(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nenhuma lista recebida ainda.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}











@Composable
fun SharedListItem(list: SharedList) {

    val listName = savedListWithFoods.savedList.name
    val ownerId = savedListWithFoods.savedList.userId
    val items = savedListWithFoods.foods.joinToString(", ") { it.name }


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(16.dp)
    ) {
        Text(text = "Nome da Lista: ${list.listId}",
            style = MaterialTheme.typography.bodyLarge
        )
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
