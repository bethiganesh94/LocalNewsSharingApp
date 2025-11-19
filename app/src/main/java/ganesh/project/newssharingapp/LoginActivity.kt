package ganesh.project.newssharingapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.database.FirebaseDatabase
import kotlin.jvm.java

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoginScreen()
        }
    }
}


@Composable
fun LoginScreen()
{

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val context = LocalContext.current as Activity


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = colorResource(R.color.bg_main)),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

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
                text = "Login to use app",
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
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter Email") },
            textStyle = TextStyle(color = colorResource(id = R.color.white)),
            leadingIcon = {
                Image(
                    painter = painterResource(R.drawable.email),
                    contentDescription = ""

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
                Image(
                    painter = painterResource(R.drawable.password),
                    contentDescription = ""

                )
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        Text(
            modifier = Modifier
                .clickable {
                    when {
                        email.isEmpty() -> {
                            Toast.makeText(context, " Please Enter Mail", Toast.LENGTH_SHORT).show()
                        }

                        password.isEmpty() -> {
                            Toast.makeText(context, " Please Enter Password", Toast.LENGTH_SHORT)
                                .show()
                        }

                        else -> {

                            val userEmail = email
                            val userPassword = password

                            val database = FirebaseDatabase.getInstance()
                            val databaseReference = database.reference

                            val sanitizedEmail = userEmail.replace(".", ",")

                            databaseReference.child("Accounts").child(sanitizedEmail).get()
                                .addOnSuccessListener { snapshot ->
                                    if (snapshot.exists()) {
                                        val travelGuideData =
                                            snapshot.getValue(UserData::class.java)
                                        travelGuideData?.let {
                                            if (userPassword == it.password) {
                                                Toast.makeText(
                                                    context,
                                                    "Login Successfull",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                                context.startActivity(Intent(context, HomeActivity::class.java))
                                                context.finish()

                                            } else {
                                                Toast.makeText(
                                                    context,
                                                    "Incorrect Credentials",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "No User Found", Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                }.addOnFailureListener { exception ->
                                    println("Error retrieving data: ${exception.message}")
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
            text = "Login",
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
                text = "New Here?  ",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = colorResource(id = R.color.white),
                )
            )


            Text(
                modifier = Modifier
                    .clickable {
                        context.startActivity(Intent(context, RegistrationActivity::class.java))
                        context.finish()
                    },
                text = "Create Account",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = colorResource(id = R.color.white),
                )
            )

        }

        Spacer(modifier = Modifier.height(46.dp))
    }
}