package ru.itis.android.travel_memorize_app.navigation

import android.annotation.SuppressLint
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import ru.itis.android.travel_memorize_app.core.domain.model.map.GeoPoint
import ru.itis.android.travel_memorize_app.core.ui.theme.ThemeMode
import ru.itis.android.travel_memorize_app.feature.auth.ui.ForgotPasswordScreen
import ru.itis.android.travel_memorize_app.feature.auth.ui.OnboardingScreen
import ru.itis.android.travel_memorize_app.feature.auth.ui.SignInScreen
import ru.itis.android.travel_memorize_app.feature.auth.ui.SignUpScreen
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.ForgotPasswordViewModel
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.SignInViewModel
import ru.itis.android.travel_memorize_app.feature.auth.viewmodel.SignUpViewModel
import ru.itis.android.travel_memorize_app.feature.friends.ui.FriendsScreen
import ru.itis.android.travel_memorize_app.feature.memory.ui.AddMemoryScreen
import ru.itis.android.travel_memorize_app.feature.memory.ui.EditMemoryScreen
import ru.itis.android.travel_memorize_app.feature.memory.ui.MemoriesScreen
import ru.itis.android.travel_memorize_app.feature.memory.ui.MemoryDetailsScreen
import ru.itis.android.travel_memorize_app.feature.memory.ui.MemorySearchScreen
import ru.itis.android.travel_memorize_app.feature.navigation.BottomNavigationBar
import ru.itis.android.travel_memorize_app.feature.profile.ui.EditProfileScreen
import ru.itis.android.travel_memorize_app.feature.profile.ui.ProfileScreen
import ru.itis.android.travel_memorize_app.map.ui.MapScreen
import ru.itis.android.travel_memorize_app.map.ui.SelectPlaceScreen
import ru.itis.android.travel_memorize_app.core.ui.theme.AppLanguage
import ru.itis.android.travel_memorize_app.feature.friends.ui.AddFriendScreen
import ru.itis.android.travel_memorize_app.feature.friends.ui.FriendMapScreen
import ru.itis.android.travel_memorize_app.feature.friends.ui.FriendProfileScreen

object Routes {
    const val Onboarding = "onboarding"
    const val SignUp = "signup"
    const val SignIn = "signin"
    const val ForgotPassword = "forgot_password"
    const val Map = "map"
    const val Feed = "feed"
    const val Friends = "friends"
    const val Profile = "profile"

    const val AddMemory = "add_memory"

    const val SelectPlace = "select_place"

    const val MemoryDetails = "memory_details"

    const val MemorySearch = "memory_search"

    const val EditMemory = "edit_memory"

    const val AddMemoryRoute = "add_memory/{lat}/{lng}?placeName={placeName}&city={city}&country={country}"

    const val EditProfile = "edit_profile"
    const val AddFriend = "add_friend"
    const val FriendProfile = "friend_profile"
    const val FriendMap = "friend_map"




    fun addMemoryRoute(
        lat: Double,
        lng: Double,
        placeName: String?,
        city: String?,
        country: String?
    ): String {
        return "add_memory/$lat/$lng" +
                "?placeName=${android.net.Uri.encode(placeName.orEmpty())}" +
                "&city=${android.net.Uri.encode(city.orEmpty())}" +
                "&country=${android.net.Uri.encode(country.orEmpty())}"
    }

}

