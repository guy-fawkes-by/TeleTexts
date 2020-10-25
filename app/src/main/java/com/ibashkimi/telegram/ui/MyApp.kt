package com.ibashkimi.telegram.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.ibashkimi.telegram.*
import com.ibashkimi.telegram.R
import com.ibashkimi.telegram.data.Authentication
import com.ibashkimi.telegram.data.Repository
import com.ibashkimi.telegram.ui.chat.ChatScreen
import com.ibashkimi.telegram.ui.home.HomeScreen
import com.ibashkimi.telegram.ui.login.WaitForCodeScreen
import com.ibashkimi.telegram.ui.login.WaitForNumberScreen
import com.ibashkimi.telegram.ui.login.WaitForPasswordScreen

@Composable
fun MyApp(rootActivity: MainActivity, repository: Repository) {
    val isDark = isSystemInDarkTheme()
    MaterialTheme(
        colors = if (isDark) darkColors() else lightColors()
    ) {
        val authState = repository.client?.authState?.collectAsState(Authentication.UNKNOWN)
        android.util.Log.d("MyApp", "auth state: ${authState!!.value}")
        when (authState!!.value) {
            Authentication.UNKNOWN -> {
                Text(
                    rootActivity.getString(R.string.waiting_for_client_initialization),
                    modifier = Modifier.fillMaxWidth() + Modifier.wrapContentSize(Alignment.Center)
                )
            }
            Authentication.UNAUTHENTICATED -> {
                repository.client?.startAuthentication()
                Text(
                    "Starting authentication",
                    modifier = Modifier.fillMaxWidth() + Modifier.wrapContentSize(Alignment.Center)
                )
            }
            Authentication.WAIT_FOR_NUMBER -> {
                WaitForNumberScreen {
                    repository.client?.insertPhoneNumber(it)
                }
            }
            Authentication.WAIT_FOR_CODE -> {
                    WaitForCodeScreen ({
                        repository.client?.insertCode(it)
                    },
                    {
                        repository.client?.logOut()
                    })
            }
            Authentication.WAIT_FOR_PASSWORD -> {
                WaitForPasswordScreen {
                    repository.client?.insertPassword(it)
                }
            }
            Authentication.AUTHENTICATED -> {
                MainScreen(rootActivity, repository)
            }
        }
    }
}

@Composable
private fun MainScreen(onAppBarActionClickListener: OnAppBarActionClickListener, repository: Repository) {
    val currentScreen = Navigation.currentScreen.collectAsState()
    val destination = currentScreen.value
    val title = destination.title
    Scaffold(
        topBar = {
            if (destination == Screen.ChatList) {
                TopAppBar(title = { Text(stringResource(R.string.app_name)) }, actions = {
                    IconButton(onClick = {
                        onAppBarActionClickListener.onActionClick(R.id.action_settings)
                    }) {
                        Icon(asset = vectorResource(id = R.drawable.ic_baseline_settings_24))
                    }
                })
            } else {
                TopAppBar(
                    title = { Text(title, maxLines = 1) },
                    navigationIcon = {
                        Image(
                            modifier = Modifier.clickable(onClick = { Navigation.pop() })
                                .padding(16.dp),
                            asset = Icons.Default.ArrowBack,
                            alignment = Alignment.Center,
                            colorFilter = ColorFilter.tint(MaterialTheme.colors.onPrimary)
                        )
                    })
            }
        },
        bodyContent = {
            AppContent(repository, destination, modifier = Modifier.fillMaxWidth())
        }
    )
}

@Composable
private fun AppContent(repository: Repository, screen: Screen, modifier: Modifier = Modifier) {
    Surface(color = MaterialTheme.colors.background, modifier = modifier) {
        when (screen) {
            is Screen.ChatList -> {
                HomeScreen(repository)
            }
            is Screen.Chat -> ChatScreen(repository, screen.chat)
        }
    }
}
