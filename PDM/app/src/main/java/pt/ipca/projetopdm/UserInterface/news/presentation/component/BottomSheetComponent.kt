package pt.ipca.projetopdm.UserInterface.news.presentation.component

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Data
import pt.ipca.projetopdm.UserInterface.news.domain.Model.Similar
import pt.ipca.projetopdm.UserInterface.news.presentation.component.ImageHolder

@Composable
fun BottomSheetComponent(

    article: Data,
    //Sim: Similar,
    onReadFullStoryButtonClicked: (String) -> Unit

) {

    Surface(modifier = Modifier.padding(16.dp)) {

        Log.d("BottomSheetComponent", "Recebendo artigo: ${article.title}")
        Column(horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {

            Text(text = article.title ?: "Sem titulo", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = article.source ?: "Fonte Desconhecida", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))
            ImageHolder(imageUrl = article.imageUrl ?: "url_default_image")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = article.snippet ?: "Sem descricao", style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(8.dp))

            /*
            Row(

                modifier = Modifier
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = article.orgFacet.joinToString(", "),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
             */


            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = {
                onReadFullStoryButtonClicked(article.url)
                             },
                modifier = Modifier.fillMaxWidth()

            ) {

                Text(text = "Read full Story")
            }
        }
    }
}