package pt.ipca.projetopdm.UserInterface.productsList

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation


@Entity(tableName = "foods")
data class Food(
    @PrimaryKey(autoGenerate = true)
    val foodId: Int = 0,
    val name: String)

@Entity
data class SavedList(
    @PrimaryKey(autoGenerate = true)
    val listId: Int = 0,
    val name: String
)

@Entity(primaryKeys = ["listId", "foodId"])
data class SavedListFoodCrossRef(
    val listId: Int,
    val foodId: Int
)

data class SavedListWithFoods(
    @Embedded val savedList: SavedList,
    @Relation(
        parentColumn = "listId",
        entityColumn = "foodId",
        associateBy = Junction(SavedListFoodCrossRef::class)
    )
    val foods: List<Food>
)