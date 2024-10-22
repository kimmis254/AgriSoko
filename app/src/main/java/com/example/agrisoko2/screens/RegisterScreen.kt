import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberImagePainter
import com.example.agrisoko2.ui.components.AnimatedGradientBackground
import com.example.agrisoko2.ui.components.StylishAppIconWithGradient
import com.example.agrisoko2.viewmodels.AuthenticationViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(navController: NavController, viewModel: AuthenticationViewModel = AuthenticationViewModel()) {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedGradientBackground()

        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var selectedRole by remember { mutableStateOf("customer") }
        var errorMessage by remember { mutableStateOf("") }
        var name by remember { mutableStateOf("") }
        var contact by remember { mutableStateOf("") }

        var nameError by remember { mutableStateOf(false) }
        var contactError by remember { mutableStateOf(false) }
        var emailError by remember { mutableStateOf(false) }
        var passwordError by remember { mutableStateOf(false) }
        var isLoading by remember { mutableStateOf(false) }

        var profilePictureUri by remember { mutableStateOf<Uri?>(null) }
        val imagePickerLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent(),
            onResult = { uri -> profilePictureUri = uri }
        )

        // Make the content scrollable
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())  // Ensure vertical scrolling if content overflows
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            StylishAppIconWithGradient(size = 100.dp)

            Spacer(modifier = Modifier.height(32.dp))

            // Profile picture picker
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.Gray),
                contentAlignment = Alignment.Center
            ) {
                if (profilePictureUri != null) {
                    Image(
                        painter = rememberImagePainter(profilePictureUri),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Text(text = "Add Photo", color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = { imagePickerLauncher.launch("image/*") }) {
                Text("Select Profile Picture")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Fields
            RegisterFormFields(
                name = name,
                onNameChange = {
                    name = it
                    nameError = it.isBlank()
                },
                nameError = nameError,
                contact = contact,
                onContactChange = {
                    contact = it
                    contactError = contact.length < 10 || contact.any { !it.isDigit() }
                },
                contactError = contactError,
                email = email,
                onEmailChange = {
                    email = it
                    emailError = !android.util.Patterns.EMAIL_ADDRESS.matcher(it).matches()
                },
                emailError = emailError,
                password = password,
                onPasswordChange = {
                    password = it
                    passwordError = password.length < 6
                },
                passwordError = passwordError
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Role selection
            Text(text = "Choose Your Role", modifier = Modifier.padding(bottom = 8.dp))
            RoleSelection(selectedRole, onRoleChange = { selectedRole = it })

            Spacer(modifier = Modifier.height(16.dp))

            // Sign Up Button
            Button(
                onClick = {
                    isLoading = true
                    viewModel.registerUser(email, password, name, contact, selectedRole, {
                        isLoading = false
                        if (selectedRole == "farmer") {
                            navController.navigate("farmer_home")
                        } else {
                            navController.navigate("customer_home")
                        }
                    }, { error ->
                        isLoading = false
                        errorMessage = error
                    })
                },
                enabled = !nameError && !contactError && !emailError && !passwordError,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = Color.White
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Text("Sign Up", style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                }
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { navController.navigate("login_screen") }) {
                Text("Already have an account? Login!", color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}

@Composable
fun RegisterFormFields(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: Boolean,
    contact: String,
    onContactChange: (String) -> Unit,
    contactError: Boolean,
    email: String,
    onEmailChange: (String) -> Unit,
    emailError: Boolean,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordError: Boolean
) {
    // Name field
    TextField(
        value = name,
        onValueChange = onNameChange,
        label = { Text("Enter your name") },
        isError = nameError,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
    if (nameError) {
        Text(text = "Name cannot be empty", color = MaterialTheme.colorScheme.error)
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Contact field
    TextField(
        value = contact,
        onValueChange = onContactChange,
        label = { Text("Enter your number") },
        isError = contactError,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
    if (contactError) {
        Text(
            text = "Phone number should be at least 10 digits and numeric",
            color = MaterialTheme.colorScheme.error
        )
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Email field
    TextField(
        value = email,
        onValueChange = onEmailChange,
        label = { Text("Enter your email") },
        isError = emailError,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    )
    if (emailError) {
        Text(text = "Invalid email address", color = MaterialTheme.colorScheme.error)
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Password field
    TextField(
        value = password,
        onValueChange = onPasswordChange,
        label = { Text("Enter your password") },
        isError = passwordError,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        visualTransformation = PasswordVisualTransformation()
    )
    if (passwordError) {
        Text(
            text = "Password should be at least 6 characters",
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
fun RoleSelection(selectedRole: String, onRoleChange: (String) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedRole == "farmer",
                onClick = { onRoleChange("farmer") }
            )
            Text(text = "Farmer", style = MaterialTheme.typography.bodyMedium)
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = selectedRole == "customer",
                onClick = { onRoleChange("customer") }
            )
            Text(text = "Customer", style = MaterialTheme.typography.bodyMedium)
        }
    }
}
