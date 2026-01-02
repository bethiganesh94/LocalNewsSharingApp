package s3359881.ganeshbethi.newssharingapp

import android.app.DatePickerDialog
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Password
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Preview(showBackground = true)
@Composable
fun RegistrationScreenPreview() {
    RegistrationScreen(navController = NavHostController(LocalContext.current))
}

@Composable
fun RegistrationScreen(navController: NavController) {

    var name by remember { mutableStateOf("") }
    var city by remember { mutableStateOf("") }

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current.findActivity()

    val context1 = LocalContext.current

    var dobDate by remember { mutableStateOf("") }

    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    fun openDatePicker(
        onSelect: (String) -> Unit,
        minDate: Long? = null,
        maxDate: Long? = null
    ) {
        val dp = DatePickerDialog(
            context1,
            { _, year, month, day ->
                val c = Calendar.getInstance().apply {
                    set(year, month, day)
                }
                onSelect(dateFormat.format(c.time))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )

        minDate?.let { dp.datePicker.minDate = it }

        maxDate?.let { dp.datePicker.maxDate = it }

        dp.show()
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.bg_main)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(modifier = Modifier.weight(1f))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Welcome",
                color = Color.White,
                style = TextStyle(fontSize = 52.sp, fontWeight = FontWeight.Bold)
            )
            Text(
                text = "Register to create Account",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall.copy(fontSize = 16.sp)
            )

        }

        Spacer(modifier = Modifier.height(24.dp))


        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = colorResource(R.color.tran_white),
                focusedContainerColor = colorResource(R.color.tran_white),
                unfocusedTextColor = Color.White,
            ),
            value = name,
            onValueChange = { name = it },
            label = { Text("Enter Name") },
            textStyle = TextStyle(color = colorResource(id = R.color.white)),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = "Name",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            }
        )

        Spacer(modifier = Modifier.height(6.dp))

        DOBDateField(
            label = "Date of Birth",
            value = dobDate,
            onClick = {
                openDatePicker({ dobDate = it }, 1900)
            }
        )

        Spacer(Modifier.height(6.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = colorResource(R.color.tran_white),
                focusedContainerColor = colorResource(R.color.tran_white),
                unfocusedTextColor = Color.White,
            ),
            value = city,
            onValueChange = { city = it },
            label = { Text("Enter City") },
            textStyle = TextStyle(color = colorResource(id = R.color.white)),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Map,
                    contentDescription = "City",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            },
        )

        Spacer(modifier = Modifier.height(6.dp))

        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = colorResource(R.color.tran_white),
                focusedContainerColor = colorResource(R.color.tran_white),
                unfocusedTextColor = Color.White,
            ),
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter Email") },
            textStyle = TextStyle(color = colorResource(id = R.color.white)),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Email,
                    contentDescription = "Email",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            },
        )

        Spacer(modifier = Modifier.height(6.dp))


        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            colors = TextFieldDefaults.colors(
                unfocusedContainerColor = colorResource(R.color.tran_white),
                focusedContainerColor = colorResource(R.color.tran_white),
                unfocusedTextColor = Color.White,
            ),
            value = password,
            onValueChange = { password = it },
            label = { Text("Enter Password") },
            textStyle = TextStyle(color = colorResource(id = R.color.white)),
            leadingIcon = {
                Icon(
                    imageVector = Icons.Filled.Password,
                    contentDescription = "Password",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(28.dp)
                )
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier
                .clickable {
                    when {

                        name.isEmpty() -> {
                            Toast.makeText(context, " Please Enter Name", Toast.LENGTH_SHORT).show()
                        }

                        dobDate.isBlank() -> {
                            Toast.makeText(context, " Please Select DOB", Toast.LENGTH_SHORT).show()
                        }

                        city.isEmpty() -> {
                            Toast.makeText(context, " Please Enter City", Toast.LENGTH_SHORT).show()
                        }

                        email.isEmpty() -> {
                            Toast.makeText(context, " Please Enter Mail", Toast.LENGTH_SHORT).show()
                        }

                        password.isEmpty() -> {
                            Toast.makeText(context, " Please Enter Password", Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {

                            val userData = UserData(
                                fullname = name,
                                dob = dobDate,
                                email = email,
                                city = city,
                                password = password
                            )

                            val db = FirebaseDatabase.getInstance()
                            val ref = db.getReference("Accounts")

                            ref.child(userData.email.replace(".", ","))
                                .setValue(userData)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        Toast.makeText(
                                            context,
                                            "Registration Successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        navController.navigate(AppScreens.Login.route) {
                                            popUpTo(AppScreens.Register.route) {
                                                inclusive = true
                                            }
                                        }
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "User Registration Failed: ${task.exception?.message}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                                .addOnFailureListener { exception ->
                                    Toast.makeText(
                                        context,
                                        "User Registration Failed: ${exception.message}",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }


                        }

                    }
                }
                .width(350.dp)
                .background(
                    color = colorResource(id = R.color.bt_color),
                    shape = RoundedCornerShape(20.dp)
                )
                .border(
                    width = 1.dp,
                    color = colorResource(id = R.color.bt_color),
                    shape = RoundedCornerShape(20.dp)
                )
                .padding(vertical = 12.dp)
                .align(Alignment.CenterHorizontally),
            text = "Register",
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge.copy(
                color = Color.White,
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {

            Text(
                modifier = Modifier,
                text = "Already have an account?  ",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = colorResource(id = R.color.white),
                )
            )


            Text(
                modifier = Modifier
                    .clickable {

                        navController.navigate(AppScreens.Login.route) {
                            popUpTo(AppScreens.Register.route) {
                                inclusive = true
                            }
                        }

                    },
                text = "Login",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = colorResource(id = R.color.white),
                )
            )

        }

        Spacer(modifier = Modifier.height(46.dp))
    }
}

@Composable
fun DOBDateField(label: String, value: String, onClick: () -> Unit) {
    Column {
        Spacer(Modifier.height(6.dp))

        Box {
            OutlinedTextField(
                value = value,
                onValueChange = {},
                readOnly = true,
                placeholder = { Text("Select $label") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = colorResource(R.color.tran_white),
                    focusedContainerColor = colorResource(R.color.tran_white),
                    unfocusedTextColor = Color.White,
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.DateRange,
                        contentDescription = "DOB",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            )
            Box(
                Modifier
                    .matchParentSize()
                    .background(Color.Transparent)
                    .clickable { onClick() }
            )
        }
    }
}


data class UserData(
    val fullname: String = "",
    val dob: String = "",
    val email: String = "",
    val city: String = "",
    val password: String = "",
    val profileImage: String = ""
)
