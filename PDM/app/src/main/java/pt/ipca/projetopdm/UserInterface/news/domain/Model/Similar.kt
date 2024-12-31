package pt.ipca.projetopdm.UserInterface.news.domain.Model


import com.google.gson.annotations.SerializedName
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Entity

data class Similar(
    @SerializedName("description")
    val description: String,
    @SerializedName("entities")
    val entities: List<Entity>,
    @SerializedName("image_url")
    val imageUrl: String,
    @SerializedName("keywords")
    val keywords: String,
    @SerializedName("language")
    val language: String,
    @SerializedName("published_at")
    val publishedAt: String,
    @SerializedName("relevance_score")
    val relevanceScore: Any,
    @SerializedName("snippet")
    val snippet: String,
    @SerializedName("source")
    val source: String,
    @SerializedName("title")
    val title: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("uuid")
    val uuid: String
)