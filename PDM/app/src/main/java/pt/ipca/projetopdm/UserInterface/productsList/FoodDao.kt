package pt.ipca.projetopdm.UserInterface.productsList

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.Update

@Dao
interface FoodDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFood(food: Food)

    @Update
    suspend fun updateFood(food: Food)

    @Query("SELECT * FROM foods")
    fun getAllFoods(): LiveData<List<Food>>

    //@Query("SELECT * FROM foods")  //redudante
    //suspend fun getAllFoodsList(): List<Food>

    @Query("DELETE FROM foods WHERE foodId = :id")
    suspend fun deleteFood(id: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveSelectedFoods(foods: List<Food>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSavedList(savedList: SavedList): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSavedListFoods(crossRefs: List<SavedListFoodCrossRef>)

    @Transaction
    @Query("SELECT * FROM SavedList")
    fun getSavedListsWithFoods(): LiveData<List<SavedListWithFoods>>

    @Query("SELECT * FROM foods WHERE foodId = :id")
    suspend fun getFoodById(id: Int): Food?

    @Query("DELETE FROM SavedList WHERE listId = :listId")
    suspend fun deleteSavedListById(listId: Int)

}



@Database(entities = [Food::class, SavedList::class, SavedListFoodCrossRef::class], version = 7)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodDao(): FoodDao

    companion object { //evitar criar multiplas instancias de bancos de dados
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "food_database"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}