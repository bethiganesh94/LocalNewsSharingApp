package ganesh.project.newssharingapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Person
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ganesh.project.newssharingapp.R
import ganesh.project.newssharingapp.UserPrefs
import ganesh.project.newssharingapp.savenews.SavedNewsViewModel
import ganesh.project.newssharingapp.ui.theme.Main_BG_Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllPostsScreen(navController: NavController) {

    val context = LocalContext.current

    // ViewModel for saving
    val saveViewModel: SavedNewsViewModel = viewModel(factory = object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return SavedNewsViewModel(context) as T
        }
    })

    var postsList by remember { mutableStateOf<List<NewsPost>>(emptyList()) }

    // Fetch all posts
    LaunchedEffect(Unit) {
        getAllPosts { list -> postsList = list }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("All Posts", color = MaterialTheme.colorScheme.onTertiary)
                },
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
        },

        ) { padding ->

        Column(modifier = Modifier
            .padding(padding)
            .padding(16.dp)) {

            if (postsList.isEmpty())
                EmptyScreen(R.drawable.empty_news, "No News Found")
            else
                LazyColumn {
                    items(postsList) { post ->

                        AllPostsCard(
                            post = post,
                            isSaved = saveViewModel.savedMap[post.newsId] == true,
                            onToggleSave = { saveViewModel.toggleSave(it) }
                        )

                        Spacer(Modifier.height(16.dp))
                    }
                }
        }
    }
}


fun getAllPosts(
    onResult: (List<NewsPost>) -> Unit
) {
    val databaseRef = FirebaseDatabase.getInstance().reference

    databaseRef.child("NewsPosts")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val allPosts = mutableListOf<NewsPost>()

                snapshot.children.forEach { userNode ->
                    userNode.children.forEach { postNode ->
                        val post = postNode.getValue(NewsPost::class.java)
                        if (post != null) allPosts.add(post)
                    }
                }

                onResult(allPosts.sortedByDescending { it.timestamp })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
}


@Composable
fun AllPostsCard(
    post: NewsPost,
    isSaved: Boolean,
    onToggleSave: (NewsPost) -> Unit
) {

    val context = LocalContext.current
    val userEmail = UserPrefs.getEmail(context).replace(".", ",")  // You must add this field in NewsPost OR pass separately

    var comments by remember { mutableStateOf<List<Comment>>(emptyList()) }
    var isCommentsLoaded by remember { mutableStateOf(false) }

    // Load comments
    LaunchedEffect(post.newsId) {
        getCommentsForPost(userEmail, post.newsId) {
            comments = it
            isCommentsLoaded = true
        }
    }

    // Dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var showViewDialog by remember { mutableStateOf(false) }
    var commentInput by remember { mutableStateOf("") }

    // ------------------ CARD ---------------------
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFECE5FF)),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column {

            // ------------------ IMAGE SECTION ---------------------
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {

                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                IconButton(
                    onClick = { onToggleSave(post) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "",
                        tint = Color.White
                    )
                }

                Text(
                    text = post.newsCategory,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                        .padding(6.dp)
                )

                Text(
                    text = post.date,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                        .padding(6.dp)
                )
            }

            // ------------------ DETAILS ---------------------
            Column(modifier = Modifier.padding(16.dp)) {

                Text(post.newsTitle, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = "", tint = Color.Gray, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(post.author, color = Color.Gray, fontSize = 13.sp)
                }

                Spacer(Modifier.height(6.dp))

                Text(post.place, color = Color.DarkGray, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))

                Text(post.newsContent, maxLines = 3, overflow = TextOverflow.Ellipsis)

                Spacer(Modifier.height(12.dp))

                // ------------------ COMMENTS SECTION ---------------------

                Row(verticalAlignment = Alignment.CenterVertically) {

                    // Comments count
                    Text(
                        text = "💬 ${comments.size} Comments",
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Black
                    )

                    Spacer(Modifier.width(16.dp))

                    // Add comment
                    Text(
                        text = "Add Comment",
                        color = Color(0xFF3F51B5),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { showAddDialog = true }
                    )

                    Spacer(Modifier.width(16.dp))

                    // View comments
                    if (comments.isNotEmpty()) {
                        Text(
                            text = "View",
                            color = Color(0xFF009688),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { showViewDialog = true }
                        )
                    }
                }
            }
        }
    }

    // ------------------ ADD COMMENT DIALOG ---------------------
    if (showAddDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add Comment") },
            text = {
                androidx.compose.material3.OutlinedTextField(
                    value = commentInput,
                    onValueChange = { commentInput = it },
                    placeholder = { Text("Write your comment...") }
                )
            },
            confirmButton = {
                Text(
                    text = "Post",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable {
                            addCommentToPost(
                                context = context,
                                userEmail = userEmail,
                                newsId = post.newsId,
                                commentText = commentInput
                            ) {
                                showAddDialog = false
                                commentInput = ""
                            }
                        }
                )
            },
            dismissButton = {
                Text(
                    text = "Cancel",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { showAddDialog = false }
                )
            }
        )
    }

    // ------------------ VIEW COMMENTS DIALOG ---------------------
    if (showViewDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showViewDialog = false },
            title = { Text("Comments") },
            text = {
                LazyColumn(modifier = Modifier.height(250.dp)) {
                    items(comments) { comment ->
                        Column(Modifier.padding(8.dp)) {
                            Text("👤 ${comment.author}", fontWeight = FontWeight.Bold)
                            Text(comment.comment)
                            Spacer(Modifier.height(8.dp))
                        }
                    }
                }
            },
            confirmButton = {
                Text(
                    "Close",
                    modifier = Modifier
                        .padding(8.dp)
                        .clickable { showViewDialog = false }
                )
            }
        )
    }
}


@Composable
fun AllPostsCardOld(
    post: NewsPost,
    isSaved: Boolean,
    onToggleSave: (NewsPost) -> Unit
) {

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFECE5FF)),
        modifier = Modifier.fillMaxWidth()
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {

                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Toggle Save Icon (Top Right)
                IconButton(
                    onClick = { onToggleSave(post) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "",
                        tint = Color.White
                    )
                }

                // CATEGORY bottom-left
                Text(
                    text = post.newsCategory,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                // DATE bottom-right
                Text(
                    text = post.date,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // Content Below Image
            Column(modifier = Modifier.padding(16.dp)) {

                Text(post.newsTitle, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(4.dp))

                // Author Below Title
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = "",
                        tint = Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(post.author, color = Color.Gray, fontSize = 13.sp)
                }

                Spacer(Modifier.height(6.dp))

                Text(post.place, color = Color.DarkGray, fontSize = 13.sp)
                Spacer(Modifier.height(8.dp))

                Text(
                    post.newsContent,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}


