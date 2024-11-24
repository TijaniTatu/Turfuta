package com.example.turfuta.screens

import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavHostController

@Composable
fun BottomNavigationBar(
    pagerState: PagerState,
    onPageSelected: (Int) -> Unit
) {
    NavigationBar(
        containerColor = Color(0xFF1B5E20) // Dark Green for the background
    ) {
        val items = listOf("Home", "Search", "History", "Profile")
        items.forEachIndexed { index, label ->
            val isSelected = pagerState.currentPage == index
            val selectedColor = Color(0xFF81C784) // Light Green for selected item
            val unselectedColor = Color(0xFF388E3C) // A lighter green for unselected items

            NavigationBarItem(
                selected = isSelected,
                onClick = { onPageSelected(index) },
                icon = {
                    when (index) {
                        0 -> Icon(Icons.Default.Home, contentDescription = "Home", tint = if (isSelected) selectedColor else unselectedColor)
                        1 -> Icon(Icons.Default.Search, contentDescription = "Search", tint = if (isSelected) selectedColor else unselectedColor)
                        2 -> Icon(Icons.Default.DateRange, contentDescription = "History", tint = if (isSelected) selectedColor else unselectedColor)
                        3 -> Icon(Icons.Default.Person, contentDescription = "Profile", tint = if (isSelected) selectedColor else unselectedColor)
                    }
                },
                label = {
                    Text(
                        label,
                        color = if (isSelected) selectedColor else unselectedColor
                    )
                }
            )
        }
    }
}

