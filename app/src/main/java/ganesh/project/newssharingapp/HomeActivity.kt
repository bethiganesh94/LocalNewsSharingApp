package ganesh.project.newssharingapp

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ganesh.project.newssharingapp.network.NewsViewModel
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = NavHostController(LocalContext.current))
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: NewsViewModel = viewModel()) {

    val newsList = viewModel.newsList
    val isLoading = viewModel.isLoading

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "News App",
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        content = { innerPadding ->

            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            )
            {

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Top News",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 12.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // --- Auto Slider ---
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                } else {
                    AutoSlider(newsList)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp, horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            navController.navigate(AppScreens.CreatePost.route)
                        }
                    )
                    {

                        Image(
                            painter = painterResource(id = R.drawable.icon_news_sharing),
                            contentDescription = "News Sharing",
                            modifier = Modifier
                                .size(62.dp)
                        )
                        Text(
                            text = "Create Post",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center

                        )

                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {

                        Image(
                            painter = painterResource(id = R.drawable.icon_news_sharing),
                            contentDescription = "News Sharing",
                            modifier = Modifier
                                .size(62.dp)
                        )
                        Text(
                            text = "Saved News",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center

                        )

                    }

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    )
                    {

                        Image(
                            painter = painterResource(id = R.drawable.icon_news_sharing),
                            contentDescription = "News Sharing",
                            modifier = Modifier
                                .size(62.dp)
                        )
                        Text(
                            text = "My Post",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center

                        )

                    }


                }


            }
        }

    )


}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AutoSlider(newsList: List<Article>) {

    val pagerState = rememberPagerState(pageCount = { newsList.size })

    LaunchedEffect(pagerState) {
        while (true) {
            delay(3000)
            if (pagerState.pageCount > 0) {
                pagerState.animateScrollToPage(
                    page = (pagerState.currentPage + 1) % pagerState.pageCount
                )
            }
        }
    }

    HorizontalPager(
        state = pagerState,
        modifier = Modifier
            .fillMaxWidth()
            .height(360.dp)
            .padding(12.dp)
    ) { page ->

        val article = newsList[page]

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxSize()
        ) {

            AsyncImage(
                model = article.urlToImage,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = article.title ?: "",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        }
    }
}


data class NewsResponse(
    val articles: List<Article>
)

data class Article(
    val title: String?,
    val urlToImage: String?,
    val description: String?
)
