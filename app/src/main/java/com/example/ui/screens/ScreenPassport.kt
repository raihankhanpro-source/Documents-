package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ProfileEntity
import com.example.ui.components.DetailAccordion
import com.example.ui.components.DetailRow
import com.example.ui.components.SimpleTopBar
import com.example.ui.theme.CivilBackground
import com.example.ui.theme.CivilCardBorder
import com.example.ui.theme.CivilGreenDark
import com.example.ui.theme.CivilTextSecondary

@Composable
fun ScreenPassport(
    profile: ProfileEntity?,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            SimpleTopBar(
                title = "My Passport",
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Amount Deposit Card (matching Screenshot 12)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("passport_amount_deposit_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, CivilCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Amount deposit",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        ),
                        color = CivilTextSecondary
                    )

                    Text(
                        text = profile?.passportDepositAmount ?: "SAR 00.0",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        ),
                        color = CivilGreenDark
                    )
                }
            }

            // Normal Passport Accordion (matching Screenshot 12)
            DetailAccordion(
                title = "Normal Passport",
                icon = Icons.Default.Public,
                defaultExpanded = true
            ) {
                DetailRow(
                    label = "Passport Number",
                    value = profile?.passportNumber ?: "A20144174",
                    canCopy = true
                )
                DetailRow(label = "Type", value = profile?.passportType ?: "Normal")
                DetailRow(label = "Issuing Date", value = profile?.passportIssueDate ?: "03/09/2025")
                DetailRow(label = "Expiry Date", value = profile?.passportExpiryDate ?: "02/09/2035")
                DetailRow(label = "Issuing City", value = profile?.passportIssueCity ?: "113")
                DetailRow(label = "Status", value = profile?.passportStatus ?: "-")
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}
