package pt.ipca.projetopdm.Telas

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseUser
import pt.ipca.projetopdm.Components.SignUpText
import pt.ipca.projetopdm.R
//import pt.ipca.projetopdm.navigation.BackButtonHandler
//import pt.ipca.projetopdm.navigation.PostOfficeAppRouter
//import pt.ipca.projetopdm.navigation.Screen


private const val TERMS_TAG = "TermsAndConditionsScreen"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TermosCondicoesTela(onNavigateToSignUp: () -> Unit) {

    // BackButtonHandler {

    //   onNavigateToSignUp()


    // }

    // Estrutura principal da tela
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White)
            .padding(16.dp)
    ) {
        // Conteúdo da tela
        androidx.compose.foundation.layout.Column {
            Text(
                text = stringResource(id = R.string.terms_and_conditions_header),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = "\"On the other hand, we denounce with righteous indignation and dislike men who are so beguiled and demoralized by the charms of pleasure of the moment, so blinded by desire, that they cannot foresee the pain and trouble that are bound to ensue; and equal blame belongs to those who fail in their duty through weakness of will, which is the same as saying through shrinking from toil and pain. These cases are perfectly simple and easy to distinguish. In a free hour, when our power of choice is untrammelled and when nothing prevents our being able to do what we like best, every pleasure is to be welcomed and every pain avoided. But in certain circumstances and owing to the claims of duty or the obligations of business it will frequently occur that pleasures have to be repudiated and annoyances accepted. The wise man therefore always holds in these matters to this principle of selection: he rejects pleasures to secure other greater pleasures, or else he endures pains to avoid worse pains.\"\n" +
                        "\n"
            )

            Button(
                onClick = {
                    onNavigateToSignUp()
                },
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text("Go Back to Sign Up")
            }

            // Button(onClick = onNavigateBack) {
            //    Text("NavigateBack")

            // }


        }
    }


}