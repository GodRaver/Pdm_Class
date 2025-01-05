package pt.ipca.projetopdm.UserInterface.productsList

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Date

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val foodDao: FoodDao = AppDatabase.getDatabase(application).foodDao()


    private val _foods = foodDao.getAllFoods()

    val foods: LiveData<List<Food>> = foodDao.getAllFoods()

    private val _selectedFoods = mutableStateListOf<Food>()
    val selectedFoods: List<Food> get() = _selectedFoods

    private val _savedListsWithFoods = MutableLiveData<List<SavedListWithFoods>>()


    //val savedListsWithFoods: LiveData<List<SavedListWithFoods>> = _savedListsWithFoods



    var isAddingProduct by mutableStateOf(false)
        private set

    //val savedListsWithFoods: LiveData<List<SavedListWithFoods>> = foodDao.getSavedListsWithFoods()

    val savedListsWithFoods: LiveData<List<SavedListWithFoods>> = liveData {
        currentUserId?.let { userId ->
            emitSource(foodDao.getSavedListsWithFoodsByUser(userId))
        } ?: emit(emptyList())
    }


    val allFoods: LiveData<List<Food>> = foodDao.getAllFoods()

    val currentUserId: String?
        get() = FirebaseAuth.getInstance().currentUser?.uid

    //private val _savedLists = MutableLiveData<List<SavedList>>()


    //val savedLists: LiveData<List<SavedList>> get() = _savedLists


    /*

    init {
        // Carregar os itens do DAO ao iniciar o ViewModel
        loadFoods()
    }


    private fun loadFoods() {
        viewModelScope.launch {

            _foods.value = foodDao.getAllFoods()
        }
    }
 */

    fun setAddingProductState(state: Boolean) {
        isAddingProduct = state
    }

    fun saveSelectedFoods(selectedFoodList: List<Food>) {
        // Pode ser um banco de dados ou outras ações, como um log
        viewModelScope.launch {
            foodDao.saveSelectedFoods(selectedFoodList)
        }
    }


    fun addFood(food: Food) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                foodDao.addFood(food)
                //loadFoods()
            }
        }

    }

    fun updateFood(id: Int, newName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            // Buscando o alimento no banco de dados de forma assíncrona
            val foodToUpdate = foodDao.getFoodById(id)

            if (foodToUpdate != null) {
                Log.d("FoodViewModel", "Atualizando alimento com ID: $id para o nome: $newName")
                val updatedFood = foodToUpdate.copy(name = newName)
                foodDao.updateFood(updatedFood)
                Log.d("FoodViewModel", "Alimento atualizado com sucesso: ${updatedFood.name}")
            } else {
                Log.e("FoodViewModel", "Alimento não encontrado com ID: $id")
            }
        }
    }


    fun deleteFood(id: Int) {
        viewModelScope.launch {
            foodDao.deleteFood(id) // Atualização automática via LiveData
        }
    }

    fun saveList(name: String) {
        val userId = currentUserId ?: run {
            Log.e("FoodViewModel", "Usuário não autenticado. Não é possível salvar a lista.")
            return
        }
        viewModelScope.launch {
            val createdAt = Date()
            val listId = foodDao.addSavedList(SavedList(name = name, userId = userId, createdAt = createdAt))
            val listIdInt = listId.toInt()
            val crossRefs = selectedFoods.map { SavedListFoodCrossRef(listId.toInt(), it.foodId) }
            foodDao.addSavedListFoods(crossRefs)  //----------------------------------------------------------------------------------------------

            val savedList = SavedList(listId = listIdInt, name = name, createdAt = createdAt, userId = userId)
            syncListToFirestore(savedList) // Enviar para Firestore
        }
    }



    fun createSavedList(name: String) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val newSavedList = SavedList(
            name = name,
            createdAt = Date(),
            userId = userId
        )
        viewModelScope.launch {
            foodDao.insertSavedList(newSavedList)
        }
    }


    fun deleteSavedList(listId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                foodDao.deleteSavedListById(listId)
            }
        }
    }


    /*
    fun fetchSharedLists(currentUserId: String) {
        FirebaseFirestore.getInstance()
            .collection("shared_lists")
            .whereArrayContains("sharedWith", currentUserId) // Verifica se o ID do usuário está incluído
            .get()
            .addOnSuccessListener { result ->
                val sharedLists = result.toObjects(SavedList::class.java)
                viewModelScope.launch {
                    foodDao.insertSavedLists(sharedLists) // Sincroniza com o Room
                }
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Erro ao buscar listas compartilhadas", exception)
            }
    }
     */

    fun fetchSharedLists(currentUserId: String) {
        val firestore = FirebaseFirestore.getInstance()

        firestore.collection("shared_lists")
            .whereArrayContains("sharedWith", currentUserId) // Busca listas compartilhadas com o usuário atual
            .get()
            .addOnSuccessListener { result ->
                val sharedLists = result.map { document ->
                    val savedList = document.toObject(SavedList::class.java)
                    val foods = document["foods"] as? List<Map<String, Any>> ?: emptyList()

                    val foodItems = foods.map { foodMap ->
                        Food(
                            foodId = (foodMap["foodId"] as? Long)?.toInt() ?: 0,
                            name = foodMap["name"] as? String ?: ""
                        )
                    }

                    SavedListWithFoods(
                        savedList = savedList,
                        foods = foodItems
                    )

                }
                _savedListsWithFoods.postValue(sharedLists)
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreError", "Erro ao buscar listas partilhadas", exception)
            }
    }


    fun toggleSelection(food: Food, isSelected: Boolean) {
        if (isSelected) {
            _selectedFoods.add(food)
        } else {
            _selectedFoods.remove(food)
        }
    }

    fun saveSelectedFoods() {

        println("Itens Selecionados: ${_selectedFoods.joinToString { it.name }}")
    }


    fun syncListToFirestore(savedList: SavedList) {
        val userId = currentUserId ?: return
        FirebaseFirestore.getInstance()
            .collection("Utilizadores")
            .document(userId)
            .collection("listas_de_compras")
            .document(savedList.listId.toString())
            .set(savedList)
            .addOnSuccessListener {
                Log.d("Firestore", "Lista sincronizada com sucesso: ${savedList.name}")
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao sincronizar lista", e)
            }
    }

    fun syncSavedListsWithFirebase() {
        val userId = currentUserId ?: return
        FirebaseFirestore.getInstance()
            .collection("Utilizadores")
            .document(userId)
            .collection("listas_de_compras")
            .get()
            .addOnSuccessListener { snapshot ->
                val savedLists = snapshot.toObjects(SavedList::class.java)
                viewModelScope.launch {
                    foodDao.insertSavedLists(savedLists)
                }
            }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Erro ao sincronizar listas do Firestore", e)
            }
    }

    fun listenToSavedLists() {
        val userId = currentUserId ?: return
        FirebaseFirestore.getInstance()
            .collection("Utilizadores")
            .document(userId)
            .collection("listas_de_compras")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.e("Firestore", "Erro ao ouvir listas salvas", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val savedLists = snapshot.toObjects(SavedList::class.java)
                    viewModelScope.launch {
                        foodDao.insertSavedLists(savedLists)
                    }
                }
            }
    }

    fun syncUnsyncedLists() {
        viewModelScope.launch {
            val unsyncedLists = foodDao.getUnsyncedSavedLists()
            unsyncedLists.forEach { savedList ->
                syncListToFirestore(savedList)
            }
        }
    }

    fun saveSharedListsLocally(sharedLists: List<SavedListWithFoods>) {
        viewModelScope.launch {
            sharedLists.forEach { savedListWithFoods ->
                foodDao.insertSavedList(savedListWithFoods.savedList)
                val crossRefs = savedListWithFoods.foods.map { food ->
                    SavedListFoodCrossRef(savedListWithFoods.savedList.listId, food.foodId)
                }
                foodDao.addSavedListFoods(crossRefs)
            }
        }
    }

    fun shareListWithUser(listId: Int, userIdToShare: String) {
        val firestore = FirebaseFirestore.getInstance()

        // Referência à lista compartilhada no Firestore
        firestore.collection("shared_lists")
            .document(listId.toString())
            .update("sharedWith", FieldValue.arrayUnion(userIdToShare)) // Adiciona o utilizador à lista
            .addOnSuccessListener {
                Log.d("ShareList", "Lista $listId compartilhada com o utilizador $userIdToShare")
            }
            .addOnFailureListener { e ->
                Log.e("ShareList", "Erro ao compartilhar a lista", e)
            }
    }



}





class FoodViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            return FoodViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

