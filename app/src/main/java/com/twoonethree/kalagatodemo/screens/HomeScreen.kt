package com.twoonethree.kalagatodemo.screens

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.twoonethree.kalagatodemo.R
import com.twoonethree.kalagatodemo.SortType
import com.twoonethree.kalagatodemo.TaskViewModel
import com.twoonethree.kalagatodemo.navsetup.ScreenName
import com.twoonethree.kalagatodemo.room.Priority
import com.twoonethree.kalagatodemo.room.Task
import com.twoonethree.kalagatodemo.utils.toFormattedString
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController, vm: TaskViewModel) {

    val snackbarHostState = remember { SnackbarHostState() }

    val onSettingClick = remember(navController) { { navController.navigate(ScreenName.SettingScreen) } }

    val onEditClick = remember(navController) {
        { taskId: Int -> navController.navigate(ScreenName.TaskViewScreen(taskId)) }
    }

    val onDelete = remember(vm) {
        {task:Task -> vm.deleteWithUndo(task)}
    }
    val onUpdate = remember(vm) {
        {task:Task, isCompleted:Boolean -> vm.markAsCompleteWithUndo(task, isCompleted)}
    }

    val deleteUndo = remember(vm) {
        {task:Task -> vm.deleteUndo(task)}
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatBtn(onclick = {navController.navigate(ScreenName.TaskAddScreen)})
        },
        topBar = {
            TopAppBar(
                title = { Text("Tasks") },
                actions = {
                    HomeAppBar(
                        onSortSelected = { vm.setSortType(it) },
                        onFilterSelected = { vm.setShowCompleted(it) },
                        onSettingClick = onSettingClick,
                    )
                },
            )
        }
    ) { padding ->
        MainBody(padding,
            vm.tasks,
            onEditClick,
            snackbarHostState,
            ondelete = onDelete,
            onUpadte = onUpdate,
            deleteUndo
        )
    }
}


@Composable
fun HomeAppBar(
    onSortSelected: (SortType) -> Unit,
    onFilterSelected: (Boolean) -> Unit,
    onSettingClick: () -> Unit,
) {
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }

    Box {
        Row {
            IconButton(onClick = { showSortMenu = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_sort),
                    contentDescription = "Sort",
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(onClick = { showFilterMenu = true }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_filter),
                    contentDescription = "Filter",
                    modifier = Modifier.size(32.dp)
                )
            }

            IconButton(onClick = { onSettingClick() }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = "Settings",
                    modifier = Modifier.size(32.dp)
                )
            }
        }

        DropdownMenu(
            expanded = showSortMenu,
            onDismissRequest = { showSortMenu = false }
        ) {
            SortType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.name) },
                    onClick = {
                        onSortSelected(type)
                        showSortMenu = false
                    }
                )
            }
        }

        DropdownMenu(
            expanded = showFilterMenu,
            onDismissRequest = { showFilterMenu = false }
        ) {
            DropdownMenuItem(
                text = { Text("All Tasks") },
                onClick = {
                    onFilterSelected(false)
                    showFilterMenu = false
                }
            )
            DropdownMenuItem(
                text = { Text("Completed Only") },
                onClick = {
                    onFilterSelected(true)
                    showFilterMenu = false
                }
            )
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onEditClick: (Int) -> Unit,
    onDelete: (Task) -> Unit,
    onUpdate: (Task, Boolean) -> Unit,
    deleteUndo: (Task) -> Unit,
    snackbarHostState: SnackbarHostState
) {
    val scope = rememberCoroutineScope()
    val dismissState = rememberSwipeToDismissBoxState()

    SwipeToDismissBox(
        state = dismissState,
        backgroundContent = {
            when (dismissState.dismissDirection) {
                SwipeToDismissBoxValue.StartToEnd -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .background(Color.Green),
                        contentAlignment = Alignment.CenterStart
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Complete", tint = Color.White)
                    }
                }

                SwipeToDismissBoxValue.EndToStart -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                            .background(Color.Red),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.White)
                    }
                }

                else -> {}
            }
        },
        content = {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = when (task.priority) {
                        Priority.HIGH -> Color(0xFFFFCDD2)
                        Priority.MEDIUM -> Color(0xFFFFE0B2)
                        Priority.LOW -> Color(0xFFC8E6C9)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = task.title,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(onClick = { onEditClick(task.id) }) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Task")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Due: ${task.dueDate.toFormattedString()}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )

                        Text(
                            text = if (task.isCompleted) "Completed" else "Pending",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    )

    LaunchedEffect(dismissState.currentValue) {
        when (dismissState.currentValue) {
            SwipeToDismissBoxValue.StartToEnd -> {
                onUpdate(task, true)
                scope.launch { dismissState.reset() }
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Task marked as completed",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onUpdate(task, false) // Revert completion
                    }
                }
            }

            SwipeToDismissBoxValue.EndToStart -> {
                onDelete(task)
                scope.launch { dismissState.reset() }
                scope.launch {
                    val result = snackbarHostState.showSnackbar(
                        message = "Task deleted",
                        actionLabel = "Undo",
                        duration = SnackbarDuration.Short
                    )
                    if(result == SnackbarResult.ActionPerformed) {
                        deleteUndo(task)
                    }
                }

            }

            else -> {}
        }
    }
}


@Composable
fun FloatBtn(onclick: () -> Unit)
{
    FloatingActionButton(
        onClick = { onclick() },
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary
    ) {
        Icon(Icons.Filled.Add, contentDescription = "Add Task")
    }
}


@Composable
fun MainBody(
    padding: PaddingValues,
    tasks: List<Task>,
    onEditClick: (Int) -> Unit,
    snackbarHostState: SnackbarHostState,
    ondelete: (Task) -> Unit,
    onUpadte: (Task, Boolean) -> Unit,
    deleteUndo: (Task) -> Unit
) {
    var isLoading by remember { mutableStateOf(true) }

    // Simulate a delay for data fetching
    LaunchedEffect(Unit) {
        delay(500) // Simulated delay for loading effect
        isLoading = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            LazyColumn {
                items(5) { ShimmerTaskItem() } // Show shimmer placeholders
            }
        } else {
            if (tasks.isEmpty()) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.img_no_task),
                        contentDescription = "No Tasks",
                        modifier = Modifier.size(350.dp),
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No tasks available",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Gray
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(tasks) { task ->
                        TaskItem(task = task, onEditClick = onEditClick, onDelete = ondelete, onUpdate = onUpadte, deleteUndo, snackbarHostState = snackbarHostState)
                    }
                }
            }
        }
    }
}

@Composable
fun ShimmerTaskItem() {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.3f),
        Color.LightGray.copy(alpha = 0.6f)
    )

    val transition = rememberInfiniteTransition()
    val shimmerAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = shimmerAnim, y = 0f),
        end = Offset(x = shimmerAnim + 300f, y = 0f)
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth(0.4f)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(brush)
            )
        }
    }
}

