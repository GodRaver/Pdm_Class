package pt.ipca.projetopdm

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import pt.ipca.projetopdm.UserInterface.NavControllerNavigation
import pt.ipca.projetopdm.app.AutenticacaoApp
import pt.ipca.projetopdm.ui.theme.ProjetoPDMTheme

class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseApp.initializeApp(this)


        auth = FirebaseAuth.getInstance()

        setContent {


            NavControllerNavigation(auth = auth)
            }



        }






    }

