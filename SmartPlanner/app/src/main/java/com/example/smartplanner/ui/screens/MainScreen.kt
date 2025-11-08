package com.example.smartplanner.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.smartplanner.R
import com.example.smartplanner.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: TaskViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Text(stringResource(R.string.nav_create)) },
                    label = { Text(stringResource(R.string.compose_plan)) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Text(stringResource(R.string.nav_list)) },
                    label = { Text(stringResource(R.string.nav_list)) }
                )
            }
        }
    ) { paddingValues ->
        when (selectedTab) {
            0 -> GeneratePlanScreen(viewModel = viewModel, modifier = Modifier.padding(paddingValues))
            1 -> PlanListScreen(viewModel = viewModel)
        }
    }
}
