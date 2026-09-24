package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.entity.ProfileEntity
import com.example.storage.LocalFileManager
import com.example.ui.components.DetailAccordion
import com.example.ui.components.DetailRow
import com.example.ui.components.SimpleTopBar
import com.example.ui.theme.CivilBackground
import com.example.ui.theme.CivilGreenLight

@Composable
fun ScreenPersonalDetails(
    profile: ProfileEntity?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            SimpleTopBar(
                title = "My Personal Details",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Accordion 1: Personal Details (matching Screenshot 10)
            DetailAccordion(
                title = "Personal Details",
                icon = Icons.Default.Person,
                defaultExpanded = true
            ) {
                // Photo thumbnail in accordion
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(CivilGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        val avatar = profile?.avatarPath ?: ""
                        if (LocalFileManager.fileExists(avatar)) {
                            AsyncImage(
                                model = avatar,
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.default_avatar),
                                contentDescription = "Avatar",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }

                DetailRow(label = "Name", value = profile?.fullName ?: "AZIZUL ISMAIL HOSSAIN O")
                DetailRow(label = "Birth City", value = profile?.birthCity ?: "-")
                DetailRow(label = "Birth Country/Region", value = profile?.birthCountry ?: "Bangladesh")
                DetailRow(label = "Date of Birth", value = profile?.dateOfBirth ?: "01/01/1981")
                DetailRow(label = "Marital Status", value = profile?.maritalStatus ?: "Married")
                DetailRow(label = "No. of sponsorship transfers", value = (profile?.sponsorshipTransfers ?: 0).toString())
                DetailRow(label = "Religion", value = profile?.religion ?: "Islam")
                DetailRow(label = "Work Permit", value = profile?.workPermit ?: "-")
                DetailRow(label = "Biometrics Collected", value = profile?.biometricsCollected ?: "Yes")
                DetailRow(label = "Travel Status", value = profile?.travelStatus ?: "Inside")
            }

            // Accordion 2: Sponsor Details (matching Screenshot 11)
            DetailAccordion(
                title = "Sponsor Details",
                icon = Icons.Default.Apartment,
                defaultExpanded = true
            ) {
                DetailRow(label = "Sponsor Name", value = profile?.sponsorName ?: "مؤسسة الضمان العربي للمقاولات")
                DetailRow(
                    label = "Sponsor ID Number",
                    value = profile?.sponsorId ?: "7004899147",
                    canCopy = true
                )
            }

            // Accordion 3: Health Insurance (matching Screenshot 11)
            DetailAccordion(
                title = "Health Insurance",
                icon = Icons.Default.HealthAndSafety,
                defaultExpanded = true
            ) {
                DetailRow(label = "Issuing Date", value = profile?.insuranceIssuingDate ?: "18/06/2026")
                DetailRow(label = "Expiry Date", value = profile?.insuranceExpiryDate ?: "17/06/2027")
                DetailRow(label = "Blood Type", value = profile?.bloodType ?: "-")
            }

            // Accordion 4: Hajj Details (matching Screenshot 11)
            DetailAccordion(
                title = "Hajj Details",
                icon = Icons.Default.Mosque,
                defaultExpanded = true
            ) {
                DetailRow(
                    label = "Hajj Status",
                    value = "",
                    badgeText = profile?.hajjStatus ?: "Eligible for Hajj"
                )
                DetailRow(label = "Last Hajj year", value = profile?.lastHajjYear ?: "-")
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
