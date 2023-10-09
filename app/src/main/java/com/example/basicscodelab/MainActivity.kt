package com.example.basicscodelab

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.basicscodelab.ui.theme.BasicsCodelabTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicsCodelabTheme {
                MyApp(Modifier.fillMaxSize())
            }
        }
    }
}

@Composable
fun MyApp(modifier: Modifier  = Modifier, names:List<String> = listOf("world", "compose")){
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.padding(4.dp)) {
            names.forEach {
                Greeting(it)
            }
        }
    }

}


//Coding state in the wrong way

/*
State in compose

Compose apps transform data into UI by calling composable functions.
If your data changes, compose re executes these functions with new data, creating an updated UI.
This is called recomposition.

Compose also looks at what data is needed by an individual composable so that it only needs to
 recompose components whose data has changed and skip recomposing those that are not affected.


Composable functions can execute frequently and in any order, you must not rely on thee ordering in
 which the code is exectued, or on how many times this function will be recomposed.

Here we don't have an state so this won't work as expected

Next approach can be to use a new API State/MutableSate
Whenever the data changes it will trigger updates to the UI to recompose the UI
but... -> declaration within a composable function will recreate with initial value everytime it is recomposed

Solution:
Use an API wrapper called Remember that will holds and remember the State variable, so that there are two possible scenarios:
 1.- first time -  creation : it will create the variable and assign the value to the reference
 2- subsequent times - It will look for any value stored and will assign this value to the reference
*/


@Composable

fun Greeting(name: String) {
    var expanded: MutableState<Boolean> = remember{ mutableStateOf(false) }
    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        Row {
            Column(modifier = Modifier.padding(24.dp).weight(1f)) {
                Text(text = "Hello,")
                Text(text = name)
            }
            ElevatedButton(onClick = { expanded.value = !expanded.value }) {
                Text(text = if (expanded.value) "Show less" else "Show more")
                /*
                The reason why mutating this variable does not trigger recompositions is that it's not being tracked by
                compose. Also, each time greeting is called the variable will reset to false
                 */
            }
        }

    }
}

@Preview(showBackground = true, name = "Text preview", widthDp = 320)
@Composable
fun GreetingPreview() {
    BasicsCodelabTheme {
        MyApp()
    }
}