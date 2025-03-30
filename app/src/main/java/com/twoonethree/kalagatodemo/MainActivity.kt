package com.twoonethree.kalagatodemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.twoonethree.kalagatodemo.navsetup.NavigationSetUp
import com.twoonethree.kalagatodemo.ui.theme.MyComposeAppTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val localDataStore: LocalDataStore by inject()
            val isDarkTheme = remember { mutableStateOf(localDataStore.get(LocalDataStore.IS_DARK_THEME, false)) }
            val primaryColor = remember { mutableStateOf(localDataStore.get(LocalDataStore.PRIMARY_COLOR, 0xFFFF8C00)) }
            MyComposeAppTheme(darkTheme = isDarkTheme.value, primaryColor = primaryColor.value) {
                Surface {
                    NavigationSetUp(isDarkTheme, primaryColor)
                }
            }
        }
    }
}