package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.*

@Composable
fun ScreenResidentId(
    profile: ProfileEntity?,
    onBackClick: () -> Unit,
    onViewDigitalCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            SimpleTopBar(
                title = "My Resident ID",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Accordion: My Resident ID details only (no graphic card)
            DetailAccordion(
                title = "My Resident ID Details",
                icon = Icons.Default.Badge,
                defaultExpanded = true
            ) {
                DetailRow(
                    label = "Resident ID Number",
                    value = profile?.residentIdNumber ?: "2471999587",
                    canCopy = true
                )
                DetailRow(label = "ID Version", value = profile?.residentIdVersion ?: "1")
                DetailRow(label = "Issuing Date", value = profile?.residentIssueDate ?: "23/09/2019")
                DetailRow(label = "Expiry Date", value = profile?.residentExpiryDate ?: "08/05/2027")
            }
        }
    }
}
