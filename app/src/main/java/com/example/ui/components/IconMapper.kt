package com.example.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.AirportShuttle
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Flight
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.vector.ImageVector

object IconMapper {
    fun getIcon(name: String): ImageVector {
        return when (name.lowercase().trim()) {
            "badge", "resident_id", "id" -> Icons.Default.Badge
            "flight", "travel", "plane" -> Icons.Default.Flight
            "calendar", "appointments", "date" -> Icons.Default.CalendarMonth
            "fingerprint", "auth", "security" -> Icons.Default.Fingerprint
            "folder", "documents", "files" -> Icons.Default.Folder
            "child_care", "baby", "newborn" -> Icons.Default.ChildCare
            "directions_car", "car", "license", "vehicle" -> Icons.Default.DirectionsCar
            "photo_camera", "camera", "photo" -> Icons.Default.CameraAlt
            "car_crash", "accident" -> Icons.Default.Warning
            "local_shipping", "delivery" -> Icons.Default.LocalShipping
            "description", "document", "authorization" -> Icons.Default.Description
            "volunteer_activism", "charity", "furijat" -> Icons.Default.VolunteerActivism
            "favorite", "ehsan", "heart" -> Icons.Default.Favorite
            "card_travel", "visa" -> Icons.Default.CardTravel
            "location_on", "map_pin", "site" -> Icons.Default.LocationOn
            "group", "workers", "labour", "family" -> Icons.Default.Group
            "person", "profile", "user" -> Icons.Default.Person
            "passport", "public" -> Icons.Default.Public
            "settings" -> Icons.Default.Settings
            "notifications" -> Icons.Default.Notifications
            "lock" -> Icons.Default.Lock
            else -> Icons.Default.Assignment
        }
    }
}
