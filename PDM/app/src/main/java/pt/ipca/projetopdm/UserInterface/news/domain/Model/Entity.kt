package pt.ipca.projetopdm.UserInterface.news.domain.Model


import com.google.gson.annotations.SerializedName

data class Entity(
    @SerializedName("country")
    val country: String,
    @SerializedName("exchange")
    val exchange: Any,
    @SerializedName("exchange_long")
    val exchangeLong: Any,
    @SerializedName("highlights")
    val highlights: List<Highlight>,
    @SerializedName("industry")
    val industry: String,
    @SerializedName("match_score")
    val matchScore: Double,
    @SerializedName("name")
    val name: String,
    @SerializedName("sentiment_score")
    val sentimentScore: Double,
    @SerializedName("symbol")
    val symbol: String,
    @SerializedName("type")
    val type: String
)