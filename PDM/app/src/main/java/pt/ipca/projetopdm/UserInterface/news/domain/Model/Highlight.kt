package pt.ipca.projetopdm.UserInterface.news.domain.Model


import com.google.gson.annotations.SerializedName

data class Highlight(
    @SerializedName("highlight")
    val highlight: String,
    @SerializedName("highlighted_in")
    val highlightedIn: String,
    @SerializedName("sentiment")
    val sentiment: Double
)