package pt.ipca.projetopdm.UserInterface.productsList

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.Junction
import androidx.room.PrimaryKey
import androidx.room.Relation
import java.util.Date


@Entity(tableName = "foods")
data class Food(
    @PrimaryKey(autoGenerate = true)
    val foodId: Int = 0,
    val name: String)

@Entity(tableName = "saved_lists")
data class SavedList(
    @PrimaryKey(autoGenerate = true)
    val listId: Int = 0,
    val name: String,
    val createdAt: Date,
    val userId: String,
    val synced: Boolean = false,

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


@Entity(
    tableName = "shared_lists",
    foreignKeys = [ForeignKey(
        entity = SavedList::class,
        parentColumns = ["listId"],
        childColumns = ["listId"],
        onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["listId"])]
)
data class SharedList(
    @PrimaryKey
    val listId: Int,
    val ownerId: String,
    //val name: String,
    //val items: List<String>,
    val sharedWith: String
)

