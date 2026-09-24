package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.ui.theme.CivilCardBorder
import com.example.ui.theme.CivilGreenDark
import com.example.ui.theme.CivilGreenLight
import com.example.ui.theme.CivilGreenPrimary
import com.example.ui.theme.CivilTextMuted
import com.example.ui.theme.CivilTextSecondary

@Composable
fun ScreenFirstTimeSetup(
    onSetupCompleted: () -> Unit,
    onCompleteSetup: (
        masterId: String,
        masterPass: String,
        appName: String,
        profileName: String,
        photoUri: Uri?
    ) -> Unit,
    modifier: Modifier = Modifier
) {
    var masterId by remember { mutableStateOf("admin") }
    var masterPassword by remember { mutableStateOf("admin123") }
    var confirmPassword by remember { mutableStateOf("admin123") }
    var appName by remember { mutableStateOf("Civil ID") }
    var profileName by remember { mutableStateOf("AZIZUL ISMAIL HOSSAIN O") }
    var photoUri by remember { mutableStateOf<Uri?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            photoUri = uri
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(CivilGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Security,
                    contentDescription = "Setup",
                    tint = CivilGreenPrimary,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "First-Time Security Setup",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 22.sp
                ),
                color = CivilGreenDark
            )

            Text(
                text = "Configure your offline Master Control account. All credentials and data remain 100% on this device.",
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                color = CivilTextSecondary,
                modifier = Modifier.padding(top = 6.dp, bottom = 20.dp)
            )

            // Avatar picker
            Box(
                modifier = Modifier
                    .size(90.dp)
                    .clip(CircleShape)
                    .border(2.dp, CivilGreenPrimary, CircleShape)
                    .clickable { photoPickerLauncher.launch("image/*") }
                    .testTag("setup_avatar_picker"),
                contentAlignment = Alignment.Center
            ) {
                if (photoUri != null) {
                    AsyncImage(
                        model = photoUri,
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        painter = painterResource(id = R.drawable.default_avatar),
                        contentDescription = "Profile Photo",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(28.dp)
                        .background(CivilGreenPrimary, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AddPhotoAlternate,
                        contentDescription = "Upload",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            Text(
                text = "Tap to choose profile photo",
                style = MaterialTheme.typography.labelSmall.copy(color = CivilTextMuted),
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Fields
            OutlinedTextField(
                value = masterId,
                onValueChange = { masterId = it },
                label = { Text("Master ID / Username") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = CivilGreenPrimary) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_master_id_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CivilGreenPrimary,
                    unfocusedBorderColor = CivilCardBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = masterPassword,
                onValueChange = { masterPassword = it },
                label = { Text("Master Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CivilGreenPrimary) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_master_password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CivilGreenPrimary,
                    unfocusedBorderColor = CivilCardBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirm Master Password") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = CivilGreenPrimary) },
                visualTransformation = PasswordVisualTransformation(),
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_confirm_password_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CivilGreenPrimary,
                    unfocusedBorderColor = CivilCardBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = appName,
                onValueChange = { appName = it },
                label = { Text("Application Name") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_app_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CivilGreenPrimary,
                    unfocusedBorderColor = CivilCardBorder
                )
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = profileName,
                onValueChange = { profileName = it },
                label = { Text("Owner / Profile Name") },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("setup_profile_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = CivilGreenPrimary,
                    unfocusedBorderColor = CivilCardBorder
                )
            )

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (masterId.isBlank()) {
                        errorMessage = "Please enter Master ID"
                        return@Button
                    }
                    if (masterPassword.length < 4) {
                        errorMessage = "Password must be at least 4 characters"
                        return@Button
                    }
                    if (masterPassword != confirmPassword) {
                        errorMessage = "Passwords do not match"
                        return@Button
                    }
                    errorMessage = null
                    onCompleteSetup(masterId, masterPassword, appName, profileName, photoUri)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("setup_complete_button"),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
            ) {
                Text(
                    text = "Initialize Master Control",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color.White
                )
            }
        }
    }
}
