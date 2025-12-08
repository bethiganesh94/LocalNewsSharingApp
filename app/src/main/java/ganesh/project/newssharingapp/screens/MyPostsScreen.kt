package ganesh.project.newssharingapp.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import ganesh.project.newssharingapp.R
import ganesh.project.newssharingapp.UserPrefs
import ganesh.project.newssharingapp.ui.theme.Main_BG_Color


@Preview(showBackground = true)
@Composable
fun MyPostsScreenPreview() {
    MaterialTheme() {
        MyPostsScreen(navController = NavHostController(LocalContext.current))
    }
}

data class NewsPost(
    val newsId: String = "",
    val newsTitle: String = "",
    val newsCategory: String = "",
    val newsContent: String = "",
    val imageUrl: String = "",
    val place: String = "",
    val date: String = "",
    val timestamp: Long = 0,
    val author: String = ""
)



fun getMyPosts(
    context: Context,
    onResult: (List<NewsPost>) -> Unit
) {
    val userEmail = UserPrefs.getEmail(context).replace(".", ",")
    val databaseRef = FirebaseDatabase.getInstance().reference

    Log.e("Test","Getting posts of $userEmail")

    databaseRef.child("NewsPosts").child(userEmail)
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                val posts = snapshot.children.mapNotNull { it.getValue(NewsPost::class.java) }

                onResult(posts.sortedByDescending { it.timestamp }) // Latest first
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, "Failed to load posts.", Toast.LENGTH_SHORT).show()
            }
        })
}

fun deletePost(
    context: Context,
    postId: String,
    onDeleted: () -> Unit
) {
    val userEmail = UserPrefs.getEmail(context).replace(".", ",")
    val databaseRef = FirebaseDatabase.getInstance().reference

    databaseRef.child("NewsPosts").child(userEmail).child(postId)
        .removeValue()
        .addOnSuccessListener {
            Toast.makeText(context, "Post deleted", Toast.LENGTH_SHORT).show()
            onDeleted()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to delete", Toast.LENGTH_SHORT).show()
        }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPostsScreen(navController: NavController) {

    val context = LocalContext.current
    var postList by remember { mutableStateOf<List<NewsPost>>(emptyList()) }

    // Load posts on screen open
    LaunchedEffect(Unit) {
        getMyPosts(context) { posts ->
            postList = posts
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Posts",
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontWeight = FontWeight.SemiBold
                    )
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
        }
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {

            if (postList.isEmpty()) {
                EmptyScreen(R.drawable.empty_news, "No posts created yet.")
            } else
                LazyColumn {
                    items(postList) { post ->

                        PostCard(
                            post = post,
                            onDelete = {
                                deletePost(context, post.newsId) {
                                    postList = postList.filter { it.newsId != post.newsId }
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
        }
    }
}


@Composable
fun PostCard(post: NewsPost, onDelete: () -> Unit) {

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFD1C4E9)
        ),
        elevation = CardDefaults.cardElevation(6.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {

                // Background Image
                AsyncImage(
                    model = post.imageUrl,
                    contentDescription = "",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // DELETE BUTTON (Top Right)
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .background(Color.Black.copy(alpha = 0.4f), CircleShape)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White
                    )
                }

                // CATEGORY (Bottom Left)
                Text(
                    text = post.newsCategory,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )

                // DATE (Bottom Right)
                Text(
                    text = post.date,
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            // CONTENT AREA BELOW IMAGE
            Column(modifier = Modifier.padding(16.dp)) {

                // TITLE
                Text(
                    text = post.newsTitle,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                // PLACE
                Text(
                    text = post.place,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.DarkGray
                )

                Spacer(modifier = Modifier.height(6.dp))

                // DESCRIPTION
                Text(
                    text = post.newsContent,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}


