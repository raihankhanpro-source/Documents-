package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.R
import com.example.data.entity.ProfileEntity
import com.example.storage.LocalFileManager
import com.example.ui.components.DetailRow
import com.example.ui.components.SimpleTopBar
import com.example.ui.theme.*

@Composable
fun ScreenDigitalDocumentDetail(
    profile: ProfileEntity?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            SimpleTopBar(
                title = "Digital Document",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Full Digital Resident ID Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, RoundedCornerShape(20.dp))
                    .testTag("full_digital_id_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.5.dp, CivilGreenDark.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Top Green Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    listOf(CivilBannerGreen, CivilGreenPrimary)
                                )
                            )
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "هوية مقيم",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = "RESIDENT IDENTITY CARD",
                                    color = Color.White.copy(alpha = 0.9f),
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            }

                            // Security Chip Emblem
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = "Chip",
                                    tint = Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Card Content
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Photo + Names Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Avatar Photo
                            Box(
                                modifier = Modifier
                                    .size(88.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .border(1.5.dp, CivilGreenPrimary, RoundedCornerShape(10.dp))
                            ) {
                                val avatar = profile?.avatarPath ?: ""
                                if (LocalFileManager.fileExists(avatar)) {
                                    AsyncImage(
                                        model = avatar,
                                        contentDescription = "ID Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                } else {
                                    Image(
                                        painter = painterResource(id = R.drawable.default_avatar),
                                        contentDescription = "ID Photo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(14.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = profile?.arabicName ?: "عزيزال اسلام حسين و",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    ),
                                    color = CivilTextPrimary
                                )
                                Text(
                                    text = profile?.fullName ?: "AZIZUL ISMAIL HOSSAIN O",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 13.sp
                                    ),
                                    color = CivilTextSecondary
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = CivilGreenLight
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "ID: ",
                                            fontSize = 11.sp,
                                            color = CivilTextSecondary
                                        )
                                        Text(
                                            text = profile?.residentIdNumber ?: "2471999587",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 13.sp,
                                            fontFamily = FontFamily.Monospace,
                                            color = CivilGreenDark
                                        )
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = CivilBorder.copy(alpha = 0.5f))

                        // Details
                        DetailRow(label = "Nationality", value = profile?.birthCountry ?: "Bangladesh")
                        DetailRow(label = "Date of Birth", value = profile?.dateOfBirth ?: "01/01/1981")
                        DetailRow(label = "Profession / المهنة", value = profile?.profession ?: "دهان")
                        DetailRow(label = "Work Location", value = profile?.workLocation ?: "منطقة الرياض")
                        DetailRow(label = "Sponsor / صاحب العمل", value = profile?.sponsorName ?: "مؤسسة الضمان العربي للمقاولات")
                        DetailRow(label = "Issue Date", value = profile?.residentIssueDate ?: "23/09/2019")
                        DetailRow(label = "Expiry Date", value = profile?.residentExpiryDate ?: "08/05/2027")

                        HorizontalDivider(color = CivilBorder.copy(alpha = 0.5f))

                        // QR & Barcode Section
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = "QR",
                                    tint = CivilGreenDark,
                                    modifier = Modifier.size(54.dp)
                                )
                                Text(
                                    text = "SCAN VERIFICATION",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CivilTextMuted
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                // Stylized Barcode
                                Row(
                                    modifier = Modifier
                                        .height(36.dp)
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    val barWidths = listOf(2, 4, 1, 3, 2, 5, 2, 1, 4, 2, 3, 1, 2, 4, 2)
                                    for (w in barWidths) {
                                        Box(
                                            modifier = Modifier
                                                .width(w.dp)
                                                .fillMaxHeight()
                                                .background(CivilGreenDark)
                                        )
                                    }
                                }
                                Text(
                                    text = profile?.residentIdNumber ?: "2471999587",
                                    fontFamily = FontFamily.Monospace,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CivilTextSecondary
                                )
                            }
                        }
                    }

                    // Bottom Verification Footer
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CivilGreenLight)
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Security,
                                contentDescription = null,
                                tint = CivilGreenPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "100% OFFLINE ENCRYPTED DIGITAL IDENTITY",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CivilGreenDark
                            )
                        }
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val id = profile?.residentIdNumber ?: "2471999587"
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Civil ID", id))
                        Toast.makeText(context, "ID copied: $id", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = CivilGreenPrimary)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy ID", fontWeight = FontWeight.SemiBold)
                }

                Button(
                    onClick = onBackClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CivilGreenPrimary)
                ) {
                    Text("Done", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
