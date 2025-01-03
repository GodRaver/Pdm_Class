package pt.ipca.projetopdm.UserInterface.news.presentation.component

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
import pt.ipca.projetopdm.UserInterface.news.util.DateFormatter



@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun NewsArticleCard(

    modifier: Modifier = Modifier,
    data: Data,
    onCardClicked: (Data) -> Unit

) {


    //val date = DateFormatter(data.publishedAt)

    val formattedDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        DateFormatter(data.publishedAt)  //api 26 ou superior
    } else {
        data.publishedAt
    }



    Card(
        modifier = modifier.clickable { onCardClicked(data) }
    ) {

        Column(

            modifier = modifier.padding(12.dp)
        ) {
            ImageHolder(imageUrl = data.imageUrl)   //     ------
            Text(

                text = data.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,

                )
            Spacer(modifier = modifier.height(8.dp))
            Row(

                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(

                    text = formattedDate,
                    style = MaterialTheme.typography.bodySmall,

                    )

                Text(

                    text = data.description ?: "",
                    style = MaterialTheme.typography.bodySmall,

                    )

            }
        }
    }

}

