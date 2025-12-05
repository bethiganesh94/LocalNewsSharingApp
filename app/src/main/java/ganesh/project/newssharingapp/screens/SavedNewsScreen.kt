package ganesh.project.newssharingapp.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import ganesh.project.newssharingapp.R
import ganesh.project.newssharingapp.savenews.SavedNewsEntity
import ganesh.project.newssharingapp.savenews.SavedNewsViewModel
import ganesh.project.newssharingapp.ui.theme.Main_BG_Color

class SavedNewsViewModelFactory(private val context: android.content.Context) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Suppress unchecked cast warning; ensure you return the right VM
        @Suppress("UNCHECKED_CAST")
        return SavedNewsViewModel(context) as T
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedNewsScreen(
    navController: NavController
) {
    // Get context in composable scope
    val context = LocalContext.current

    // Create the ViewModel with factory inside the body (safe)
    val viewModel: SavedNewsViewModel = viewModel(
        factory = SavedNewsViewModelFactory(context)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Saved News", color = MaterialTheme.colorScheme.onTertiary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Main_BG_Color
                )
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {



            Spacer(modifier = Modifier.height(8.dp))

            if (viewModel.savedList.isEmpty())
                EmptyScreen(R.drawable.empty_news, "No saved news yet.")
            else
                LazyColumn {
                    items(viewModel.savedList) { saved ->
                        SavedNewsCard(saved)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
        }
    }
}

@Composable
fun EmptyScreen(image: Int, title: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = image),
            contentDescription = title,
            modifier = Modifier.size(150.dp)
        )

        Text(title,fontSize = 28.sp, fontWeight = FontWeight.Bold)

    }
}


@Composable
fun SavedNewsCard(news: SavedNewsEntity) {

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEFEAFF)),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column {

            AsyncImage(
                model = news.imageUrl,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            )

            Column(modifier = Modifier.padding(16.dp)) {

                Text(news.newsTitle, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))

                Text(news.author, color = Color.Gray, fontSize = 12.sp)
                Spacer(Modifier.height(4.dp))

                Text(news.place, color = Color.DarkGray, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))

                Text(
                    news.newsContent,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
