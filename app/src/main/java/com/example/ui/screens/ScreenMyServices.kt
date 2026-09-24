package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.AppBrandingEntity
import com.example.data.entity.ServiceEntity
import com.example.ui.components.*
import com.example.ui.theme.*

@Composable
fun ScreenMyServices(
    branding: AppBrandingEntity?,
    services: List<ServiceEntity>,
    onServiceClick: (ServiceEntity) -> Unit,
    onSettingsClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onChatClick: () -> Unit,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val myServicesList = services.filter {
        it.category == "MY_SERVICES" &&
        (searchQuery.isBlank() || it.title.contains(searchQuery, ignoreCase = true))
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = CivilBackground,
        topBar = {
            CivilTopHeader(
                appName = branding?.appName ?: "Civil ID",
                primaryLogoPath = branding?.primaryLogoPath ?: "",
                secondaryLogoPath = branding?.secondaryLogoPath ?: "",
                onSettingsClick = onSettingsClick,
                onNotificationClick = onNotificationClick
            )
        },
        bottomBar = {
            CivilBottomNavigation(
                selectedTab = NavTab.MY_SERVICES,
                onTabSelected = onTabSelected,
                showFamily = branding?.showFamilyTab ?: true,
                showWorkers = branding?.showWorkersTab ?: true,
                showOther = branding?.showOtherServicesTab ?: true
            )
        },
        floatingActionButton = {
            if (branding?.floatingChatEnabled != false) {
                FloatingChatButton(onClick = onChatClick)
            }
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
            contentPadding = PaddingValues(top = 8.dp, bottom = 24.dp)
        ) {
            // Page Title
            item(span = { GridItemSpan(2) }) {
                Text(
                    text = "My Services",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = CivilTextPrimary,
                    modifier = Modifier
                        .padding(top = 4.dp, bottom = 4.dp)
                        .testTag("my_services_title")
                )
            }

            // Search Bar (matching Screenshot 4)
            item(span = { GridItemSpan(2) }) {
                SearchInputField(
                    query = searchQuery,
                    onQueryChange = { searchQuery = it },
                    placeholder = "Search by Service..."
                )
            }

            // 2-column Grid of Services
            items(myServicesList) { service ->
                ServiceCard(
                    title = service.title,
                    iconName = service.iconName,
                    onClick = { onServiceClick(service) }
                )
            }
        }
    }
}
