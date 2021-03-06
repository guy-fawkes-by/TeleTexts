package com.ibashkimi.telegram.ui.login

import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.state
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun WaitForNumberScreen(onEnter: (String) -> Unit) {
    AuthorizationScreen(
        title = "Введите номер тлф",
        message = "(НОМЕР, К КОТОРОМУ ПРИВЯЗАН ТЕЛЕГРАМ АККАУНТ). Пж, не забудьте +375, и вводите без пробелов",
        onEnter = onEnter
    )
}

@Composable
fun WaitForCodeScreen(onEnter: (String) -> Unit, onBack: (() -> Unit)?) {
    AuthorizationScreen(
        title = "Проверочный код (придет в телеграм)",
        onEnter = onEnter,
        onBack = onBack
    )
}

@Composable
fun WaitForPasswordScreen(onEnter: (String) -> Unit) {
    AuthorizationScreen(
        title = "Введите ваш пароль",
        onEnter = onEnter
    )
}

@Composable
fun WaitingForClientToInitialize(text: String, onRetry: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Грузимся...") })
        },
        bodyContent = {

                val phoneNumber = state { TextFieldValue() }
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text, modifier = Modifier.padding(16.dp))
                    Divider(
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
//                    Button(modifier = Modifier.gravity(Alignment.End).background(Color.Red), content = {
//                        Text("Retry (press if you wait too long)")
//                    }, onClick = {
//                        onRetry()
//                    })
                }

        }
    )
}

@Composable
private fun AuthorizationScreen(title: String, message: String? = null, onEnter: (String) -> Unit, onBack: (() -> Unit)? = null) {
    val executed = state { false }
    Scaffold(
        topBar = {
            TopAppBar(title = { Text(title) })
        },
        bodyContent = {
            if (executed.value) {
                CircularProgressIndicator()
            } else {
                val phoneNumber = state { TextFieldValue() }
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    TextField(
                        value = phoneNumber.value,
                        onValueChange = { phoneNumber.value = it },
                        label = { },
                        textStyle = MaterialTheme.typography.h5
                    )
                    Divider(
                        color = MaterialTheme.colors.onBackground,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (message == null) {
                        Spacer(modifier = Modifier.preferredHeight(16.dp))
                    } else {
                        Text(message, modifier = Modifier.padding(16.dp))
                    }
                    Button(modifier = Modifier.gravity(Alignment.End), content = {
                        Text("Enter")
                    }, onClick = {
                        onEnter(phoneNumber.value.text)
                        executed.value = true
                    })
//                    if (onBack !== null) {
//                        Button(modifier = Modifier.gravity(Alignment.Start), content = {
//                            Text("Back")
//                        }, onClick = {
//                            onBack()
//                            executed.value = true
//                        })
//                    }
                }
            }
        }
    )
}
