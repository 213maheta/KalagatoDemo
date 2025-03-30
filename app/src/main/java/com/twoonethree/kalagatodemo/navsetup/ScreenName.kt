package com.twoonethree.kalagatodemo.navsetup

import kotlinx.serialization.Serializable

sealed class ScreenName {
    @Serializable
    object HomeScreen : ScreenName()
    @Serializable
    object TaskAddScreen : ScreenName()
    @Serializable
    data class TaskViewScreen(val taskId:Int): ScreenName()
    @Serializable
    object SettingScreen : ScreenName()
}