@SuppressLint("UnrememberedGetBackStackEntry")
@Composable
fun AppNavGraph(
    startDestination: String,
    viewModelFactory: ViewModelProvider.Factory,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    appLanguage: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.Onboarding) {
            OnboardingScreen(
                onGetStarted = {
                    navController.navigate(Routes.SignUp)
                },
                onNavigateToSignIn = {
                    navController.navigate(Routes.SignIn)
                }
            )
        }

        composable(Routes.SignUp) {
            val viewModel: SignUpViewModel = viewModel(factory = viewModelFactory)
            SignUpScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToSignIn = {
                    navController.navigate(Routes.SignIn) {
                        launchSingleTop = true
                        popUpTo(Routes.SignUp) {
                            inclusive = true
                        }
                    }
                },
                onSuccess = {
                    navController.navigate(Routes.Map) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.SignIn) {
            val viewModel: SignInViewModel = viewModel(factory = viewModelFactory)
            SignInScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onNavigateToSignUp = {
                    navController.navigate(Routes.SignUp) {
                        launchSingleTop = true
                        popUpTo(Routes.SignIn) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.ForgotPassword) {
                        launchSingleTop = true
                    }
                },
                onSuccess = {
                    navController.navigate(Routes.Map) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.ForgotPassword) {
            val viewModel: ForgotPasswordViewModel = viewModel(factory = viewModelFactory)
            ForgotPasswordScreen(
                viewModel = viewModel,
                onBack = {
                    navController.popBackStack()
                },
                onBackToSignIn = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.Map) {
            val backStackEntry = navController.currentBackStackEntry

            val memoryChanged by backStackEntry
                ?.savedStateHandle
                ?.getStateFlow("memory_changed", false)
                ?.collectAsState()
                ?: remember { mutableStateOf(false) }

            Scaffold(
                bottomBar = { BottomNavigationBar(navController = navController) }
            ) { padding ->
                MapScreen(
                    paddingValues = padding,
                    viewModelFactory = viewModelFactory,
                    refreshTrigger = memoryChanged,
                    onAddClick = { navController.navigate(Routes.SelectPlace) },
                    onSearchClick = { navController.navigate(Routes.MemorySearch) },
                    onMemoryClick = { memoryId ->
                        navController.navigate("${Routes.MemoryDetails}/$memoryId")
                    }
                )
            }

            LaunchedEffect(memoryChanged) {
                if (memoryChanged) {
                    backStackEntry?.savedStateHandle?.set("memory_changed", false)
                }
            }
        }
        composable(Routes.MemorySearch) {
            MemorySearchScreen(
                viewModelFactory = viewModelFactory,
                onBack = { navController.popBackStack() },
                onMemoryClick = { memoryId ->
                    navController.navigate("${Routes.MemoryDetails}/$memoryId?readonly=false")
                }
            )
        }


        composable(Routes.SelectPlace) {
            SelectPlaceScreen(
                viewModelFactory = viewModelFactory,
                onBack = { navController.popBackStack() },
                onPlaceSelected = { selectedPoint ->
                    navController.navigate(
                        Routes.addMemoryRoute(
                            lat = selectedPoint.coordinates.latitude,
                            lng = selectedPoint.coordinates.longitude,
                            placeName = selectedPoint.placeName,
                            city = selectedPoint.city,
                            country = selectedPoint.country
                        )
                    )
                }
            )
        }

        composable(
            route = Routes.AddMemoryRoute,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType },
                navArgument("lng") { type = NavType.StringType },
                navArgument("placeName") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("city") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("country") {
                    type = NavType.StringType
                    defaultValue = ""
                }
            )
        ) { entry ->
            val lat = entry.arguments?.getString("lat")?.toDoubleOrNull()
            val lng = entry.arguments?.getString("lng")?.toDoubleOrNull()

            if (lat == null || lng == null) {
                LaunchedEffect(Unit) {
                    navController.popBackStack(Routes.Map, inclusive = false)
                }
                return@composable
            }

            AddMemoryScreen(
                viewModelFactory = viewModelFactory,
                coordinates = GeoPoint(lat, lng),
                placeName = entry.arguments?.getString("placeName")?.takeIf { it.isNotBlank() },
                city = entry.arguments?.getString("city")?.takeIf { it.isNotBlank() },
                country = entry.arguments?.getString("country")?.takeIf { it.isNotBlank() },
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.getBackStackEntry(Routes.Map)
                        .savedStateHandle["memory_changed"] = true

                    navController.popBackStack(Routes.Map, inclusive = false)
                }
            )
        }

        composable(
            route = "${Routes.MemoryDetails}/{memoryId}?readonly={readonly}",
            arguments = listOf(
                navArgument("memoryId") { type = NavType.StringType },
                navArgument("readonly") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { entry ->
            val memoryId = entry.arguments?.getString("memoryId").orEmpty()
            val readonly = entry.arguments?.getBoolean("readonly") ?: false

            val onEditClick: ((String) -> Unit)? =
                if (readonly) null
                else { id -> navController.navigate("${Routes.EditMemory}/$id") }

            val onDeletedCallback: (() -> Unit)? =
                if (readonly) null
                else {
                    {
                        navController.getBackStackEntry(Routes.Map)
                            .savedStateHandle["memory_changed"] = true

                        navController.popBackStack(Routes.Map, inclusive = false)
                    }
                }

            MemoryDetailsScreen(
                viewModelFactory = viewModelFactory,
                memoryId = memoryId,
                onBack = { navController.popBackStack() },
                onEditClick = onEditClick,
                onDeleted = onDeletedCallback,
                showOwnerActions = !readonly
            )
        }

        composable(
            route = "${Routes.EditMemory}/{memoryId}",
            arguments = listOf(navArgument("memoryId") { type = NavType.StringType })
        ) { entry ->
            val memoryId = entry.arguments?.getString("memoryId").orEmpty()

            EditMemoryScreen(
                viewModelFactory = viewModelFactory,
                memoryId = memoryId,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.getBackStackEntry(Routes.Map)
                        .savedStateHandle["memory_changed"] = true

                    navController.popBackStack()
                },
                onDeleted = {
                    navController.getBackStackEntry(Routes.Map)
                        .savedStateHandle["memory_changed"] = true

                    navController.popBackStack(Routes.Map, inclusive = false)
                }
            )
        }

        composable(Routes.Feed) {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
                MemoriesScreen(
                    viewModelFactory = viewModelFactory,
                    paddingValues = padding,
                    onAddClick = { navController.navigate(Routes.SelectPlace) },
                    onMemoryClick = { memoryId ->
                        navController.navigate("${Routes.MemoryDetails}/$memoryId?readonly=false")
                    }
                )
            }
        }

        composable(Routes.Friends) {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
                FriendsScreen(
                    viewModelFactory = viewModelFactory,
                    paddingValues = padding,
                    onAddFriendClick = {
                        navController.navigate(Routes.AddFriend)
                    },
                    onFriendClick = { friendId ->
                        navController.navigate("${Routes.FriendProfile}/$friendId")
                    }
                )
            }
        }

        composable(Routes.AddFriend) {
            AddFriendScreen(
                viewModelFactory = viewModelFactory,
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = "${Routes.FriendProfile}/{friendId}",
            arguments = listOf(navArgument("friendId") { type = NavType.StringType })
        ) { entry ->
            val friendId = entry.arguments?.getString("friendId").orEmpty()

            FriendProfileScreen(
                viewModelFactory = viewModelFactory,
                friendId = friendId,
                onBack = { navController.popBackStack() },
                onMemoryClick = { memoryId ->
                    navController.navigate("${Routes.MemoryDetails}/$memoryId?readonly=true")
                },
                onShowMapClick = { friendUid ->
                    navController.navigate("${Routes.FriendMap}/$friendUid")
                }
            )
        }

        composable(Routes.Profile) {
            Scaffold(bottomBar = { BottomNavigationBar(navController) }) { padding ->
                ProfileScreen(
                    viewModelFactory = viewModelFactory,
                    paddingValues = padding,
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange,
                    appLanguage = appLanguage,
                    onLanguageChange = onLanguageChange,
                    onEditProfile = {
                        navController.navigate(Routes.EditProfile)
                    },
                    onLogout = {
                        navController.navigate(Routes.SignIn) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
        composable(
            route = "${Routes.FriendMap}/{friendId}",
            arguments = listOf(navArgument("friendId") { type = NavType.StringType })
        ) { entry ->
            val friendId = entry.arguments?.getString("friendId").orEmpty()

            FriendMapScreen(
                viewModelFactory = viewModelFactory,
                friendId = friendId,
                onBack = { navController.popBackStack() },
                onMemoryClick = { memoryId ->
                    navController.navigate("${Routes.MemoryDetails}/$memoryId?readonly=true")
                }
            )
        }

        composable(Routes.EditProfile) {
            EditProfileScreen(
                viewModelFactory = viewModelFactory,
                onBack = { navController.popBackStack() },
                onSaved = {
                    navController.popBackStack()
                },
                onAccountDeleted = {
                    navController.navigate(Routes.Onboarding) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

    }
}