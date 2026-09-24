package com.example.ui.screens

import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.AsyncImage
import com.example.R
import com.example.data.entity.AppBrandingEntity
import com.example.data.entity.DocumentEntity
import com.example.data.entity.ProfileEntity
import com.example.data.entity.QuickAccessItemEntity
import com.example.storage.LocalFileManager
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ScreenHome(
    branding: AppBrandingEntity?,
    profile: ProfileEntity?,
    quickAccessItems: List<QuickAccessItemEntity>,
    documents: List<DocumentEntity>,
    onProfileClick: () -> Unit,
    onDigitalDocClick: () -> Unit,
    onQuickAccessClick: (QuickAccessItemEntity) -> Unit,
    onSearchClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onChatClick: () -> Unit,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedDocForPopup by remember { mutableStateOf<DocumentEntity?>(null) }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = CivilBackground,
            topBar = {
                CivilTopHeader(
                    appName = branding?.appName ?: "Civil ID",
                    primaryLogoPath = branding?.primaryLogoPath ?: "",
                    secondaryLogoPath = branding?.secondaryLogoPath ?: "",
                    onSettingsClick = onSettingsClick,
                    onNotificationClick = onNotificationClick,
                    onSearchClick = onSearchClick
                )
            },
            bottomBar = {
                CivilBottomNavigation(
                    selectedTab = NavTab.HOME,
                    onTabSelected = onTabSelected,
                    showFamily = branding?.showFamilyTab ?: true,
                    showWorkers = branding?.showWorkersTab ?: true,
                    showOther = branding?.showOtherServicesTab ?: true
                )
            }
            // FloatingActionButton removed as requested ("Remove the unwanted extra dot/floating element")
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
            ) {
                // Profile Card
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onProfileClick)
                            .testTag("home_profile_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        border = BorderStroke(1.dp, CivilCardBorder)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(CivilGreenLight),
                                contentAlignment = Alignment.Center
                            ) {
                                val avatar = profile?.avatarPath ?: ""
                                if (LocalFileManager.fileExists(avatar)) {
                                    AsyncImage(
                                        model = avatar,
                                        contentDescription = "Profile",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.default_avatar),
                                        contentDescription = "Profile",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile?.fullName ?: "AZIZUL ISMAIL HOSSAIN O",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    ),
                                    color = CivilTextPrimary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "ID No. ",
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = CivilTextSecondary
                                    )
                                    Text(
                                        text = profile?.idNumber ?: "2471999587",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontWeight = FontWeight.SemiBold,
                                            fontSize = 12.sp,
                                            fontFamily = FontFamily.Monospace
                                        ),
                                        color = CivilGreenDark
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.Default.ChevronRight,
                                contentDescription = "View Profile",
                                tint = CivilGreenPrimary,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Search Bar
                item {
                    SearchInputField(
                        query = searchQuery,
                        onQueryChange = { searchQuery = it },
                        placeholder = "Look for a Service",
                        onSearch = onSearchClick
                    )
                }

                // Section Header: My Digital Documents
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "My Digital Documents",
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
                                .clickable { onDigitalDocClick() }
                                .testTag("see_all_documents_link")
                        )
                    }
                }

                // Digital Document / ID Card matching Reference Image 1
                val docsToShow = if (documents.isNotEmpty()) {
                    documents
                } else {
                    listOf(
                        DocumentEntity(
                            id = 1,
                            title = "Resident Identity Card (هوية مقيم)",
                            documentNumber = profile?.residentIdNumber ?: "2471999587",
                            category = "RESIDENT_ID",
                            issueDate = profile?.residentIssueDate ?: "23/09/2019",
                            expiryDate = profile?.residentExpiryDate ?: "08/05/2027",
                            notes = "Primary Digital Resident ID"
                        )
                    )
                }

                items(docsToShow) { doc ->
                    if (doc.localFilePath.isNotBlank() && LocalFileManager.fileExists(doc.localFilePath)) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable { selectedDocForPopup = doc }
                                .testTag("document_image_card_${doc.id}"),
                            shape = RoundedCornerShape(16.dp),
                            border = BorderStroke(1.dp, CivilCardBorder)
                        ) {
                            AsyncImage(
                                model = doc.localFilePath,
                                contentDescription = doc.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        DigitalResidentIdCard(
                            fullName = profile?.fullName ?: "AZIZUL ISMAIL HOSSAIN O",
                            arabicName = profile?.arabicName ?: "عزيزال اسلام حسين و",
                            idNumber = doc.documentNumber.ifBlank { profile?.residentIdNumber ?: "2471999587" },
                            expiryDate = doc.expiryDate.ifBlank { profile?.residentExpiryDate ?: "08/05/2027" },
                            profession = doc.notes.ifBlank { profile?.profession ?: "دهان" },
                            avatarPath = profile?.avatarPath ?: "",
                            onClick = { selectedDocForPopup = doc },
                            modifier = Modifier.testTag("document_card_${doc.id}")
                        )
                    }
                }

                // Section Header: Quick Access
                item {
                    Text(
                        text = "Quick Access",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = CivilTextPrimary,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                val filteredQuick = if (searchQuery.isBlank()) {
                    quickAccessItems
                } else {
                    quickAccessItems.filter {
                        it.title.contains(searchQuery, ignoreCase = true) ||
                        it.subtitle.contains(searchQuery, ignoreCase = true)
                    }
                }

                items(filteredQuick) { item ->
                    QuickAccessCard(
                        title = item.title,
                        subtitle = item.subtitle,
                        iconName = item.iconName,
                        onClick = { onQuickAccessClick(item) }
                    )
                }
            }
        }

        // Full-Screen 90° Document Viewer (Matching Reference Image 2: Dark background, 90° rotation, X close button)
        if (selectedDocForPopup != null) {
            val doc = selectedDocForPopup!!
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black)
                    .zIndex(100f)
            ) {
                // X Close button at top right
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .padding(16.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(
                        onClick = { selectedDocForPopup = null },
                        modifier = Modifier
                            .size(48.dp)
                            .testTag("close_document_viewer_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                // Centered 90-degree rotated document view
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .height(340.dp)
                            .graphicsLayer(rotationZ = 90f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        val path = doc.localFilePath
                        if (path.isNotBlank() && LocalFileManager.fileExists(path)) {
                            AsyncImage(
                                model = path,
                                contentDescription = doc.title,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            DigitalResidentIdCard(
                                fullName = profile?.fullName ?: "AZIZUL ISMAIL HOSSAIN O",
                                arabicName = profile?.arabicName ?: "عزيزال اسلام حسين و",
                                idNumber = doc.documentNumber.ifBlank { profile?.residentIdNumber ?: "2471999587" },
                                expiryDate = doc.expiryDate.ifBlank { profile?.residentExpiryDate ?: "08/05/2027" },
                                profession = doc.notes.ifBlank { profile?.profession ?: "دهان" },
                                avatarPath = profile?.avatarPath ?: "",
                                onClick = {}
                            )
                        }
                    }
                }
            }
        }
    }
}
