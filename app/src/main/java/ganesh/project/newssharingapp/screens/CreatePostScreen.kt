package ganesh.project.newssharingapp.screens

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ganesh.project.newssharingapp.R

import ganesh.project.newssharingapp.UserPrefs
import ganesh.project.newssharingapp.ui.theme.Main_BG_Color
import ganesh.project.newssharingapp.ui.theme.Purple40
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController) {

    val context = LocalContext.current

    var photoUri by remember { mutableStateOf<Uri?>(null) }


    val takePicturePreview = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            val file = saveBitmapToCache(context, bitmap)
            photoUri = Uri.fromFile(file)
        }
    }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            takePicturePreview.launch()
        } else {
            Toast.makeText(context, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }

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

        var imageSelected by remember { mutableStateOf(false) }

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
                        containerColor = Main_BG_Color
                    )
                )
            },
            content = { innerPadding ->
                Column(
                    modifier = Modifier
                        .padding(innerPadding)
                        .padding(16.dp)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
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

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (photoUri != null) {

                            imageSelected = true

                            Image(
                                painter = rememberAsyncImagePainter(photoUri),
                                contentDescription = "Captured News Image",
                                modifier = Modifier
                                    .size(120.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.add_image),
                                contentDescription = "News Image",
                                modifier = Modifier
                                    .size(120.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = {
                                when (PackageManager.PERMISSION_GRANTED) {
                                    ContextCompat.checkSelfPermission(
                                        context,
                                        Manifest.permission.CAMERA
                                    ) -> {
                                        takePicturePreview.launch()
                                    }

                                    else -> {
                                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                        ) {
                            Text("Add Photo")
                        }
                    }

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


                                if (imageSelected) {

                                    val newsData1 = NewsData(
                                        newsTitle = headline,
                                        newsCategory = selectedCategory,
                                        newsContent = description,
                                        place = place,
                                        date = date
                                    )


//                                    uploadNewsWithImage(
//                                        newsData = newsData1,
//                                        photoUri!!,
//                                        context = context
//                                    )
                                    uploadNewsWithImage(
                                        newsData = newsData1,
                                        context = context
                                    )
                                }else{
                                    Toast.makeText(
                                        context,
                                        "Posted News Successfully.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
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
//        CreatePostScreen(navController = NavHostController(LocalContext.current))
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
    selectedImageUri: Uri,
    context: Context
) {
    val storageRef = FirebaseStorage.getInstance().reference
    val databaseRef = FirebaseDatabase.getInstance().reference
    val userName = UserPrefs.getName(context)
    val userEmail = UserPrefs.getEmail(context).replace(".", ",")


    val newsId = databaseRef.child("NewsPosts").push().key ?: return
    val imageRef = storageRef.child("NewsPosts/$newsId/news_image.jpg")
    imageRef.putFile(selectedImageUri)
        .continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let { throw it }
            }
            imageRef.downloadUrl
        }
        .addOnSuccessListener { downloadUri ->
            val newsMap = mapOf(
                "newsId" to newsId,
                "newsTitle" to newsData.newsTitle,
                "newsCategory" to newsData.newsCategory,
                "newsContent" to newsData.newsContent,
                "imageUrl" to downloadUri.toString(),
                "place" to newsData.place,
                "date" to newsData.date,
                "timestamp" to System.currentTimeMillis(),
                "author" to userName
            )

            databaseRef.child("NewsPosts/$userEmail").child(newsId)
                .setValue(newsMap)
                .addOnSuccessListener {
                    Toast.makeText(context, "Posted News Successfully.", Toast.LENGTH_SHORT).show()
                    (context as Activity).finish()
                }
                .addOnFailureListener {
                    Toast.makeText(context, "Failed to save news.", Toast.LENGTH_SHORT).show()
                }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to upload image.", Toast.LENGTH_SHORT).show()
        }
}

fun saveBitmapToCache(context: Context, bitmap: Bitmap): File {
    val file = File(context.cacheDir, "photo_${System.currentTimeMillis()}.jpg")
    file.outputStream().use {
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, it)
    }
    return file
}

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
        "place" to newsData.place,
        "date" to newsData.date,
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
