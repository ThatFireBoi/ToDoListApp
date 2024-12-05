# ToDoListApp

## Project Description
ToDoListApp is a simple Android app designed to help users manage their daily tasks. You can add tasks to a dynamically filled list to keep track of them.

## Screenshots
![Welcome](https://github.com/user-attachments/assets/d784d09d-c8f3-4b7b-9f50-cddb59862e12)
![app](https://github.com/user-attachments/assets/7b0972cb-2bea-4105-bc50-1c5d7983982e)

## Usage Instructions
1. Clone the repository:
   ```sh
   git clone https://github.com/ThatFireBoi/ToDoListApp.git

## Features Implemented
* Welcome Screen: A simple screen that welcomes the user and provides access to the list.
* Add To-Do Items: Users can add new to-do items using a text field and a button.
* Dynamic To-Do List: The list of to-do items updates dynamically as new items are added.
* Animations: To-do items appear with an animation when added to the list.

## Challenges and Solutions
* One of the bigget challenge was dinamically adding items to the list and animating them as they were added. After searching I was able to solve this using rememberSaveable and AnimatedVisibility.

## Future Enhancements
* Add the ability to mark the tasks as completed and remove them from the list
* Add the ability to delete items from the list
* Allow extra text fields to add specific details to the tasks

## Reflection

Building this list wa a challenging but excellent learning experience. It brought me closer to fully understanding Jetpack Compose and see its potential when creating Android apps. Due to the limited amount of time
given to learning these topics, especially for someone such a myself, who is relatively new to the coding world, the app was scoped to be very simple and be all encompassed inside the MainActivity.kt file, using Composable 
to create multiple screens. The hardest part was figuring out how to dinamically add items to the list and how to animate them as they're added. Research and the Jetpack Compose basics website lead me to rememberSaveable and AnimatedVisibility, which helped with state management and animations respectively. Through this I learned more about dynamic UIs. I also had issues with the device's keyboard not showing up and instead a bar coming up, but research leads me to believe it varies from phone to phone.

I believe that this served as a good stepping stone to more advanced projects in the future and how to progress with creating more advanced Android tasks. I have a better understanding of JetPack now and can now more reliably use it for future assignments. This was a crucial step in this Android/Kotlin experience and I'm excited to see what comes after this. After all is said and done, I see myself continuing to develop my skills in Kotlin.
