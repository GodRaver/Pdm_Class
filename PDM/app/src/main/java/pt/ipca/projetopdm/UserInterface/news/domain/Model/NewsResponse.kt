package pt.ipca.projetopdm.UserInterface.news.domain.Model


import com.google.gson.annotations.SerializedName
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Meta

data class NewsResponse(
    @SerializedName("data")
    val `data`: List<Data>,
    @SerializedName("meta")
    val meta: Meta
)