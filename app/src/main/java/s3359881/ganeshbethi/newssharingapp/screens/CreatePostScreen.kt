package s3359881.ganeshbethi.newssharingapp.screens

import android.app.DatePickerDialog
import android.content.ContentResolver
import android.content.Context
import android.net.Uri
import android.util.Base64
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.FirebaseDatabase
import s3359881.ganeshbethi.newssharingapp.R
import s3359881.ganeshbethi.newssharingapp.UserAccountPrefs
import s3359881.ganeshbethi.newssharingapp.ui.theme.Main_BG_Color
import s3359881.ganeshbethi.newssharingapp.ui.theme.Purple40
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.ByteArrayOutputStream
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

                        val newsData = NewsData(
                            newsTitle = headline,
                            newsCategory = selectedCategory,
                            newsContent = description,
                            place = place,
                            date = date
                        )


                        coroutineScope.launch {

                            val bytes = readBytesFromUri(photoUri!!, context.contentResolver)
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
private const val IMGBB_API_KEY = "dd2c6f23d315032050b31f06adcfaf3b"

suspend fun uploadToImgBB(base64Image: String): String? = withContext(Dispatchers.IO) {
    try {
        val client = OkHttpClient()

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
            val data = json.optJSONObject("data")
            return@withContext data?.optString("url") ?: data?.optString("display_url")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return@withContext null
    }
}


fun uploadPostToFirebase(
    newsData: NewsData,
    imageUrl: String,
    context: Context,
    onComplete: () -> Unit
) {
    val databaseRef = FirebaseDatabase.getInstance().reference
    val userEmail = UserAccountPrefs.getEmail(context).replace(".", ",")
    val userName = UserAccountPrefs.getName(context)
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
