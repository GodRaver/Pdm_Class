package pt.ipca.projetopdm.UserInterface.productsList


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.room.Room
import pt.ipca.projetopdm.UserInterface.Routes
import pt.ipca.projetopdm.UserInterface.productsList.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsList(
    auth: FirebaseAuth,        // Instância do Firebase Authentication
    onLogout: () -> Unit,      // Callback de logout
    onNavigateToSignUp: () -> Unit, // Navegação para tela de registro
    onEditClick: (String) -> Unit
) {






}




@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FoodScreen(
    viewModel: FoodViewModel,
    auth: FirebaseAuth,
    navController: NavController,
    onNavigateToSavedLists: () -> Unit,
    onAddNewFood: () -> Unit
) {
    val isAddingProduct = viewModel.isAddingProduct
    val foods by viewModel.foods.observeAsState(emptyList())
    val selectedFoods = viewModel.selectedFoods
    val savedLists by viewModel.savedLists.observeAsState(emptyList())

    val onSaveList = {
        val listName = "Lista criada com ID: ${System.currentTimeMillis()}"
        viewModel.saveList(listName)
    }

    if (isAddingProduct) {
        AddProductScreen(
            onSave = { productName ->
                val newFood = Food(name = productName)
                viewModel.addFood(newFood)
                viewModel.setAddingProductState(false)
            },
            onCancel = {
                viewModel.setAddingProductState(false)
            }
        )
    } else {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Lista de Alimentos", style = MaterialTheme.typography.titleLarge) },
                    navigationIcon = {
                        IconButton(onClick = { auth.signOut() }) {
                            Icon(Icons.Default.Logout, contentDescription = "Logout")
                        }
                    },
                    actions = {
                        IconButton(onClick = onNavigateToSavedLists) {
                            Icon(Icons.Default.List, contentDescription = "Listas Salvas")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = onAddNewFood,
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Adicionar Alimento")
                }
            },
            bottomBar = {
                if (selectedFoods.isNotEmpty()) {
                    Button(
                        onClick = onSaveList,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("Salvar Lista", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        ) { padding ->
            if (foods.isEmpty()) {
                EmptyState(padding)
            } else {
                FoodList(
                    foods = foods,
                    onDeleteFood = { viewModel.deleteFood(it.foodId) },
                    onEditFood = { id, newName -> viewModel.updateFood(id, newName) },
                    onToggleSelection = { food, isSelected ->
                        viewModel.toggleSelection(food, isSelected)
                    },
                    padding = padding,
                    selectedFoods = selectedFoods
                )
            }
        }
    }
}




@Composable
fun AddProductScreen(
    onSave: (String) -> Unit, // Callback para salvar o produto
    onCancel: () -> Unit      // Callback para voltar à lista
) {
    var productName by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = productName,
            onValueChange = { productName = it },
            label = { Text("Nome do Produto") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(onClick = onCancel) {
                Text("Cancelar")
            }
            Button(
                onClick = {
                    if (productName.isNotBlank()) {
                        onSave(productName)
                    }
                }
            ) {
                Text("Salvar")
            }
        }
    }
}

/*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedListsScreen(viewModel: FoodViewModel, onBack: () -> Unit) {
    val savedLists by viewModel.savedLists.observeAsState(emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Listas Salvas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            contentPadding = padding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(savedLists) { listWithFoods ->
                Text(text = listWithFoods.savedList.name, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                listWithFoods.foods.forEach { food ->
                    Text(text = "- ${food.name}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
            }
        }
    }
}
 */


@Composable
fun FoodItem(
    food: Food,
    onEdit: (String) -> Unit,
    onDelete: () -> Unit,
    isSelected: Boolean,
    onSelectionChange: (Boolean) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(food.name) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (isEditing) {
            TextField(
                value = newName,
                onValueChange = { newName = it },
                modifier = Modifier.weight(1f),
                label = { Text("Editar nome") }
            )
            IconButton(onClick = {
                onEdit(newName)
                isEditing = false
            }) {
                Icon(imageVector = Icons.Default.Check, contentDescription = "Salvar edição")
            }
        } else {
            Text(
                text = food.name,
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { isEditing = true }) {
                Icon(imageVector = Icons.Default.Edit, contentDescription = "Editar")
            }
        }
        IconButton(onClick = onDelete) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Apagar")
        }

        Checkbox(
            checked = isSelected,
            onCheckedChange = onSelectionChange,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}


@Composable
fun EmptyState(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(
                imageVector = Icons.Default.Restaurant,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Nenhum alimento adicionado ainda.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun FoodList(
    foods: List<Food>,
    onDeleteFood: (Food) -> Unit,
    onEditFood: (Int, String) -> Unit,
    onToggleSelection: (Food, Boolean) -> Unit,
    padding: PaddingValues,
    selectedFoods: List<Food>
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        items(foods) { food ->
            val isSelected = selectedFoods.contains(food)
            FoodItem(
                food = food,
                onEdit = { newName -> onEditFood(food.foodId, newName) },
                onDelete = { onDeleteFood(food) },
                isSelected = isSelected,
                onSelectionChange = { isSelected ->
                    onToggleSelection(food, isSelected)
                }
            )
        }
    }
}



@Composable
fun ProductCard(
    food: Food,
    onDelete: () -> Unit,
    onEdit: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }
    var newName by remember { mutableStateOf(food.name) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isEditing) {
                TextField(
                    value = newName,
                    onValueChange = { newName = it },
                    label = { Text("Editar nome do produto") },
                    modifier = Modifier.fillMaxWidth()
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { isEditing = false }) {
                        Text("Cancelar")
                    }
                    TextButton(onClick = {
                        onEdit(newName)
                        isEditing = false
                    }) {
                        Text("Salvar")
                    }
                }
            } else {
                Text(
                    text = food.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    IconButton(onClick = { isEditing = true }) {
                        Icon(Icons.Default.Edit, contentDescription = "Editar")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Apagar")
                    }
                }
            }
        }
    }
}