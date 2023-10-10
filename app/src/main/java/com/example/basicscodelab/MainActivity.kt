package com.example.basicscodelab

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.basicscodelab.ui.theme.BasicsCodelabTheme
import kotlin.math.exp

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

/*
 State Hoisting
 In composable functions, the state that is read or modified by multiple functions should live in a common ancestor
 To hoist = to lift or elevate ( we are lifting the state to the common ancestor of multiple composable functions that are
    using the state

 Advantages:
 If not hoisting, and multiple composable functions use the same state then we would have to replicate the state
 this will make the UI prone to errors
     -State duplications
     -introduction of bugs
 -helps reusing composables
 - makes composables substantially easier to test

 SOURCE OF TRUTH belongs to whoever creates and controls the state
 MyAPP - ST as it creates and controls the state
 */
@Composable
fun MyApp(modifier: Modifier  = Modifier){
    var shouldShowOnboarding: Boolean by rememberSaveable { mutableStateOf(true) }
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background
    ) {
        if (shouldShowOnboarding){
            OnboardingScreen(onContinueClicked = { shouldShowOnboarding = false })
            /*
            How do we pass events up? By passing callbacks down.
            Callbacks are functions that are passed as arguments to other functions and
            get executed when the event occurs.
            By passing a function and not a state to OnboardingScreen we make composable :
                1. more reusable
                2- state protection : other composables cannot mutate it
            e.g. OnboardingScreenPreview sends an empty lambda function (it´s jsut a preview does not need
            behaviour)
            */
        }else{
            Greetings()
        }
    }

}

/*
 We can have multiple previews at the same time

Column attrs
    verticalArrangement: Arrangement - values top, bottom, center
    horizontalAlignment: Alignment - values start. end. centerHorizontally
    by reserved keyword: kotlin feature to apply delefate pattern, it unwraps the MutableState(boolean) into
    boolean it saves us from typing .value every time
    shouldShowOnboarding is set to false but still pending the data source
 */
@Composable
fun OnboardingScreen(modifier: Modifier = Modifier, onContinueClicked: () -> Unit){

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Text(text = "Welcome to the Basics Codelab!")
        Button(
            modifier = Modifier.padding(vertical = 24.dp),
            onClick = onContinueClicked
        ) {
            Text(text = "Continue")
        }
    }
}

/*
New use case: what if the list holds thousands of records, in view base we have list and recycler views
list weren't performant with huge number of items,

Problem:
    one might be tempted to go straight forward with the same approach but it is not performant
    Instead of
    listOf("world", "compose")
    what if we had
    List(1000){ "$it"]) even the emulator can freeze up

Solution: LazyColumn ( just like recyclerview) it renders only the visible items on screen,
allowing performance gains when rendering a big list

Note: LazyColumn and LazyRow are equivalent to RecyclerView in Android Views.


 */
@Composable
fun Greetings(modifier: Modifier  = Modifier,  names:List<String> = List(1000) { "$it" }) {
    LazyColumn(modifier = modifier.padding(4.dp)) {
        items( items = names){ name ->
            Greeting(name = name)
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
 which the code is executed, or on how many times this function will be recomposed.

Here we don't have an state so this won't work as expected

Next approach can be to use a new API State/MutableSate
Whenever the data changes it will trigger updates to the UI to recompose the UI
but... -> declaration within a composable function will recreate with initial value everytime it is recomposed

Solution:
Use an API wrapper called Remember that will holds and remember the State variable, so that there are two possible scenarios:
 1.- first time -  creation : it will create the variable and assign the value to the reference
 2- subsequent times - It will look for any value stored and will assign this value to the reference
*/

/*
Persisting State

1. variable boolean false does not trigger recomposition when it changes
2. MutableState,State can hold a variables an trigger recomposition when value changes but it resets
       the value in each recompositions
3. Remember function can hold MutableState that holds var, and it can remember latest value before recomposition
    happens, it survives recomposition however it won´t survive activity recreation ( conf. changes,
    process death)
4. RememberSaveable: Offers all the features of Remember functions and additionally it can survive
activity recreation ( configuration changes and process death)
 */
@Composable
fun Greeting(name: String) {
    var expanded by rememberSaveable{ mutableStateOf(false) }
    /*
    Animation API animattionDpAsState
    It is a high level API composable, it returns a state object whose value will continuously be updated
    by the animation until it finishes. It takes a Dp target value.
     */
    //animationSpec parameter that let us customize the animation
    val extraPadding by animateDpAsState(
        if (expanded) 48.dp else 0.dp,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "extraPaddingAnimation"
    )

    Surface(
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 4.dp, horizontal = 8.dp)) {
        Row(modifier = Modifier.padding(24.dp)) {
            Column(modifier = Modifier
                .weight(1f)
                .padding(bottom = extraPadding.coerceAtLeast(0.dp))) {
                // also making sure that padding is never negative, otherwise it could crash the app.
                Text(text = "Hello,")
                Text(text = name, style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold
                ))
            }
            ElevatedButton(onClick = { expanded = !expanded }) {
                Text(text = if (expanded) "Show less" else "Show more")
                /*
                The reason why mutating this variable does not trigger recompositions is that it's not being tracked by
                compose. Also, each time greeting is called the variable will reset to false
                 */
            }
        }

    }
}

@Preview
@Composable
fun MyAppPreview() {
    BasicsCodelabTheme {
        MyApp(Modifier.fillMaxSize())
    }
}
//Dark mode preview
//it just just necessary to add an additional @Preview annotation to composable with UI_MODE_NIGHT_YES
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "Dark" )
@Preview(showBackground = true, name = "GreetingsPreview preview", widthDp = 320)
@Composable
fun GreetingsPreview() {
    BasicsCodelabTheme {
        Greetings()
    }
}

@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "Dark" )
@Preview(showBackground = true, name = "GreetingPreview preview", widthDp = 320)
@Composable
fun GreetingPreview() {
    BasicsCodelabTheme {
        Greeting("Javier")
    }
}

/*
Fixed height to verify content is aligned as stated
 */
@Preview(
    showBackground = true,
    widthDp = 320,
    uiMode = UI_MODE_NIGHT_YES,
    name = "Dark" )
@Preview(showBackground = true, widthDp = 320, heightDp = 320 )
@Composable
fun OnboardingPreview(){
    BasicsCodelabTheme {
        OnboardingScreen(onContinueClicked = { })
    }
}