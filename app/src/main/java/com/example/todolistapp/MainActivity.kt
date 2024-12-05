package com.example.todolistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.ui.layout.ContentScale
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.todolistapp.ui.theme.ToDoListAppTheme

class MainActivity : ComponentActivity() {

    // Create a TaskViewModel instance by using viewModels
    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListAppTheme {
                MyApp(taskViewModel)
            }
        }
    }
}

@Composable
fun MyApp(taskViewModel: TaskViewModel) {

    // Variables that manage the visibility of the WelcomeScreen, EditUserScreen and TaskDetailsScreen
    val showWelcomeScreen = rememberSaveable { mutableStateOf(true) }
    val showEditUserScreen = rememberSaveable { mutableStateOf(false) }
    val showTaskDetailsScreen = rememberSaveable { mutableStateOf<Pair<Boolean, Int>?>(null) }

    Surface(color = MaterialTheme.colorScheme.background) {
        when {
            // Show TaskDetailsScreen if true
            showTaskDetailsScreen.value?.first == true -> {
                TaskDetailsScreen(taskViewModel, showTaskDetailsScreen.value!!.second) {
                    showTaskDetailsScreen.value = null
                }
            }
            // Show EditUserScreen if true
            showEditUserScreen.value -> {
                EditUserScreen(taskViewModel) {
                    showEditUserScreen.value = false
                }
            }
            // Show WelcomeScreen if true
            showWelcomeScreen.value -> {
                WelcomeScreen(
                    taskViewModel = taskViewModel,
                    onContinueClicked = { showWelcomeScreen.value = false },
                    onEditUserClicked = { showEditUserScreen.value = true }
                )
            }
            // Show ToDoApp if false
            else -> {
                ToDoApp(taskViewModel) { taskIndex ->
                    showTaskDetailsScreen.value = Pair(true, taskIndex)
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    taskViewModel: TaskViewModel,
    onContinueClicked: () -> Unit,
    onEditUserClicked: () -> Unit
) {
    // Collect the user data from the TaskViewModel
    val user = taskViewModel.user.collectAsState().value

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.background_image),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_todo_list),
                contentDescription = stringResource(id = R.string.todo_list_icon_description),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text("Welcome to your To-Do App!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onContinueClicked) {
                Text("Continue")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onEditUserClicked) {
                Text("Edit User Info")
            }
        }
        Box(
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.TopStart)
        ) {
            // Display the user information in a Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondary),
                elevation = CardDefaults.cardElevation(5.dp)
            ) {
                Column(
                    modifier = Modifier.padding(17.dp)
                ) {
                    Text(
                        text = "Name: ${user.name}",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Email: ${user.email}",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Composable
fun ToDoApp(taskViewModel: TaskViewModel, onTaskClicked: (Int) -> Unit) {
    // Observe the list of tasks from the TaskViewModel
    val tasks by taskViewModel.tasks.collectAsState()
    var newItem by rememberSaveable { mutableStateOf("") }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.todo_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("To-Do App", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                TextField(
                    value = newItem,
                    onValueChange = { newItem = it },
                    label = { Text("New To-Do Item") },
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            if (newItem.isNotBlank()) {
                                taskViewModel.addTask(newItem)
                                newItem = ""
                            }
                        }
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (newItem.isNotBlank()) {
                        taskViewModel.addTask(newItem)
                        newItem = ""
                    }
                }) {
                    Text("Add")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            LazyColumn {
                itemsIndexed(tasks) { index, task ->
                    ToDoItem(index, task, onTaskClicked, taskViewModel::deleteTask)
                }
            }
        }
    }
}

@Composable
fun EditUserScreen(taskViewModel: TaskViewModel, onSaveClicked: () -> Unit) {
    val user = taskViewModel.user.collectAsState().value
    // Variables to hold the name and email input values
    var name by rememberSaveable { mutableStateOf(user.name) }
    var email by rememberSaveable { mutableStateOf(user.email) }

    // Layout for the Edit User screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TextField for editing the user's name
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        // TextField for editing the user's email
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Button to save the updated user information
        Button(onClick = {
            taskViewModel.updateUser(name, email)
            onSaveClicked()
        }) {
            Text("Save")
        }
    }
}

@Composable
fun TaskDetailsScreen(taskViewModel: TaskViewModel, taskIndex: Int, onSaveClicked: () -> Unit) {
    // Get the task details from the TaskViewModel
    val task = taskViewModel.tasks.collectAsState().value[taskIndex]
    // State variable to hold the task details
    var details by rememberSaveable { mutableStateOf(task.details) }

    // Layout for the Task Details screen
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Display the task title
        Text("Task: ${task.title}", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        TextField(
            value = details,
            onValueChange = { details = it },
            label = { Text("Details") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        // Button to save the updated task details
        Button(onClick = {
            taskViewModel.updateTaskDetails(taskIndex, details)
            onSaveClicked()
        }) {
            Text("Save")
        }
    }
}

@Composable
fun ToDoItem(
    index: Int,
    task: Task,
    onTaskClicked: (Int) -> Unit,
    onDeleteTask: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    // Set the background color of the card based on the index
    val backgroundColor =
        if (index % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    var isVisible by remember { mutableStateOf(false) }

    // Set the visibility of the card to true when the task is launched
    LaunchedEffect(task) {
        isVisible = true
    }

    // Animate the visibility of the card
    AnimatedVisibility(
        visible = isVisible,
        enter = scaleIn(
            animationSpec = tween(
                durationMillis = 250,
                easing = { it * it * it }
            )
        ),
        exit = scaleOut(
            animationSpec = tween(
                durationMillis = 250,
                easing = { it * it * it }
            )
        )
    ) {
        // Card to display the task title
        Card(
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            modifier = modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .fillMaxWidth()
                .clickable { onTaskClicked(index) },
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "${index + 1}. ${task.title}",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onDeleteTask(index) }) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_delete),
                        contentDescription = "Delete"
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    ToDoListAppTheme {
        MyApp(TaskViewModel())
    }
}