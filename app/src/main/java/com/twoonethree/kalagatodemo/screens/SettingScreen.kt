package com.twoonethree.kalagatodemo.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.twoonethree.kalagatodemo.LocalDataStore


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingScreen(
    navController: NavController,
    isDarkTheme: MutableState<Boolean>,
    primaryColor: MutableState<Long>,
    localDataStore: LocalDataStore
) {
    val colorOptions = listOf(
        0xFFFF8C00, // Orange
        0xFF6200EE, // Purple
        0xFF03DAC6, // Teal
        0xFFE91E63, // Pink
        0xFF3F51B5  // Indigo
    )

    var selectedColor by remember {
        mutableStateOf(primaryColor.value) // Default to first color
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Dark Theme", fontSize = 18.sp)
                Switch(
                    checked = isDarkTheme.value,
                    onCheckedChange = { isChecked ->
                        isDarkTheme.value = isChecked
                        localDataStore.save(LocalDataStore.IS_DARK_THEME, isChecked)
                    }
                )
            }

            // Color Selection
            Text("App Color", fontSize = 18.sp)
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                colorOptions.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(color), CircleShape)
                            .border(
                                width = if (color == selectedColor) 3.dp else 0.dp,
                                color = MaterialTheme.colorScheme.onSurface,
                                shape = CircleShape
                            )
                            .clickable {
                                selectedColor = color
                                primaryColor.value = color
                                localDataStore.save(LocalDataStore.PRIMARY_COLOR, color)
                            }
                    )
                }
            }
        }
    }
}

