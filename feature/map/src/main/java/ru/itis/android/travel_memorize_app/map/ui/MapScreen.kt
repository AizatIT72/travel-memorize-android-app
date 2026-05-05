package ru.itis.android.travel_memorize_app.map.ui

import android.graphics.Bitmap
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.MyLocation
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
import ru.itis.android.travel_memorize_app.core.domain.model.map.MapMode
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors
import ru.itis.android.travel_memorize_app.feature.map.viewmodel.MapEffect
import ru.itis.android.travel_memorize_app.map.ui.components.MapMarkerBitmapFactory
import ru.itis.android.travel_memorize_app.map.ui.components.MapSearchButton
import ru.itis.android.travel_memorize_app.map.viewmodel.MapViewModel
import ru.itis.android.travel_memorize_app.ui.R

@Composable
fun MapScreen(
    viewModelFactory: ViewModelProvider.Factory,
    paddingValues: PaddingValues = PaddingValues(0.dp),
    onAddClick: () -> Unit,
    onSearchClick: () -> Unit,
    onMemoryClick: (String) -> Unit,
    refreshTrigger: Boolean = false
) {
    val viewModel: MapViewModel = viewModel(factory = viewModelFactory)
    val state by viewModel.state.collectAsState()
    val cameraState by viewModel.cameraState.collectAsState()

    val context = LocalContext.current
    val extendedColors = LocalExtendedColors.current
    val snackbarHostState = remember { SnackbarHostState() }

    var currentErrorResId by remember { mutableStateOf<Int?>(null) }

    var annotationManager by remember { mutableStateOf<PointAnnotationManager?>(null) }
    var clickListenerAdded by remember { mutableStateOf(false) }

    var markerBitmaps by remember { mutableStateOf<Map<String, Bitmap>>(emptyMap()) }

    val bottomPadding = paddingValues.calculateBottomPadding()

    val markerColor = MaterialTheme.colorScheme.primary.toArgb()
    val borderColor = MaterialTheme.colorScheme.surface.toArgb()
    val placeholderColor = extendedColors.inputMuted.copy(alpha = 0.45f).toArgb()

    LaunchedEffect(refreshTrigger) {
        if (refreshTrigger) {
            viewModel.refreshPlaceMarks()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.setMapMode(MapMode.Browsing)
    }
    LaunchedEffect(state.placeMarks, markerColor, borderColor, placeholderColor) {
        markerBitmaps = state.placeMarks.associate { mark ->
            mark.id to MapMarkerBitmapFactory.create(
                context = context,
                photoUrl = mark.photos.firstOrNull(),
                markerColor = markerColor,
                borderColor = borderColor,
                placeholderColor = placeholderColor
            )
        }
    }
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MapEffect.ShowError -> currentErrorResId = effect.message.toMessageRes()
                is MapEffect.ShowSearchResults -> Unit
                is MapEffect.NavigateToAddMemory -> Unit
            }
        }
    }
    currentErrorResId?.let { resId ->
        val message = stringResource(id = resId)
        LaunchedEffect(message) {
            snackbarHostState.showSnackbar(message)
            currentErrorResId = null
        }


    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            MapTopBar()
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = bottomPadding)
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
                            val defaultCamera = CameraOptions.Builder()
                                .center(
                                    state.placeMarks.firstOrNull()?.coordinates?.let {
                                        Point.fromLngLat(it.longitude, it.latitude)
                                    } ?: Point.fromLngLat(20.0, 20.0)
                                )
                                .zoom(2.4)
                                .build()
                            mapboxMap.setCamera(cameraState ?: defaultCamera)
                            mapboxMap.subscribeCameraChanged {
                                val camera = mapboxMap.cameraState
                                viewModel.updateCameraState(
                                    CameraOptions.Builder()
                                        .center(camera.center)
                                        .zoom(camera.zoom)
                                        .pitch(camera.pitch)
                                        .bearing(camera.bearing)
                                        .build()
                                )
                            }
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
                        state.placeMarks.forEach { mark ->
                            val bitmap = markerBitmaps[mark.id] ?: return@forEach
                            manager.create(
                                PointAnnotationOptions()
                                    .withPoint(
                                        Point.fromLngLat(
                                            mark.coordinates.longitude,
                                            mark.coordinates.latitude
                                        )
                                    )
                                    .withIconImage(bitmap)
                                    .withIconAnchor(IconAnchor.BOTTOM)
                                    .withData(JsonPrimitive(mark.id))
                            )
                        }



                    }
                )

                MapSearchButton(
                    onClick = onSearchClick,
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 14.dp, start = 30.dp, end = 30.dp)
                )
                IconButton(
                    onClick = { /* TODO геолокация */ },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 92.dp)
                        .size(48.dp)
                        .background(MaterialTheme.colorScheme.surface, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.MyLocation,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                FloatingActionButton(
                    onClick = onAddClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(end = 24.dp, bottom = 28.dp)
                        .size(56.dp),
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(

                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(R.string.button_add_memory)
                    )
                }
            }
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun MapTopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(72.dp)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {

        Text(

            text = stringResource(R.string.memories),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )
    }
}