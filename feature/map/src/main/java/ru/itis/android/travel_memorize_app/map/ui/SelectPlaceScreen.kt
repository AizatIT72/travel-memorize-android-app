package ru.itis.android.travel_memorize_app.map.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.gestures.gestures
import ru.itis.android.travel_memorize_app.core.domain.model.map.GeoPoint
import ru.itis.android.travel_memorize_app.core.domain.model.map.MapMode
import ru.itis.android.travel_memorize_app.core.domain.model.map.SelectedMapPoint
import ru.itis.android.travel_memorize_app.feature.map.viewmodel.MapEffect
import ru.itis.android.travel_memorize_app.map.viewmodel.MapViewModel
import ru.itis.android.travel_memorize_app.ui.R

@Composable
fun SelectPlaceScreen(
    viewModelFactory: ViewModelProvider.Factory,
    onBack: () -> Unit,
    onPlaceSelected: (SelectedMapPoint) -> Unit
) {
    val viewModel: MapViewModel = viewModel(factory = viewModelFactory)
    val cameraState by viewModel.cameraState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var currentErrorResId by remember { mutableStateOf<Int?>(null) }
    LaunchedEffect(Unit) {
        viewModel.setMapMode(MapMode.SelectingPoint)
    }
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is MapEffect.NavigateToAddMemory -> onPlaceSelected(effect.selectedPoint)
                is MapEffect.ShowError -> currentErrorResId = effect.message.toMessageRes()
                is MapEffect.ShowSearchResults -> Unit
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
    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                MapView(
                    context,
                    MapInitOptions(
                        context = context,
                        styleUri = Style.STANDARD_SATELLITE
                    )
                ).apply {
                    val defaultCamera = CameraOptions.Builder()
                        .center(Point.fromLngLat(20.0, 20.0))
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
                    gestures.addOnMapLongClickListener { point ->
                        viewModel.selectPoint(
                            GeoPoint(
                                latitude = point.latitude(),
                                longitude = point.longitude()
                            )
                        )
                        true
                    }

                }
            }
        )

        IconButton(
            onClick = onBack,
            modifier = Modifier
                .padding(start = 24.dp, top = 48.dp)
                .size(44.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Text(
            text = stringResource(R.string.select_place),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 52.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .padding(horizontal = 24.dp, vertical = 12.dp),
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.bodyLarge
        )
        IconButton(
            onClick = { /* TODO геолокация */ },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(end = 24.dp, top = 48.dp)
                .size(44.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.MyLocation,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 40.dp, start = 24.dp, end = 24.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_touch),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )

            Spacer(modifier = Modifier.width(10.dp))

            Text(
                text = stringResource(R.string.long_press_to_select_place),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface
            )


        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}