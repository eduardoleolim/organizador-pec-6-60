package org.eduardoleolim.organizadorPec660.app.main.router

import cafe.adriel.voyager.core.registry.ScreenProvider
import org.eduardoleolim.organizadorPec660.core.auth.application.AuthUserResponse

sealed class MainProvider : ScreenProvider {
    data object AuthScreen : MainProvider()
    data class HomeScreen(val user: AuthUserResponse) : MainProvider()
}
