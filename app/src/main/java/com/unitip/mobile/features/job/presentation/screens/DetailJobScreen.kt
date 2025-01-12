package com.unitip.mobile.features.job.presentation.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.composables.icons.lucide.Bike
import com.composables.icons.lucide.ChevronLeft
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.MapPin
import com.composables.icons.lucide.MapPinned
import com.composables.icons.lucide.RefreshCw
import com.unitip.mobile.features.job.commons.JobRoutes
import com.unitip.mobile.features.job.presentation.states.DetailJobState
import com.unitip.mobile.features.job.presentation.viewmodels.DetailJobViewModel
import com.unitip.mobile.shared.commons.compositional.LocalNavController
import com.unitip.mobile.shared.presentation.components.CustomIconButton

@Composable
fun DetailJobScreen(
    id: String,
    type: String,
    viewModel: DetailJobViewModel = hiltViewModel()
) {
    val navController = LocalNavController.current

    val listState = rememberLazyListState()
    val uiState by viewModel.uiState.collectAsState()
    var isFetched by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(id, type, isFetched) {
        if (!isFetched) {
            viewModel.fetchData(jobId = id, type = type)
            isFetched = true
        }
    }

    Scaffold {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.surfaceContainerHigh,
                            MaterialTheme.colorScheme.surfaceContainerLowest,
                        )
                    )
                )
                .padding(it)
        ) {
            // app bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                CustomIconButton(
                    onClick = { navController.popBackStack() },
                    icon = Lucide.ChevronLeft
                )
                CustomIconButton(
                    onClick = { viewModel.fetchData(jobId = id, type = type) },
                    icon = Lucide.RefreshCw
                )
            }

            // loading state
            if (uiState.detail is DetailJobState.Detail.Loading) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(strokeCap = StrokeCap.Round)
                }
            }

            // success state
            if (uiState.detail is DetailJobState.Detail.Success) {
                AnimatedVisibility(visible = listState.canScrollBackward) {
                    HorizontalDivider()
                }

                LazyColumn(modifier = Modifier.weight(1f), state = listState) {
                    item {
                        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp)) {
                            Text(
                                text = uiState.job.title,
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = uiState.job.note,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    // details job
                    itemsIndexed(
                        listOf(
                            mapOf(
                                "icon" to Lucide.Bike,
                                "title" to "Jenis Pekerjaan",
                                "value" to uiState.job.type
                            ),
                            mapOf(
                                "icon" to Lucide.MapPinned,
                                "title" to "Titik Jemput",
                                "value" to uiState.job.pickupLocation
                            ),
                            mapOf(
                                "icon" to Lucide.MapPin,
                                "title" to "Destinasi",
                                "value" to uiState.job.destination
                            ),
                        )
                    ) { index, item ->
                        Box(
                            modifier = Modifier
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = if (index == 0) 16.dp else 4.dp
                                )
                                .clip(
                                    RoundedCornerShape(
                                        topStart = if (index == 0) 16.dp else 4.dp,
                                        topEnd = if (index == 0) 16.dp else 4.dp,
                                        bottomStart = if (index == 2) 16.dp else 4.dp,
                                        bottomEnd = if (index == 2) 16.dp else 4.dp
                                    )
                                )
                                .background(
                                    MaterialTheme.colorScheme.onSurface.copy(
                                        alpha = .08f
                                    )
                                )
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(MaterialTheme.colorScheme.onSurface)
                                ) {
                                    Icon(
                                        item["icon"]!! as ImageVector,
                                        contentDescription = null,
                                        modifier = Modifier
                                            .align(Alignment.Center)
                                            .size(20.dp),
                                        tint = MaterialTheme.colorScheme.surface
                                    )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item["title"]!! as String,
                                        style = MaterialTheme.typography.titleSmall
                                    )
                                    Text(
                                        text = item["value"]!! as String,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    // daftar applicants
                    item {
                        HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
                        Column(
                            modifier = Modifier.padding(
                                start = 16.dp,
                                end = 16.dp,
                                top = 16.dp
                            )
                        ) {
                            Text(
                                text = "Daftar Pelamar",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "Berikut adalah daftar pelamar yang telah mengajukan lamaran untuk pekerjaan ini",
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }

                    itemsIndexed(uiState.job.applicants) { index, applicant ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    start = 16.dp,
                                    end = 16.dp,
                                    top = if (index == 0) 16.dp else 8.dp
                                )
                                .clip(RoundedCornerShape(16.dp))
                                .background(MaterialTheme.colorScheme.onSurface.copy(alpha = .08f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = applicant.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Text(
                                        text = "Rp ${applicant.price}",
                                        style = MaterialTheme.typography.labelSmall
                                    )
                                }
                                if (false) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Ayo kak, free open BO! Nanti klo ini catatean e panhjang gimana dong, kan bakal nabrak badge e",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }

                HorizontalDivider()

                Button(
                    onClick = {
                        navController.navigate(
                            JobRoutes.Apply(
                                id = id,
                                type = type
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp, top = 16.dp)
                ) {
                    Text(text = "Lamar pekerjaan")
                }
            }
        }
    }
}