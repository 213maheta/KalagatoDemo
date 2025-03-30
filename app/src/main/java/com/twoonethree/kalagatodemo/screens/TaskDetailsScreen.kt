package com.twoonethree.kalagatodemo.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.twoonethree.kalagatodemo.TaskViewModel
import com.twoonethree.kalagatodemo.room.Task
import com.twoonethree.kalagatodemo.utils.calculateProgress
import com.twoonethree.kalagatodemo.utils.getRemainingHours
import com.twoonethree.kalagatodemo.utils.toFormattedString
import org.koin.androidx.compose.koinViewModel



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    navController: NavController,
    vm: TaskViewModel = koinViewModel(),
    taskId: Int
) {
    var task by remember { mutableStateOf<Task?>(null) }
    var progress by remember { mutableStateOf(0f) }
    var showDeleteDialog by remember { mutableStateOf(false) } // State for delete dialog

    LaunchedEffect(taskId) {
        task = vm.getTaskById(taskId)
        task?.let {
            progress = calculateProgress(it.dueDate.time)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Task Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        task?.let { taskData ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = taskData.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp),
                    fontWeight = FontWeight.Bold
                )

                CircularProgressBar(progress)

                TaskTextDetails(taskData)

                Spacer(modifier = Modifier.height(24.dp))

                ActionButtons(
                    onDelete = { showDeleteDialog = true },
                    onComplete = {
                        vm.updateTask(taskData.copy(isCompleted = true))
                        navController.popBackStack()
                    },
                    !taskData.isCompleted
                )

                // Show Delete Confirmation Dialog if triggered
                if (showDeleteDialog) {
                    DeleteConfirmationDialog(
                        onConfirm = {
                            vm.deleteTask(taskData)
                            navController.popBackStack()
                        },
                        onDismiss = { showDeleteDialog = false }
                    )
                }
            }
        } ?: run {
            TaskNotFound(paddingValues)
        }
    }
}

/**
 * Delete Confirmation Dialog
 */
@Composable
fun DeleteConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Task") },
        text = { Text("Are you sure you want to delete this task? This action cannot be undone.") },
        confirmButton = {
            TextButton(onClick = {
                onConfirm()
                onDismiss()
            }) {
                Text("Delete", color = Color.Red)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

/**
 * Action Buttons with Delete Confirmation
 */
@Composable
fun ActionButtons(onDelete: () -> Unit, onComplete: () -> Unit, isCompleted: Boolean) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedButton(
            onClick = onDelete,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
        ) {
            Text("Delete Task")
        }

        if (isCompleted) {
            Button(
                onClick = onComplete,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("Mark as Completed")
            }
        }
    }
}



@Composable
fun TaskNotFound(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Task not found",
            style = MaterialTheme.typography.bodyLarge.copy(fontSize = 20.sp, color = Color.Gray)
        )
    }
}


@Composable
fun CircularProgressBar(progress: Float) {
    Box(contentAlignment = Alignment.Center) {
        CircularProgressIndicator(
            progress = { progress },
            color = if (progress < 0.5f) Color.Red else MaterialTheme.colorScheme.primary,
            strokeWidth = 8.dp,
            trackColor = ProgressIndicatorDefaults.circularIndeterminateTrackColor,
            modifier = Modifier.size(100.dp)
        )
        Text("${(progress * 100).toInt()}%", fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}


@Composable
fun TaskTextDetails(taskData: Task) {
    Text(
        "Due in: ${getRemainingHours(taskData.dueDate.time)} hours",
        color = MaterialTheme.colorScheme.secondary
    )

    Text(
        "Priority: ${taskData.priority.name}",
        color = MaterialTheme.colorScheme.tertiary
    )

    Text(
        "Due Date: ${taskData.dueDate.toFormattedString()}",
        color = MaterialTheme.colorScheme.secondary
    )
}



