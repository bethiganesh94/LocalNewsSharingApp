package ganesh.project.newssharingapp.screens

import android.app.Activity
import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import ganesh.project.newssharingapp.R
import ganesh.project.newssharingapp.UserPrefs
import ganesh.project.newssharingapp.ui.theme.Main_BG_Color
import ganesh.project.newssharingapp.ui.theme.Purple40
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController) {

    val context = LocalContext.current

    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }

    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            photoUri = uri
        }
    }

    var headline by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var place by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }

    val categories =
        listOf("Breaking News", "Politics", "Sports", "Local News", "Education", "Emergency")
    var selectedCategory by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    val calendar = Calendar.getInstance()
    val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    val coroutineScope = rememberCoroutineScope()

    val datePicker = DatePickerDialog(
        context,
        { _, y, m, d ->
            calendar.set(y, m, d)
            date = dateFormatter.format(calendar.time)
        },
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH)
    )

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Create Post", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Main_BG_Color)
            )
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            OutlinedTextField(
                value = headline,
                onValueChange = { headline = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Headline") })

            ExposedDropdownMenuBox(
                expanded = categoryExpanded,
                onExpandedChange = { categoryExpanded = !categoryExpanded }) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth(),
                    placeholder = { Text("Select Category") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) }
                )
                ExposedDropdownMenu(
                    expanded = categoryExpanded,
                    onDismissRequest = { categoryExpanded = false }) {
                    categories.forEach {
                        DropdownMenuItem(
                            text = { Text(it) },
                            onClick = { selectedCategory = it; categoryExpanded = false }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                placeholder = { Text("Description") },
                maxLines = 6
            )

            OutlinedTextField(
                value = place,
                onValueChange = { place = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Place") })

            OutlinedTextField(
                value = date,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Date") },
                trailingIcon = {
                    IconButton(onClick = { datePicker.show() }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Pick date")
                    }
                }
            )

            // Image Selection
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (photoUri != null) {
                    Image(
                        painter = rememberAsyncImagePainter(photoUri),
                        contentDescription = "",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.add_image),
                        contentDescription = "",
                        modifier = Modifier.size(120.dp)
                    )
                }

                Button(
                    onClick = { pickImageLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = Purple40)
                ) {
                    Text("Select Image")
                }
            }

            Spacer(Modifier.height(16.dp))

            // POST BUTTON
            Button(
                onClick = {
                    if (headline.isBlank() || selectedCategory.isBlank() || place.isBlank() || description.isBlank() || date.isBlank()) {
                        Toast.makeText(context, "All fields are required!", Toast.LENGTH_SHORT)
                            .show()
                    } else if (photoUri == null) {
                        Toast.makeText(context, "Please select an image!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        isUploading = true

                        // Upload process
                        val newsData = NewsData(
                            newsTitle = headline,
                            newsCategory = selectedCategory,
                            newsContent = description,
                            place = place,
                            date = date
                        )

                        // Coroutine launch

                        coroutineScope.launch {

                            val bytes = readBytesFromUri(photoUri!!, context.contentResolver)
                            // Convert to Base64 (ImgBB expects base64 without data URI prefix)
                            val base64 = Base64.encodeToString(bytes, Base64.DEFAULT)

                            val imageUrl = uploadToImgBB(base64)

                            if (imageUrl != null) {
                                uploadPostToFirebase(newsData, imageUrl, context) {
                                    isUploading = false
                                }
                            } else {
                                Toast.makeText(context, "Image upload failed!", Toast.LENGTH_SHORT)
                                    .show()
                                isUploading = false
                            }
                        }

                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .width(180.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Main_BG_Color)
            ) {
                if (isUploading)
                    CircularProgressIndicator(color = Color.White, strokeWidth = 2.dp)
                else
                    Text("Post", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }
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

suspend fun readBytesFromUri(uri: Uri, contentResolver: ContentResolver): ByteArray =
    withContext(Dispatchers.IO) {
        val input: InputStream? = contentResolver.openInputStream(uri)
        val baos = ByteArrayOutputStream()
        input?.use { stream ->
            val buffer = ByteArray(4096)
            var read: Int
            while (stream.read(buffer).also { read = it } != -1) {
                baos.write(buffer, 0, read)
            }
        }
        baos.toByteArray()
    }
private const val IMGBB_API_KEY = "dd2c6f23d315032050b31f06adcfaf3b" // <- your key (from user)

// ----------------- Helper: Upload to ImgBB (returns image URL) -----------------
suspend fun uploadToImgBB(base64Image: String): String? = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()

        // ImgBB accepts 'image' param as base64 string
        val form = FormBody.Builder()
            .add("key", IMGBB_API_KEY)
            .add("image", base64Image)
            .build()

        val request = Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(form)
            .build()

        client.newCall(request).execute().use { response ->
            val body = response.body?.string() ?: return@withContext null
            if (!response.isSuccessful) return@withContext null

            val json = JSONObject(body)
            // Look for data -> url or display_url
            val data = json.optJSONObject("data")
            return@withContext data?.optString("url") ?: data?.optString("display_url")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}

fun uploadToImgBBOld(imageUri: Uri, context: Context): String? {
    return try {
        val apiKey = "dd2c6f23d315032050b31f06adcfaf3b"   // <-- Replace with your key

        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bytes = inputStream?.readBytes() ?: return null
        val base64Image = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)

        val requestBody = okhttp3.MultipartBody.Builder()
            .setType(okhttp3.MultipartBody.FORM)
            .addFormDataPart("key", apiKey)
            .addFormDataPart("image", base64Image)
            .build()

        val request = okhttp3.Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(requestBody)
            .build()

        val client = okhttp3.OkHttpClient()
        val response = client.newCall(request).execute()

        val responseJson = response.body?.string()
        val jsonObj = org.json.JSONObject(responseJson!!)
        jsonObj.getJSONObject("data").getString("url")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}


fun uploadPostToFirebase(
    newsData: NewsData,
    imageUrl: String,
    context: Context,
    onComplete: () -> Unit
) {
    val databaseRef = FirebaseDatabase.getInstance().reference
    val userEmail = UserPrefs.getEmail(context).replace(".", ",")
    val userName = UserPrefs.getName(context)
    val newsId = databaseRef.child("NewsPosts").push().key ?: return

    val newsMap = mapOf(
        "newsId" to newsId,
        "newsTitle" to newsData.newsTitle,
        "newsCategory" to newsData.newsCategory,
        "newsContent" to newsData.newsContent,
        "imageUrl" to imageUrl,
        "place" to newsData.place,
        "date" to newsData.date,
        "timestamp" to System.currentTimeMillis(),
        "author" to userName
    )

    databaseRef.child("NewsPosts/$userEmail/$newsId")
        .setValue(newsMap)
        .addOnSuccessListener {
            Toast.makeText(context, "Post Uploaded Successfully!", Toast.LENGTH_SHORT).show()
            onComplete()
        }
        .addOnFailureListener {
            Toast.makeText(context, "Failed to upload post!", Toast.LENGTH_SHORT).show()
            onComplete()
        }
}
