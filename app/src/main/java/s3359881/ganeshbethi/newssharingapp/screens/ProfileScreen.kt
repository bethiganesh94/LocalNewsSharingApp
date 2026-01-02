package s3359881.ganeshbethi.newssharingapp.screens

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.google.firebase.database.FirebaseDatabase
import s3359881.ganeshbethi.newssharingapp.AppScreens
import s3359881.ganeshbethi.newssharingapp.UserData
import s3359881.ganeshbethi.newssharingapp.UserAccountPrefs
import s3359881.ganeshbethi.newssharingapp.ui.theme.Main_BG_Color
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userName = UserAccountPrefs.getName(context)
    val userEmail = UserAccountPrefs.getEmail(context)
    val userCity = UserAccountPrefs.getCity(context)
    val userDob = UserAccountPrefs.getDOB(context)
    val profileImage = UserAccountPrefs.getProfileImage(context) // ImgBB URL


    val userKey = userEmail.replace(".", ",")

    var userData by remember { mutableStateOf<UserData?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }

    userData = UserData(
        fullname = userName,
        email = userEmail,
        city = userCity,
        dob = userDob,
    )


    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "My Profile",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, null, tint = Color.White)
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
                .background(Color(0xFFF6F7FB))
        ) {

            // 🌈 HEADER SECTION
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Main_BG_Color, Color(0xFF6A5AE0))
                        )
                    ),
                contentAlignment = Alignment.BottomCenter
            ) {

                Box(
                    modifier = Modifier
                        .offset(y = 50.dp)
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    if (profileImage.isNotEmpty()) {
                        AsyncImage(
                            model = profileImage,
                            contentDescription = "",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Icon(
                            Icons.Default.Person,
                            contentDescription = "",
                            tint = Main_BG_Color,
                            modifier = Modifier.size(64.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(70.dp))

            // 👤 NAME + EMAIL
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = userName,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = userEmail,
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 📦 PROFILE INFO CARD
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(20.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {

                Column(modifier = Modifier.padding(20.dp)) {

                    ProfileInfoRow(
                        icon = Icons.Default.LocationOn,
                        label = "City",
                        value = userCity
                    )

                    Divider(modifier = Modifier.padding(vertical = 12.dp))

                    ProfileInfoRow(
                        icon = Icons.Default.DateRange,
                        label = "Date of Birth",
                        value = userDob
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))

            // ✏️ EDIT PROFILE
            Button(
                onClick = { showEditDialog = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Main_BG_Color)
            ) {
                Icon(Icons.Default.Edit, null)
                Spacer(Modifier.width(8.dp))
                Text("Edit Profile", fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 🚪 LOGOUT
            OutlinedButton(
                onClick = {
                    UserAccountPrefs.markLoginStatus(context, false)
                    navController.navigate(AppScreens.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(52.dp),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, Color.Red)
            ) {
                Icon(Icons.Default.Logout, null, tint = Color.Red)
                Spacer(Modifier.width(8.dp))
                Text("Logout", color = Color.Red, fontWeight = FontWeight.Bold)
            }
        }
    }

    if (showEditDialog && userData != null) {

        var name by remember { mutableStateOf(userData!!.fullname) }
        var city by remember { mutableStateOf(userData!!.city) }
        var dob by remember { mutableStateOf(userData!!.dob) }
        var imageUri by remember { mutableStateOf<Uri?>(null) }

        val imagePicker = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri -> imageUri = uri }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {

                    OutlinedTextField(name, { name = it }, label = { Text("Name") })
                    OutlinedTextField(city, { city = it }, label = { Text("City") })
                    OutlinedTextField(dob, { dob = it }, label = { Text("DOB") })

                    Spacer(Modifier.height(12.dp))

                    Button(onClick = { imagePicker.launch("image/*") }) {
                        Text("Change Profile Picture")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isUploading = true

                        coroutineScope.launch {

                            val imageUrl =
                                if (imageUri != null)
                                    uploadProfileImageToImgBB(context, imageUri!!)
                                else userData!!.profileImage

                            val updatedUser = userData!!.copy(
                                fullname = name,
                                city = city,
                                dob = dob,
                                profileImage = imageUrl ?: ""
                            )

                            FirebaseDatabase.getInstance()
                                .getReference("Accounts")
                                .child(userKey)
                                .setValue(updatedUser)
                                .addOnSuccessListener {
                                    userData = updatedUser
                                    isUploading = false
                                    showEditDialog = false
                                }
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {

        Icon(
            icon,
            contentDescription = "",
            tint = Main_BG_Color,
            modifier = Modifier.size(24.dp)
        )

        Spacer(Modifier.width(12.dp))

        Column {
            Text(label, color = Color.Gray, fontSize = 12.sp)
            Text(value, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreenOld(navController: NavController) {

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userEmail = UserAccountPrefs.getEmail(context)
    val userKey = userEmail.replace(".", ",")

    var userData by remember { mutableStateOf<UserData?>(null) }
    var showEditDialog by remember { mutableStateOf(false) }
    var isUploading by remember { mutableStateOf(false) }

    // Load profile
    LaunchedEffect(Unit) {
        FirebaseDatabase.getInstance()
            .getReference("Accounts")
            .child(userKey)
            .get()
            .addOnSuccessListener {
                userData = it.getValue(UserData::class.java)
            }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile", color = MaterialTheme.colorScheme.onPrimary) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            null,
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            )
        }
    ) { padding ->

        userData?.let { user ->

            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(24.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                // PROFILE IMAGE
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFE3DFFD)),
                    contentAlignment = Alignment.Center
                ) {
                    if (user.profileImage.isNotEmpty()) {
                        AsyncImage(
                            model = user.profileImage,
                            contentDescription = "",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Icon(Icons.Default.Person, null, modifier = Modifier.size(60.dp))
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(user.fullname, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Text(user.email, color = Color.Gray)
                Spacer(Modifier.height(8.dp))
                Text("City: ${user.city}")
                Text("DOB: ${user.dob}")

                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { showEditDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Edit Profile")
                }

                Spacer(Modifier.height(12.dp))

                Button(
                    onClick = {
                        UserAccountPrefs.markLoginStatus(context, false)
                        navController.navigate(AppScreens.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Logout", color = Color.White)
                }
            }
        }
    }

    // ---------------- EDIT PROFILE DIALOG ----------------
    if (showEditDialog && userData != null) {

        var name by remember { mutableStateOf(userData!!.fullname) }
        var city by remember { mutableStateOf(userData!!.city) }
        var dob by remember { mutableStateOf(userData!!.dob) }
        var imageUri by remember { mutableStateOf<Uri?>(null) }

        val imagePicker = rememberLauncherForActivityResult(
            ActivityResultContracts.GetContent()
        ) { uri -> imageUri = uri }

        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile") },
            text = {
                Column {

                    OutlinedTextField(name, { name = it }, label = { Text("Name") })
                    OutlinedTextField(city, { city = it }, label = { Text("City") })
                    OutlinedTextField(dob, { dob = it }, label = { Text("DOB") })

                    Spacer(Modifier.height(12.dp))

                    Button(onClick = { imagePicker.launch("image/*") }) {
                        Text("Change Profile Picture")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        isUploading = true

                        coroutineScope.launch {

                            val imageUrl =
                                if (imageUri != null)
                                    uploadProfileImageToImgBB(context, imageUri!!)
                                else userData!!.profileImage

                            val updatedUser = userData!!.copy(
                                fullname = name,
                                city = city,
                                dob = dob,
                                profileImage = imageUrl ?: ""
                            )

                            FirebaseDatabase.getInstance()
                                .getReference("Accounts")
                                .child(userKey)
                                .setValue(updatedUser)
                                .addOnSuccessListener {
                                    userData = updatedUser
                                    isUploading = false
                                    showEditDialog = false
                                }
                        }
                    }
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}


suspend fun uploadProfileImageToImgBB(
    context: Context,
    imageUri: Uri
): String? {
    return try {
        val apiKey = "dd2c6f23d315032050b31f06adcfaf3b"

        val inputStream = context.contentResolver.openInputStream(imageUri)
        val bytes = inputStream?.readBytes() ?: return null
        val base64Image = android.util.Base64.encodeToString(bytes, android.util.Base64.DEFAULT)

        val body = okhttp3.MultipartBody.Builder()
            .setType(okhttp3.MultipartBody.FORM)
            .addFormDataPart("key", apiKey)
            .addFormDataPart("image", base64Image)
            .build()

        val request = okhttp3.Request.Builder()
            .url("https://api.imgbb.com/1/upload")
            .post(body)
            .build()

        val response = okhttp3.OkHttpClient().newCall(request).execute()
        val json = org.json.JSONObject(response.body!!.string())

        json.getJSONObject("data").getString("url")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
