package com.unitip.mobile.shared.domain.models

import androidx.compose.ui.graphics.vector.ImageVector

data class Role(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val role: String
)