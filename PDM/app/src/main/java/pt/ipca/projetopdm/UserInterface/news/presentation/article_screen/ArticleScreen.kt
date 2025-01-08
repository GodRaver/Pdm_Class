package pt.ipca.projetopdm.UserInterface.news.presentation.article_screen

import android.util.Log
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.viewinterop.AndroidView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleScreen(

    url: String?,
    onBackPressed: () -> Unit

) {

    val context = LocalContext.current
    var isLoading by remember {

        mutableStateOf(true)
    }
    var errorOccurred by remember { mutableStateOf(false) }


    if (url.isNullOrEmpty()) {
        Log.e("ArticleScreen", "URL inválida: $url")
        Text("Invalid URL", style = MaterialTheme.typography.bodyLarge)
        return
    }


    Scaffold(

        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(text = "News", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackPressed) {


                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Search")
                    }

                },

                colors = TopAppBarDefaults.topAppBarColors(

                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer

                )
            )
        }

    ) {padding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(padding), contentAlignment = Alignment.Center) {

            AndroidView(factory = {
                WebView(context).apply {

                    webViewClient = object : WebViewClient() {

                        override fun onPageFinished(view: WebView?, url: String?) {
                            isLoading = false
                            Log.d("ArticleScreen", "Página carregada com sucesso: $url")

                        }

                        override fun onReceivedError(
                            view: WebView?,
                            request: WebResourceRequest?,
                            error: WebResourceError?
                        ) {
                            super.onReceivedError(view, request, error)
                            isLoading = false
                            errorOccurred = true
                            Log.e("ArticleScreen", "Erro ao carregar a página: ${error?.description}")
                        }
                    }
                    Log.d("ArticleScreen", "Carregando URL: $url")
                    loadUrl(url ?: "")
                }
            }
            )

            if(isLoading && !url.isNullOrEmpty()) {

                CircularProgressIndicator()
                Log.d("ArticleScreen", "Carregando a página...")
            }
            if (errorOccurred) {
                Text(text = "Failed to load the article", color = Color.Red)
                Log.e("ArticleScreen", "Falha ao carregar o artigo.")
            }
        }
    }
}