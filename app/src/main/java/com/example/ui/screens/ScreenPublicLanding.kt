package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.entity.ServiceEntity
import com.example.storage.LocalFileManager
import com.example.ui.components.CivilTopHeader
import com.example.ui.components.ServiceCard
import com.example.ui.theme.*

@Composable
fun ScreenPublicLanding(
    appName: String,
    primaryLogoPath: String,
    secondaryLogoPath: String,
    loginImagePath: String,
    footerText: String,
    publicServices: List<ServiceEntity>,
    onLoginClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onServiceClick: (ServiceEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            CivilTopHeader(
                appName = appName,
                primaryLogoPath = primaryLogoPath,
                secondaryLogoPath = secondaryLogoPath,
                onSettingsClick = onSettingsClick,
                onNotificationClick = onNotificationClick
            )
        }
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 32.dp, top = 8.dp)
        ) {
            // Hero Login Card (matches Screenshot 1)
            item(span = { GridItemSpan(2) }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(2.dp, RoundedCornerShape(20.dp))
                        .testTag("public_hero_login_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Hero Logo Emblem
                        Box(
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (LocalFileManager.fileExists(loginImagePath)) {
                                AsyncImage(
                                    model = loginImagePath,
                                    contentDescription = "Portal Emblem",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else if (LocalFileManager.fileExists(primaryLogoPath)) {
                                AsyncImage(
                                    model = primaryLogoPath,
                                    contentDescription = "Portal Emblem",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Image(
                                    painter = painterResource(id = R.drawable.ic_placeholder_logo),
                                    contentDescription = "Portal Emblem",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Log In Button
                        Button(
                            onClick = onLoginClick,
                            shape = RoundedCornerShape(24.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary),
                            modifier = Modifier
                                .fillMaxWidth(0.65f)
                                .height(46.dp)
                                .testTag("public_login_button")
                        ) {
                            Text(
                                text = "Log In",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                ),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Section Header: Public Services
            item(span = { GridItemSpan(2) }) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Public Services",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = CivilTextPrimary
                    )

                    Text(
                        text = "See All",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        ),
                        color = CivilGreenPrimary,
                        modifier = Modifier
                            .clickable { onLoginClick() }
                            .testTag("public_services_see_all")
                    )
                }
            }

            // Grid items for Public Services
            items(publicServices) { service ->
                ServiceCard(
                    title = service.title,
                    iconName = service.iconName,
                    onClick = { onServiceClick(service) }
                )
            }

            // Footer note
            item(span = { GridItemSpan(2) }) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 28.dp, bottom = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = footerText,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = CivilTextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
