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
import androidx.room.TypeConverters
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
    @Query("SELECT * FROM saved_lists")
    fun getSavedListsWithFoods(): LiveData<List<SavedListWithFoods>>

    @Query("SELECT * FROM foods WHERE foodId = :id")
    suspend fun getFoodById(id: Int): Food?

    @Query("DELETE FROM saved_lists WHERE listId = :listId")
    suspend fun deleteSavedListById(listId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedList(savedList: SavedList)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSavedLists(savedLists: List<SavedList>)

    @Query("SELECT * FROM saved_lists WHERE userId = :userId ORDER BY createdAt DESC")
    fun getSavedListsByUser(userId: String): LiveData<List<SavedList>>

    @Transaction
    @Query("SELECT * FROM saved_lists WHERE userId = :userId")
    fun getSavedListsWithFoodsByUser(userId: String): LiveData<List<SavedListWithFoods>>

    @Query("SELECT * FROM saved_lists WHERE synced = 0")
    suspend fun getUnsyncedSavedLists(): List<SavedList>

    @Query("UPDATE saved_lists SET synced = :status WHERE listId = :listId")
    suspend fun updateSyncedStatus(listId: Int, status: Boolean)


    @Query("SELECT * FROM shared_lists WHERE sharedWith LIKE '%' || :currentUserId || '%'")
    fun getSharedLists(currentUserId: String): LiveData<List<SharedList>>   //obter as listas compartilhadas diretamente do room

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSharedList(sharedList: SharedList)

    @Transaction
    @Query("""
        SELECT * FROM saved_lists
        INNER JOIN shared_lists ON saved_lists.listId = shared_lists.listId
        WHERE sharedWith LIKE '%' || :currentUserId || '%'
    """)
    fun getSharedListsWithDetails(currentUserId: String): LiveData<List<SavedListWithFoods>>

    @Query("DELETE FROM shared_lists WHERE listId = :listId")
    suspend fun deleteSharedList(listId: Int)

    @Query("SELECT * FROM shared_lists WHERE listId = :listId")
    suspend fun getSharedListByListId(listId: Int): SharedList?


}



@Database(entities = [Food::class, SavedList::class, SavedListFoodCrossRef::class, SharedList::class], version = 14)
@TypeConverters(ConverterDate::class)
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