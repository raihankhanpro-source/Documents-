package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.storage.LocalFileManager
import com.example.ui.theme.*

@Composable
fun ScreenLogin(
    primaryLogoPath: String,
    loginImagePath: String,
    onLoginSubmit: (username: String, pass: String) -> Unit,
    onBackClick: () -> Unit,
    errorMessage: String? = null,
    modifier: Modifier = Modifier
) {
    var username by remember { mutableStateOf("admin") }
    var password by remember { mutableStateOf("admin123") }
    var keepMeLoggedIn by remember { mutableStateOf(true) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordDialog by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("login_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = CivilGreenDark
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Logo at top center (matching Screenshot 2)
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (LocalFileManager.fileExists(loginImagePath)) {
                    AsyncImage(
                        model = loginImagePath,
                        contentDescription = "Login Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else if (LocalFileManager.fileExists(primaryLogoPath)) {
                    AsyncImage(
                        model = primaryLogoPath,
                        contentDescription = "Login Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.ic_placeholder_logo),
                        contentDescription = "Login Logo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Username or ID Number
            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Username or ID Number") },
                placeholder = { Text("Enter ID or Username") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = CivilGreenPrimary
                    )
                },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_username_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = CivilGreenPrimary,
                    unfocusedBorderColor = CivilCardBorder
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                placeholder = { Text("Enter Password") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = CivilGreenPrimary
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (passwordVisible) "Hide password" else "Show password",
                            tint = CivilTextSecondary
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = {
                    if (username.isNotBlank() && password.isNotBlank()) {
                        onLoginSubmit(username, password)
                    }
                }),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = CivilGreenPrimary,
                    unfocusedBorderColor = CivilCardBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Keep me logged in Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { keepMeLoggedIn = !keepMeLoggedIn },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = keepMeLoggedIn,
                    onCheckedChange = { keepMeLoggedIn = it },
                    colors = CheckboxDefaults.colors(checkedColor = CivilGreenPrimary),
                    modifier = Modifier.testTag("keep_logged_in_checkbox")
                )
                Text(
                    text = "Keep me logged in",
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = CivilTextPrimary
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.testTag("login_error_text")
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Log In Button (matching Screenshot 2)
            Button(
                onClick = {
                    if (username.isNotBlank() && password.isNotBlank()) {
                        onLoginSubmit(username, password)
                    }
                },
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary),
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(48.dp)
                    .testTag("login_submit_button")
            ) {
                Text(
                    text = "Log In",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    ),
                    color = Color.White
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Forgot Password
            TextButton(
                onClick = { showForgotPasswordDialog = true },
                modifier = Modifier.testTag("forgot_password_button")
            ) {
                Text(
                    text = "Forgot Password",
                    color = CivilGreenPrimary,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
            }

            Spacer(modifier = Modifier.height(30.dp))
        }

        if (showForgotPasswordDialog) {
            AlertDialog(
                onDismissRequest = { showForgotPasswordDialog = false },
                title = { Text("Offline Password Recovery", fontWeight = FontWeight.Bold) },
                text = {
                    Text(
                        "Because this application operates 100% offline with encrypted local security, password resets must be performed by the Master Administrator or by entering the Master Account credentials (default: admin / admin123)."
                    )
                },
                confirmButton = {
                    Button(
                        onClick = { showForgotPasswordDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                    ) {
                        Text("Understood", color = Color.White)
                    }
                }
            )
        }
    }
}
