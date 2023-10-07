package main

import BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ui.NewsScreen
import ui.Personal
import ui.route.Route


@Composable
fun MainScreen(
    route : SnapshotStateList<Route>
){
    BackHandler(true){
        route.remove(route.last())
    }
    Scaffold(
        drawerContent = {
            Personal(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(all = 10.dp)
            )
        },
        content = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                Surface (
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ){
                    NewsScreen()
                }
                var selectedItem by remember { mutableStateOf(0) }
                val items = listOf("Songs", "Artists", "Playlists")
                BottomNavigation{
                    items.forEachIndexed { index, item ->
                        BottomNavigationItem(
                            icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                            label = { Text(item) },
                            selected = selectedItem == index,
                            onClick = { selectedItem = index }
                        )
                    }
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
    )
}

