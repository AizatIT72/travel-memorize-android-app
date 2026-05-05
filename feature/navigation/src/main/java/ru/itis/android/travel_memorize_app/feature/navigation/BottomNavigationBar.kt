package ru.itis.android.travel_memorize_app.feature.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import ru.itis.android.travel_memorize_app.core.ui.theme.LocalExtendedColors
import ru.itis.android.travel_memorize_app.ui.R

@Composable
fun BottomNavigationBar(navController: NavController) {
    val colors = LocalExtendedColors.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavItemData("map", R.drawable.ic_bottommap, R.string.nav_map),
        BottomNavItemData("feed", R.drawable.ic_bottom_feed, R.string.nav_feed),
        BottomNavItemData("friends", R.drawable.ic_botoom_friends, R.string.nav_friends),
        BottomNavItemData("profile", R.drawable.ic_bottom_profile, R.string.nav_profile)
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(88.dp)
            .background(
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.96f),
                shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)
            )
            .padding(horizontal = 24.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach { item ->
            val selected = currentRoute == item.route

            Column(
                modifier = Modifier
                    .width(68.dp)
                    .clickable(enabled = !selected) {
                        navController.navigate(item.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .background(
                            color = if (selected) Color(0xFFD1FAE5) else Color.Transparent,
                            shape = RoundedCornerShape(999.dp)
                        )
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = item.iconResId),
                        contentDescription = null,
                        tint = if (selected) colors.linkText else Color(0xFF78716C),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = stringResource(id = item.labelResId),
                    color = if (selected) colors.linkText else Color(0xFF78716C),
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}