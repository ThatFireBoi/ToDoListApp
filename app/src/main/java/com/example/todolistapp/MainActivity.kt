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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import com.example.todolistapp.ui.theme.ToDoListAppTheme

class MainActivity : ComponentActivity() {

    // Create a TaskViewModel instance
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

    // Variables that manage the visibility of the WelcomeScreen and EditUserScreen
    val showWelcomeScreen = rememberSaveable { mutableStateOf(true) }
    val showEditUserScreen = rememberSaveable { mutableStateOf(false) }

    Surface(color = MaterialTheme.colorScheme.background) {
        when {
            // Show UserScreen if true
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
                ToDoApp(taskViewModel)
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
            Icon(
                painter = painterResource(id = R.drawable.ic_todo_list),
                contentDescription = stringResource(id = R.string.todo_list_icon_description),
                modifier = Modifier.size(64.dp)
            )
            // Using MaterialTheme to ajust the text, buttons and spacing
            Spacer(modifier = Modifier.height(16.dp))
            Text("Welcome to your To-Do App!", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Name: ${user.name}", style = MaterialTheme.typography.bodyLarge)
            Text("Email: ${user.email}", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onContinueClicked) {
                Text("Continue")
            }
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onEditUserClicked) {
                Text("Edit User Info")
            }
        }
    }
}

@Composable
fun ToDoApp(taskViewModel: TaskViewModel) {
    // Collect the toDoItems from the TaskViewModel
    val toDoItems by taskViewModel.toDoItems.collectAsState()
    var newItem by rememberSaveable { mutableStateOf("") }
    var itemVisibility by rememberSaveable { mutableStateOf(mapOf<String, Boolean>()) }

    // Function to delete an item from the list
    fun deleteItem(item: String) {
        taskViewModel.deleteItem(item)
        itemVisibility = itemVisibility - item
    }

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
                                taskViewModel.addItem(newItem)
                                itemVisibility = itemVisibility + (newItem to false)
                                newItem = ""
                            }
                        }
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (newItem.isNotBlank()) {
                        taskViewModel.addItem(newItem)
                        itemVisibility = itemVisibility + (newItem to false)
                        newItem = ""
                    }
                }) {
                    Text("Add")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            ToDoList(
                items = toDoItems,
                itemVisibility = itemVisibility,
                onItemAdded = { item: String ->
                    itemVisibility = itemVisibility + (item to true)
                },
                onDeleteItem = { item: String ->
                    deleteItem(item)
                }
            )
        }
    }
}

@Composable
fun EditUserScreen(taskViewModel: TaskViewModel, onSaveClicked: () -> Unit) {
    val user = taskViewModel.user.collectAsState().value
    var name by rememberSaveable { mutableStateOf(user.name) }
    var email by rememberSaveable { mutableStateOf(user.email) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Name") }
        )
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") }
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = {
            taskViewModel.updateUser(name, email)
            onSaveClicked()
        }) {
            Text("Save")
        }
    }
}

@Composable
fun ToDoList(
    items: List<String>,
    itemVisibility: Map<String, Boolean>,
    onItemAdded: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        itemsIndexed(items) { index, item ->
            ToDoItem(index + 1, item, itemVisibility[item] ?: false, onItemAdded, onDeleteItem)
        }
    }
}

@Composable
fun ToDoItem(
    index: Int,
    item: String,
    visible: Boolean,
    onItemAdded: (String) -> Unit,
    onDeleteItem: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor =
        if (index % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    LaunchedEffect(item) {
        onItemAdded(item)
    }

    AnimatedVisibility(
        visible = visible,
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
        Card(
            colors = CardDefaults.cardColors(containerColor = backgroundColor),
            modifier = modifier
                .padding(vertical = 4.dp, horizontal = 8.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Row(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "$index. ",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = { onDeleteItem(item) }) {
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