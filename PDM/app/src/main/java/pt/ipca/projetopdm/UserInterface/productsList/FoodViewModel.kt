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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FoodViewModel(application: Application) : AndroidViewModel(application) {

    private val foodDao: FoodDao = AppDatabase.getDatabase(application).foodDao()


    private val _foods = foodDao.getAllFoods()

    val foods: LiveData<List<Food>> = foodDao.getAllFoods()

    private val _selectedFoods = mutableStateListOf<Food>()
    val selectedFoods: List<Food> get() = _selectedFoods

    var isAddingProduct by mutableStateOf(false)
    private set

    val savedLists: LiveData<List<SavedListWithFoods>> = foodDao.getSavedListsWithFoods()


    val allFoods: LiveData<List<Food>> = foodDao.getAllFoods()

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
        viewModelScope.launch {
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
        viewModelScope.launch {
            val listId = foodDao.addSavedList(SavedList(name = name))
            val crossRefs = selectedFoods.map { SavedListFoodCrossRef(listId.toInt(), it.foodId) }
            foodDao.addSavedListFoods(crossRefs)
        }
    }

    fun deleteSavedList(listId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                foodDao.deleteSavedListById(listId)
            }
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

    }


class FoodViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FoodViewModel::class.java)) {
            return FoodViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

