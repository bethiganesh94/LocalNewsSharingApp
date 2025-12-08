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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PermIdentity
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import ganesh.project.newssharingapp.network.NewsViewModel
import ganesh.project.newssharingapp.screens.NewsPost
import ganesh.project.newssharingapp.screens.getMyPosts
import ganesh.project.newssharingapp.ui.theme.Main_BG_Color
import kotlinx.coroutines.delay

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen(navController = NavHostController(LocalContext.current))
}


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(navController: NavController, viewModel: NewsViewModel = viewModel()) {

    val context = LocalContext.current
    val newsList = viewModel.newsList
    val isLoading = viewModel.isLoading

    // State for MyPosts Slider
    var myPosts by remember { mutableStateOf<List<NewsPost>>(emptyList()) }

    // Load MyPosts from Firebase
    LaunchedEffect(Unit) {
        getMyPosts(context) { posts ->
            myPosts = posts.take(6) // Show only 6 posts
        }
    }

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
                actions = {
                    IconButton(
                        onClick = { navController.navigate(AppScreens.Profile.route) }
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.PermIdentity,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Main_BG_Color)
            )
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {

            Spacer(modifier = Modifier.height(12.dp))

            // ---------------- TOP NEWS TITLE ----------------
            Text(
                text = "Top News",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ---------------- TOP NEWS SLIDER ----------------
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            } else {
                AutoSlider(newsList)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ---------------- MY POSTS TITLE ----------------
            Text(
                text = "Local News",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ---------------- MY POSTS SLIDER ----------------
            if (myPosts.isEmpty()) {
                Text(
                    text = "No posts found.",
                    color = Color.Gray,
                    modifier = Modifier.padding(start = 12.dp)
                )
            } else {
                MyPostsSlider(myPosts, navController)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ---------------- OPTIONS GRID ----------------
            HomeOptionsGrid(
                onLocalNewsClick = { navController.navigate(AppScreens.AllPosts.route) },
                onSavedNewsClick = { navController.navigate(AppScreens.SavedPosts.route) },
                onCreatePostClick = { navController.navigate(AppScreens.CreatePost.route) },
                onMyPostsClick = { navController.navigate(AppScreens.MyPosts.route) }
            )
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MyPostsSlider(posts: List<NewsPost>, navController: NavController) {

    val pagerState = rememberPagerState(pageCount = { posts.size })

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
            .height(280.dp)
            .padding(12.dp)
    ) { page ->

        val post = posts[page]

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .fillMaxSize()
                .clickable {
                    navController.navigate("post_details/${post.newsId}")
                }
        ) {

            AsyncImage(
                model = post.imageUrl,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // TITLE BAR
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(10.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = post.newsTitle,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1
                )
            }

            // READ ARTICLE BUTTON (Transparent Black)
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .background(
                        Color.Black.copy(alpha = 0.4f),
                        RoundedCornerShape(20.dp)
                    )
                    .clickable {
                        navController.navigate("post_details/${post.newsId}")
                    }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "Read Article",
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}



@Composable
fun HomeOptionsGrid(
    onLocalNewsClick: () -> Unit = {},
    onSavedNewsClick: () -> Unit = {},
    onCreatePostClick: () -> Unit = {},
    onMyPostsClick: () -> Unit = {}
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OptionCard(
                title = "Local News",
                image = R.drawable.icon_localnews,
                modifier = Modifier.weight(1f),
                onClick = onLocalNewsClick
            )
            OptionCard(
                title = "Saved News",
                image = R.drawable.icon_savednews,
                modifier = Modifier.weight(1f),
                onClick = onSavedNewsClick
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            OptionCard(
                title = "Create Post",
                image = R.drawable.icon_createpost,
                modifier = Modifier.weight(1f),
                onClick = onCreatePostClick
            )
            OptionCard(
                title = "My Posts",
                image = R.drawable.icon_myposts,
                modifier = Modifier.weight(1f),
                onClick = onMyPostsClick
            )
        }
    }
}


@Composable
fun OptionCard(
    title: String,
    image: Int,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFECE5FF)
        ),
        modifier = modifier   // <-- weight is passed from parent
            .height(150.dp)
            .clickable { onClick() }
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {

            Image(
                painter = painterResource(id = image),
                contentDescription = title,
                modifier = Modifier.size(50.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
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
