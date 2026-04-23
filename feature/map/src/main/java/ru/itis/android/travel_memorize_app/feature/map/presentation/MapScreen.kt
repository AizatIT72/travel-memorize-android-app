package ru.itis.android.travel_memorize_app.feature.map.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatListBulleted
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.GpsFixed
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Map
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.mapbox.common.MapboxOptions
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import com.mapbox.maps.plugin.viewport.data.FollowPuckViewportStateOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(viewModel: MapViewModel) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showSearchDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }

    SideEffect {
        MapboxOptions.accessToken = viewModel.getMapboxToken()
    }
    
    val viewportState = rememberMapViewportState {
        setCameraOptions {
            center(Point.fromLngLat(0.0, 0.0))
            zoom(1.0)
        }
    }

    LaunchedEffect(state.selectedLocation) {
        state.selectedLocation?.let { location ->
            viewportState.flyTo(
                cameraOptions = CameraOptions.Builder()
                    .center(Point.fromLngLat(location.longitude, location.latitude))
                    .zoom(14.0)
                    .build()
            )
        }
    }

    Scaffold(
        bottomBar = {
            Column {
                HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 0.dp
                ) {
                    NavigationBarItem(
                        selected = selectedTab == 0,
                        onClick = { selectedTab = 0 },
                        icon = { Icon(Icons.Default.Map, contentDescription = "Карта") },
                        label = { Text("Карта") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, indicatorColor = Color.Transparent)
                    )
                    NavigationBarItem(
                        selected = selectedTab == 1,
                        onClick = { selectedTab = 1 },
                        icon = { Icon(Icons.AutoMirrored.Filled.FormatListBulleted, contentDescription = "Список") },
                        label = { Text("Список") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, indicatorColor = Color.Transparent)
                    )
                    NavigationBarItem(
                        selected = selectedTab == 2,
                        onClick = { selectedTab = 2 },
                        icon = { Icon(Icons.Default.GroupAdd, contentDescription = "Группы") },
                        label = { Text("Группы") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, indicatorColor = Color.Transparent)
                    )
                    NavigationBarItem(
                        selected = selectedTab == 3,
                        onClick = { selectedTab = 3 },
                        icon = { Icon(Icons.Default.AccountCircle, contentDescription = "Профиль") },
                        label = { Text("Профиль") },
                        colors = NavigationBarItemDefaults.colors(selectedIconColor = Color.Black, indicatorColor = Color.Transparent)
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF5F7FA))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(Color.White)
            ) {
                MapboxMap(
                    modifier = Modifier.fillMaxSize(),
                    mapViewportState = viewportState
                ) {
                    state.placeMarks.forEach { mark ->
                        CircleAnnotation(
                            point = Point.fromLngLat(mark.longitude, mark.latitude)
                        ) {
                            circleColor = Color(0xFFE91E63)
                            circleRadius = 10.0
                            circleStrokeColor = Color.White
                            circleStrokeWidth = 2.0
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(bottom = 24.dp, end = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    FloatingActionButton(
                        onClick = { 
                            viewportState.transitionToFollowPuckState(
                                followPuckViewportStateOptions = FollowPuckViewportStateOptions.Builder()
                                    .zoom(14.0)
                                    .build()
                            )
                        },
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.GpsFixed, contentDescription = "Мое местоположение")
                    }
                    FloatingActionButton(
                        onClick = { showSearchDialog = true },
                        containerColor = Color.White,
                        contentColor = Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Добавить место")
                    }
                }
            }

            if (state.error != null) {
                Snackbar(modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 80.dp)) {
                    Text(state.error!!)
                }
            }

            if (showSearchDialog) {
                AlertDialog(
                    onDismissRequest = { showSearchDialog = false },
                    title = { Text("Поиск места") },
                    text = {
                        TextField(
                            value = searchQuery,
                            onValueChange = { searchQuery = it },
                            placeholder = { Text("Например: Москва, Арбат") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    },
                    confirmButton = {
                        Button(onClick = {
                            viewModel.onSearchPlace(searchQuery)
                            showSearchDialog = false
                            searchQuery = ""
                        }) {
                            Text("Найти")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showSearchDialog = false }) {
                            Text("Отмена")
                        }
                    }
                )
            }
        }
    }
}
