package ru.itis.android.travel_memorize_app.feature.friends.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.gson.JsonPrimitive
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.extension.style.layers.properties.generated.IconAnchor
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import kotlinx.coroutines.flow.collectLatest
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors
import ru.itis.android.travel_memorize_app.feature.friends.viewmodel.FriendProfileEffect
import ru.itis.android.travel_memorize_app.feature.friends.viewmodel.FriendProfileViewModel
import ru.itis.android.travel_memorize_app.feature.memory.ui.toMemoryMessageRes
import ru.itis.android.travel_memorize_app.map.ui.components.MapMarkerBitmapFactory
import ru.itis.android.travel_memorize_app.ui.R

@Composable
fun FriendMapScreen(
    viewModelFactory: ViewModelProvider.Factory,
    friendId: String,
    onBack: () -> Unit,
    onMemoryClick: (String) -> Unit
) {
    val viewModel: FriendProfileViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val colors = LocalExtendedColors.current
    val snackbarHostState = remember { SnackbarHostState() }
    var errorResId by remember { mutableStateOf<Int?>(null) }
    var annotationManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    var clickListenerAdded by remember { mutableStateOf(false) }
    var markerBitmaps by remember { mutableStateOf<Map<String, Bitmap>>(emptyMap()) }
    val markerColor = MaterialTheme.colorScheme.primary.toArgb()
    val borderColor = MaterialTheme.colorScheme.surface.toArgb()
    val placeholderColor = colors.inputMuted.copy(alpha = 0.45f).toArgb()

    LaunchedEffect(friendId) {
        viewModel.load(friendId)
    }
    LaunchedEffect(state.memories, markerColor, borderColor, placeholderColor) {
        markerBitmaps = state.memories.associate { memory ->
            memory.id to MapMarkerBitmapFactory.create(
                context = context,
                photoUrl = memory.photos.firstOrNull(),
                markerColor = markerColor,
                borderColor = borderColor,
                placeholderColor = placeholderColor
            )
        }
    }
    LaunchedEffect(Unit) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is FriendProfileEffect.ShowError -> {
                    errorResId = effect.error.toMemoryMessageRes()
                }

                FriendProfileEffect.RemovedFromFriends -> Unit
            }
        }
    }
    errorResId?.let { resId ->
        val message = stringResource(resId)
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            errorResId = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { androidContext ->
                annotationManager = null
                clickListenerAdded = false
                MapView(
                    androidContext,
                    MapInitOptions(
                        context = androidContext,
                        styleUri = Style.STANDARD_SATELLITE
                    )
                ).apply {
                    val firstMemory = state.memories.firstOrNull()
                    val defaultCamera = CameraOptions.Builder()
                        .center(
                            firstMemory?.coordinates?.let {
                                Point.fromLngLat(it.longitude, it.latitude)
                            } ?: Point.fromLngLat(20.0, 20.0)
                        )
                        .zoom(if (firstMemory == null) 2.4 else 5.5)
                        .build()
                    mapboxMap.setCamera(defaultCamera)
                }
            },
            update = { mapView ->
                val manager = annotationManager
                    ?: mapView.annotations.createPointAnnotationManager()
                        .also { annotationManager = it }

                if (!clickListenerAdded) {
                    manager.addClickListener { annotation ->
                        val memoryId = annotation.getData()?.asString
                        if (!memoryId.isNullOrBlank()) {
                            onMemoryClick(memoryId)
                        }
                        true
                    }
                    clickListenerAdded = true
                }
                manager.deleteAll()
                state.memories.forEach { memory ->
                    val bitmap = markerBitmaps[memory.id] ?: return@forEach
                    manager.create(
                        PointAnnotationOptions()
                            .withPoint(
                                Point.fromLngLat(
                                    memory.coordinates.longitude,
                                    memory.coordinates.latitude
                                )
                            )
                            .withIconImage(bitmap)
                            .withIconAnchor(IconAnchor.BOTTOM)
                            .withData(JsonPrimitive(memory.id))
                    )
                }
            }
        )

        FriendMapTopBar(
            title = state.profile?.username ?: stringResource(R.string.friend_map),
            onBack = onBack
        )
        if (state.isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center)
            )
        }
        if (!state.isLoading && state.memories.isEmpty()) {
            Text(
                text = stringResource(R.string.no_friend_memories),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .align(Alignment.Center)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun FriendMapTopBar(
    title: String,
    onBack: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 48.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .size(44.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(Modifier.width(12.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .padding(horizontal = 18.dp, vertical = 11.dp)
        )
    }
}