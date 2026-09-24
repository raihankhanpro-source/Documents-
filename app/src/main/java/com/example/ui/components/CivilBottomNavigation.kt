package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CivilGreenDark
import com.example.ui.theme.CivilGreenLight
import com.example.ui.theme.CivilGreenPrimary
import com.example.ui.theme.CivilTextSecondary

enum class NavTab {
    HOME,
    MY_SERVICES,
    FAMILY,
    WORKERS,
    OTHER_SERVICES
}

@Composable
fun CivilBottomNavigation(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    showFamily: Boolean = true,
    showWorkers: Boolean = true,
    showOther: Boolean = true,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .height(68.dp),
        containerColor = Color.White,
        tonalElevation = 6.dp
    ) {
        NavigationBarItem(
            selected = selectedTab == NavTab.HOME,
            onClick = { onTabSelected(NavTab.HOME) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "Home",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == NavTab.HOME) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CivilGreenPrimary,
                selectedTextColor = CivilGreenPrimary,
                indicatorColor = CivilGreenLight,
                unselectedIconColor = CivilTextSecondary,
                unselectedTextColor = CivilTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_home")
        )

        NavigationBarItem(
            selected = selectedTab == NavTab.MY_SERVICES,
            onClick = { onTabSelected(NavTab.MY_SERVICES) },
            icon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "My Services",
                    modifier = Modifier.size(24.dp)
                )
            },
            label = {
                Text(
                    text = "My Services",
                    fontSize = 11.sp,
                    fontWeight = if (selectedTab == NavTab.MY_SERVICES) FontWeight.Bold else FontWeight.Normal
                )
            },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = CivilGreenPrimary,
                selectedTextColor = CivilGreenPrimary,
                indicatorColor = CivilGreenLight,
                unselectedIconColor = CivilTextSecondary,
                unselectedTextColor = CivilTextSecondary
            ),
            modifier = Modifier.testTag("nav_item_my_services")
        )

        if (showFamily) {
            NavigationBarItem(
                selected = selectedTab == NavTab.FAMILY,
                onClick = { onTabSelected(NavTab.FAMILY) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = "Family",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = "Family",
                        fontSize = 11.sp,
                        fontWeight = if (selectedTab == NavTab.FAMILY) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CivilGreenPrimary,
                    selectedTextColor = CivilGreenPrimary,
                    indicatorColor = CivilGreenLight,
                    unselectedIconColor = CivilTextSecondary,
                    unselectedTextColor = CivilTextSecondary
                ),
                modifier = Modifier.testTag("nav_item_family")
            )
        }

        if (showWorkers) {
            NavigationBarItem(
                selected = selectedTab == NavTab.WORKERS,
                onClick = { onTabSelected(NavTab.WORKERS) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Engineering,
                        contentDescription = "Workers",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = "Workers",
                        fontSize = 11.sp,
                        fontWeight = if (selectedTab == NavTab.WORKERS) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CivilGreenPrimary,
                    selectedTextColor = CivilGreenPrimary,
                    indicatorColor = CivilGreenLight,
                    unselectedIconColor = CivilTextSecondary,
                    unselectedTextColor = CivilTextSecondary
                ),
                modifier = Modifier.testTag("nav_item_workers")
            )
        }

        if (showOther) {
            NavigationBarItem(
                selected = selectedTab == NavTab.OTHER_SERVICES,
                onClick = { onTabSelected(NavTab.OTHER_SERVICES) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Widgets,
                        contentDescription = "Other Services",
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = "Other Services",
                        fontSize = 11.sp,
                        fontWeight = if (selectedTab == NavTab.OTHER_SERVICES) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CivilGreenPrimary,
                    selectedTextColor = CivilGreenPrimary,
                    indicatorColor = CivilGreenLight,
                    unselectedIconColor = CivilTextSecondary,
                    unselectedTextColor = CivilTextSecondary
                ),
                modifier = Modifier.testTag("nav_item_other_services")
            )
        }
    }
}
