package com.example.todolistapp


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ToDoListAppTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun MyApp(
    modifier: Modifier = Modifier
) {
    // Controls the visibility of the welcome screen
    var showWelcomeScreen by rememberSaveable { mutableStateOf(true) }

    Surface(modifier, color = MaterialTheme.colorScheme.background) {
        if (showWelcomeScreen) {
            WelcomeScreen(onContinueClicked = { showWelcomeScreen = false })
        } else {
            ToDoApp()
        }
    }
}

@Composable
fun WelcomeScreen(onContinueClicked: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // Loads background image for the welcome screen
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
                // To-do icon for the welcome screen
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
        }
    }
}

@Composable
fun ToDoApp() {
    // Hold the list of to-do items
    var toDoItems by rememberSaveable { mutableStateOf(listOf<String>()) }
    // Holds the new item text
    var newItem by rememberSaveable { mutableStateOf("") }
    // Controls the visibility of the items
    var itemVisibility by rememberSaveable { mutableStateOf(mapOf<String, Boolean>()) }

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
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("To-Do App", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(16.dp))
            Row {
                // Field where user enters new to-do items
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
                                toDoItems = toDoItems + newItem
                                itemVisibility = itemVisibility + (newItem to false)
                                newItem = ""
                            }
                        }
                    )
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    if (newItem.isNotBlank()) {
                        toDoItems = toDoItems + newItem
                        itemVisibility = itemVisibility + (newItem to false)
                        newItem = ""
                    }
                }) {
                    Text("Add")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            // List of to-do items
            ToDoList(
                items = toDoItems,
                itemVisibility = itemVisibility,
                onItemAdded = { item: String ->
                    itemVisibility = itemVisibility + (item to true)
                })
        }
    }
}

@Composable
fun ToDoList(items: List<String>, itemVisibility: Map<String, Boolean>, onItemAdded: (String) -> Unit, modifier: Modifier = Modifier) {
    // LazyColumn to display the list of to-do items
    LazyColumn(modifier = modifier) {
        itemsIndexed(items) { index, item ->
            ToDoItem(index + 1, item, itemVisibility[item] ?: false, onItemAdded)
        }
    }
}

@Composable
fun ToDoItem(index: Int, item: String, visible: Boolean, onItemAdded: (String) -> Unit, modifier: Modifier = Modifier) {
    val backgroundColor = if (index % 2 == 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

    // When the item is added, call the onItemAdded callback
    LaunchedEffect(item) {
        onItemAdded(item)
    }

    // Animations for the items
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
                    // Determine the item's position in the list
                    text = "$index. ",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Text(
                    text = item,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MyAppPreview() {
    ToDoListAppTheme {
        MyApp(Modifier.fillMaxSize())
    }
}