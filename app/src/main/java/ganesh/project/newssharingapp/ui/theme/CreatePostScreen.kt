package ganesh.project.newssharingapp.ui.theme

import android.app.DatePickerDialog
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import ganesh.project.newssharingapp.UserPrefs
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController) {
    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {

        var headline by remember { mutableStateOf("") }
        var description by remember { mutableStateOf("") }
        var place by remember { mutableStateOf("") }
        var date by remember { mutableStateOf("") }


        val categories = listOf(
            "Breaking News",
            "Politics",
            "Business",
            "Technology",
            "Sports",
            "Health",
            "Entertainment",
            "Science",
            "World News",
            "Local News",
            "Education",
            "Environment",
            "Lifestyle",
            "Crime & Safety",
            "Weather",
            "Top Stories",
            "Headlines",
            "Trending",
            "Opinion / Editorial",
            "Finance",
            "Stock Market",
            "Startups",
            "Innovation",
            "Automobiles",
            "Space",
            "Research",
            "Culture",
            "Art & Media",
            "Travel",
            "Food",
            "Fashion",
            "Real Estate",
            "Agriculture",
            "Announcement",
            "Update",
            "Emergency"
        )
        var selectedCategory by remember { mutableStateOf("") }
        var categoryExpanded by remember { mutableStateOf(false) }

        val context = LocalContext.current
        val calendar = Calendar.getInstance()
        val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val datePicker = DatePickerDialog(
            context,
            { _, year, month, day ->
                calendar.set(year, month, day)
                date = dateFormatter.format(calendar.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(
                            text = "Create Post",
                            color = MaterialTheme.colorScheme.onPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
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
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)

                ) {

                    Text(text = "Headline", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = headline,
                        onValueChange = { headline = it },
                        placeholder = { Text("Enter headline") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )


                    Text(text = "Category", fontWeight = FontWeight.SemiBold)

                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = !categoryExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedCategory,
                            onValueChange = { /* readOnly */ },
                            readOnly = true,
                            placeholder = { Text("Select category") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            categories.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        selectedCategory = option
                                        categoryExpanded = false
                                    },
                                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                                )
                            }
                        }
                    }
                    Text(text = "Description", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Enter description") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        maxLines = 6
                    )


                    Text(text = "Place", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = place,
                        onValueChange = { place = it },
                        placeholder = { Text("Enter place") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )




                    Text(text = "Date", fontWeight = FontWeight.SemiBold)
                    OutlinedTextField(
                        value = date,
                        onValueChange = {},
                        readOnly = true,
                        placeholder = { Text("Select date") },
                        trailingIcon = {
                            IconButton(onClick = { datePicker.show() }) {
                                Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))


                    Button(
                        onClick = {

                            if (headline.isBlank() || selectedCategory.isBlank() || place.isBlank() || description.isBlank() || date.isBlank()) {
                                Toast.makeText(
                                    context,
                                    "All Fields  are Mandatory",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else {

                                val newsData1 = NewsData(
                                    newsTitle = headline,
                                    newsCategory = selectedCategory,
                                    newsContent = description,
                                    place = place,
                                    date = date
                                )

                                Toast.makeText(
                                    context,
                                    "Posted News Successfully.",
                                    Toast.LENGTH_SHORT
                                ).show()

//                                uploadNewsWithImage(newsData = newsData1, context = context)
                            }

                        },
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .height(52.dp)
                            .width(180.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(text = "Post", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun CreatePostScreenPreview() {
    MaterialTheme {
        CreatePostScreen(navController = NavHostController(LocalContext.current))
    }
}


data class NewsData(
    val newsTitle: String = "",
    val newsCategory: String = "",
    val newsContent: String = "",
    var newsId: String = "",
    var place: String = "",
    var date: String = "",
    var imageUrl: String = "",
    var author: String = ""
)

fun uploadNewsWithImage(
    newsData: NewsData,
    context: Context
) {
    val databaseRef = FirebaseDatabase.getInstance().reference
    val userName = UserPrefs.getName(context)
    val userEmail = UserPrefs.getEmail(context = context).replace(".", ",")
    val newsId = databaseRef.child("NewsPosts").push().key ?: return


    val newsMap = mapOf(
        "newsId" to newsId,
        "newsTitle" to newsData.newsTitle,
        "newsCategory" to newsData.newsCategory,
        "newsContent" to newsData.newsContent,
        "imageUrl" to "https://thumbs.dreamstime.com/b/breaking-news-global-updates-insights-wallpaper-366068573.jpg",
        "lat" to newsData.place,
        "lng" to newsData.date,
        "timestamp" to System.currentTimeMillis(),
        "author" to userName
    )


    Log.e("test", "1")
    databaseRef.child("NewsPosts/$userEmail").child(newsId)
        .setValue(newsMap)
        .addOnSuccessListener {
            Toast.makeText(context, "Posted News Successfully.", Toast.LENGTH_SHORT).show()
//            (context as Activity).finish()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to save news.", Toast.LENGTH_SHORT).show()
        }
}
